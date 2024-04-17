package org.example.backend.domain;

import org.example.backend.utils.Pair;
import org.example.backend.utils.json.JsonConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import static java.lang.Math.abs;

/** Master Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to handle requests from the User, schedules them for mapping to Workers
 * and processes the results back to User.
 */
public class Master {
    private static int numOfWorkers;
    private static String host1;
    private static String host2;
    private static String host3;
    private static int worker1Port;
    private static int worker2Port;
    private static int worker3Port;
    private static int userPort;
    private static int reducerPort;
    private static ArrayList<ObjectOutputStream> workers;
    private static Map<Integer, Socket> userSockets = new HashMap<>();
    private static int segmentIdCount = 0;
    private static ArrayList<Room> rooms;
    private static JsonConverter jsonConverter;

    public static void init() {
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/master.config";

        try (FileInputStream f = new FileInputStream(filename)) {
            prop.load(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        host1 = prop.getProperty("host1");
        host2 = prop.getProperty("host2");
        host3 = prop.getProperty("host3");
        worker1Port = Integer.parseInt(prop.getProperty("worker1Port"));
        worker2Port = Integer.parseInt(prop.getProperty("worker2Port"));
        worker3Port = Integer.parseInt(prop.getProperty("worker3Port"));
        numOfWorkers = Integer.parseInt(prop.getProperty("numberOfWorkers"));
        userPort = Integer.parseInt(prop.getProperty("userPort"));
        reducerPort = Integer.parseInt(prop.getProperty("reducerPort"));

        jsonConverter = new JsonConverter();
        rooms = jsonConverter.getRooms();

        workers = new ArrayList<>();
    }

    public static void main(String[] args) {
        init();

        // Start listening for user requests
        startClientSocketThread();

        // Start requesting to send chunks from workers
        startWorkerSocketThread();

        // Start listening for results from reducer
        startReducerSocketThread();

    }

    private static void startClientSocketThread() {
        new Thread(() -> {
            try (ServerSocket clientSocket = new ServerSocket(userPort)) {
                while (true) {
                    Socket connectionSocket = clientSocket.accept();
                    System.out.println("Client connected");
                    // Handle the request in a separate thread
                    new Thread(() -> handleClientRequest(connectionSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void handleClientRequest(Socket userSocket) {
        try (ObjectInputStream in = new ObjectInputStream(userSocket.getInputStream())) {

            Chunk data = (Chunk) in.readObject();
            System.out.println(data.getData().toString());

            data.setSegmentID(segmentIdCount++);
            userSockets.put(segmentIdCount, userSocket);

            processRequest(data.getTypeID(), data);
            System.out.println("Request processed successfully.");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                userSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void startWorkerSocketThread() {
        new Thread(() -> {
            try {
                // Create a worker socket and connect to the worker
                Socket workerSocket = new Socket(host1, worker1Port);
                ObjectOutputStream outWorker = new ObjectOutputStream(workerSocket.getOutputStream());

                // Add the output stream to the list of workers
                synchronized (workers) {
                    workers.add(outWorker);
                }
                splitRooms();

                // Keep the worker socket thread running to handle communication with the worker
                /*while (true) {
                    // Accept requests from the worker
                    try (ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream())) {
                        Chunk data = (Chunk) in.readObject();
                        // Process the request
                        processRequest(data.getTypeID(), data);
                        System.out.println("Request processed successfully.");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void startReducerSocketThread(){
        new Thread(() -> {
            try (ServerSocket reducerServerSocket = new ServerSocket(reducerPort)) {
                while (true) {
                    Socket reducerSocket = reducerServerSocket.accept();
                    System.out.println("Reducer connected");
                    // Handle reducer response in a separate thread
                    new Thread(() -> handleReducerResponse(reducerSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void handleReducerResponse(Socket reducerSocket) {
        try {
            // Read reducer response
            ObjectInputStream in = new ObjectInputStream(reducerSocket.getInputStream());
            Chunk resultChunk = (Chunk) in.readObject();
            //in.close(); /////////////////////////////////////////////////////////////////////////////TEST//////////////////////////////////////////////////////

            processResultsFromReducer(resultChunk);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void processResultsFromReducer(Chunk result) {
        int segmentId = result.getSegmentID();
        Socket userSocket = userSockets.get(segmentId);
        if (userSocket != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(userSocket.getOutputStream())) {
                out.writeObject(result);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("User socket not found for segment ID: " + segmentId);
        }
    }

    private static void processRequest(int type, Chunk chunk){
        switch (type){
            case 1, 6, 7:
                for(int i=0; i<numOfWorkers; i++) {
                    try {
                        workers.get(i).writeObject(chunk);
                        workers.get(i).flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(chunk.getData());
                }
                break;
            case 2, 3, 5:
                Pair<Integer, ArrayList<String>> pair = (Pair<Integer, ArrayList<String>>) chunk.getData();
                int roomID = pair.getKey();
                int w1 = hashRequestToWorker(roomID, numOfWorkers);
                try{
                    workers.get(w1).writeObject(chunk);
                    workers.get(w1).flush();
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            case 4:
                JSONObject data1 = new JSONObject( (String) chunk.getData());
                Room room1 = jsonConverter.convertToRoom(data1);
                int roomID1 = room1.getId();
                int w3 = hashRequestToWorker(roomID1, numOfWorkers);
                try{
                    Chunk c = new Chunk("i", 3, (String)data1.toString());
                    workers.get(w3).writeObject(c);
                    workers.get(w3).flush();
                    System.out.println("eleni");
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public static int hashRequestToWorker(int roomID, int numOfWorkers) {
        // Modulo operation to map the segment ID to a worker index
        return roomID % numOfWorkers;
    }

    private static void splitRooms(){
        for(Room r: rooms){
            synchronized (workers){
                System.out.println(workers.size());
                int wID = r.getName().hashCode() % Master.numOfWorkers;
                try {
                    workers.get(wID).writeObject(new Chunk("", 4, r));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}


