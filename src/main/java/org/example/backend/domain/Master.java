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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Properties;

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
    private static ArrayList<ObjectOutputStream> workers;
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

        jsonConverter = new JsonConverter();
        rooms = jsonConverter.getRooms();

        workers = new ArrayList<>();
    }

    public static void main(String[] args) {
        init();

        startWorkerSocketThread();
        startClientSocketThread();
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

    private static void handleClientRequest(Socket connectionSocket) {
        try (ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream())) {

            Chunk data = (Chunk) in.readObject();
            data.setSegmentID(segmentIdCount++);
            processRequest(data.getTypeID(), data);
            System.out.println("Request processed successfully.");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                connectionSocket.close();
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

                // Keep the worker socket thread running to handle communication with the worker
                while (true) {
                    // Accept requests from the worker
                    try (ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream())) {
                        Chunk data = (Chunk) in.readObject();
                        // Process the request
                        processRequest(data.getTypeID(), data);
                        System.out.println("Request processed successfully.");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private static void processRequest(int type, Chunk chunk){
        switch (type){
            case 1:
                System.out.println("Arxi process");
                try{
                    for(int i=0; i<numOfWorkers; i++) {
                        workers.get(i).writeObject(chunk);
                        workers.get(i).flush();
                        System.out.println("I am process for" + i);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            case 2:
                break;
            case 3:
                JSONObject data1 = new JSONObject( (String) chunk.getData());
                Room room1 = jsonConverter.convertToRoom(data1);
                int w1 = 0;//findWorkerID(room);
                try{
                    workers.get(w1).writeObject(new Chunk("i", segmentIdCount++, (String)data1.toString()));
                    workers.get(w1).flush();
                    System.out.println("eleni");
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            case 4:
                Pair<Integer, ArrayList<String>> pair = (Pair<Integer, ArrayList<String>>) chunk.getData();
                int roomID = pair.getKey();
                ArrayList<String> dates = pair.getValue();
                int wID = 0;//findWorkerID(rooms.get(roomID));
                try{
                    workers.get(wID).writeObject(pair);
                    workers.get(wID).flush();
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            case 5:
                break;
        }
    }
}


