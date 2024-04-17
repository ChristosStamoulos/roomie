package org.example.backend.domain;

import org.example.backend.utils.Pair;
import org.example.backend.utils.json.JsonConverter;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


/** Master Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to handle requests from the User, schedules them with mapping to Workers,
 * receives the results from the Reducer and processes the results back to User.
 */
public class Master {
    private static int numOfWorkers;                      // Number of Worker nodes
    private static String host1;                          // Hostname of the first Worker node
    //private static String host2;                        // Hostname of the second Worker node
    //private static String host3;                        // Hostname of the third  Worker node
    private static int worker1Port;                       // Port number of the first Worker node
    //private static int worker2Port;                     // Port number of the second Worker node
    //private static int worker3Port;                     // Port number of the third Worker node
    private static int userPort;                          // Port number for User connections
    private static int reducerPort;                       // Port number for Reducer connections
    private static ArrayList<ObjectOutputStream> workers; // List of output streams to Worker nodes
    private static Map<Integer, Socket> userSockets;      // Map of User sockets with segment IDs
    private static int segmentIdCount;                    // Counter for segment IDs
    private static ArrayList<Room> rooms;                 // List of rooms
    private static JsonConverter jsonConverter;           // JSON converter for room objects

    /**
     * Initializes the useful data of Master
     */
    public static void init() {
        // Load configuration from file
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/master.config";

        try (FileInputStream f = new FileInputStream(filename)) {
            prop.load(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Read properties from the configuration file
        host1 = prop.getProperty("host1");
        //host2 = prop.getProperty("host2");
        //host3 = prop.getProperty("host3");
        worker1Port = Integer.parseInt(prop.getProperty("worker1Port"));
        //worker2Port = Integer.parseInt(prop.getProperty("worker2Port"));
        //worker3Port = Integer.parseInt(prop.getProperty("worker3Port"));
        numOfWorkers = Integer.parseInt(prop.getProperty("numberOfWorkers"));
        userPort = Integer.parseInt(prop.getProperty("userPort"));
        reducerPort = Integer.parseInt(prop.getProperty("reducerPort"));
        segmentIdCount = 0;

        // Initialize JSON converter and retrieve rooms
        jsonConverter = new JsonConverter();
        rooms = jsonConverter.getRooms();

        // Initialize list of Worker sockets
        workers = new ArrayList<>();
    }

    /**
     * Main class of Master
     * @param args Default parameters
     */
    public static void main(String[] args) {
        // Initialize the system components
        init();

        // Start listening for User requests
        startClientSocketThread();

        // Start requesting to send chunks to Workers
        startWorkerSocketThread();

        // Start listening for results from Reducer
        startReducerSocketThread();
    }

    /**
     * Starts a thread to listen for incoming User connections on the specified port.
     * When a User connects, a new thread is created to handle the User's request.
     */
    private static void startClientSocketThread() {
        new Thread(() -> {
            try (ServerSocket clientSocket = new ServerSocket(userPort)) {
                // Listen for incoming User connections
                while (true) {
                    Socket connectionSocket = clientSocket.accept();
                    System.out.println("Client connected");
                    // Handle the request in a new thread
                    new Thread(() -> handleClientRequest(connectionSocket)).start();
                }
            } catch (IOException e) {
                // Print any exceptions that occur during the server socket initialization or client connection handling
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Handles a client request by reading a Chunk object from the provided socket's input stream (request),
     * setting its segment ID (unique), processing the request (to Workers),
     * and updating the userSockets map (for returning the result back to the right User).
     *
     * @param userSocket The socket connected to the client.
     */
    private static void handleClientRequest(Socket userSocket) {
        try{
            ObjectInputStream in = new ObjectInputStream(userSocket.getInputStream());
            Chunk data = (Chunk) in.readObject();
            System.out.println(data.getData().toString());
            // Increment the segmentIdCount and assign it to the Chunk object
            data.setSegmentID(++segmentIdCount);
            // Store the userSocket in the userSockets map with the segment ID as the key
            userSockets.put(segmentIdCount, userSocket);
            // Process the request based on the type ID in the Chunk object
            processRequest(data.getTypeID(), data);
            System.out.println("Request processed successfully.");

        } catch (IOException | ClassNotFoundException e) {
            // Print any exceptions that occur during the request handling process
            e.printStackTrace();
        }
    }

    /**
     * Starts a thread to handle communication with worker nodes. It connects to the Workers,
     * creates output streams, and adds them to the list of workers.
     *
     * Since Master connects to multiple Workers, each Worker connection and output stream
     * are created separately.
     */
    private static void startWorkerSocketThread() {
        new Thread(() -> {
            try {
                // Create a Worker socket and connect to the Worker
                Socket workerSocket1 = new Socket(host1, worker1Port);
                //Socket workerSocket2 = new Socket(host2, worker2Port);
                //Socket workerSocket3 = new Socket(host3, worker3Port);
                ObjectOutputStream outWorker1 = new ObjectOutputStream(workerSocket1.getOutputStream());
                //ObjectOutputStream outWorker2 = new ObjectOutputStream(workerSocket2.getOutputStream());
                //ObjectOutputStream outWorker3 = new ObjectOutputStream(workerSocket3.getOutputStream());

                // Add the output stream to the list of Workers
                synchronized (workers) {
                    workers.add(outWorker1);
                    //workers.add(outWorker2);
                    //workers.add(outWorker3);
                }
                splitRooms();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Starts a thread to listen for connections from the Reducer. It creates a server socket
     * and accepts connections from the Reducer. For each connection, it starts a new thread
     * to handle the Reducer response.
     */
    private static void startReducerSocketThread(){
        new Thread(() -> {
            try {
                // Create a server socket to listen for Reducer connections
                ServerSocket reducerServerSocket = new ServerSocket(reducerPort);
                while (true) {
                    Socket reducerSocket = reducerServerSocket.accept();
                    System.out.println("Reducer connected");
                    // Handle Reducer response in a new thread
                    new Thread(() -> handleReducerResponse(reducerSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Handles the response from the Reducer. It reads the response chunk from the input stream
     * of the Reducer socket and processes the result chunk.
     *
     * @param reducerSocket The socket connected to the Reducer.
     */
    private static void handleReducerResponse(Socket reducerSocket) {
        try {
            // Read Reducer response
            ObjectInputStream in = new ObjectInputStream(reducerSocket.getInputStream());
            Chunk resultChunk = (Chunk) in.readObject();
            // Process the result chunk
            processResultsFromReducer(resultChunk);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the result chunk received from the Reducer.
     * If the corresponding User socket is found, it sends the result chunk to the user.
     * If the User socket is not found, it prints an error message (avoids environment errors).
     *
     * @param result The result chunk received from the Reducer.
     */
    private static void processResultsFromReducer(Chunk result) {
        int segmentId = result.getSegmentID();
        // Retrieve the User socket associated with the segment ID
        Socket userSocket = userSockets.get(segmentId);
        // Check if the User socket is found
        if (userSocket != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(userSocket.getOutputStream())) {
                out.writeObject(result);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Print an error message if the User socket is not found for the segment ID
            System.out.println("User socket not found for segment ID: " + segmentId);
        }
    }

    /**
     * Processes the request based on the given chunk type.
     *
     * @param type The type of the request.
     * @param chunk The chunk containing the request data.
     */
    private static void processRequest(int type, Chunk chunk){
        switch (type){
            case 1, 6, 7:
                // Iterate all workers
                for(int i=0; i<numOfWorkers; i++) {
                    try {
                        workers.get(i).writeObject(chunk);
                        workers.get(i).flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(chunk.getData());
                }
                break;
            case 2, 3, 5:
                // Extract room ID and associated data from the chunk
                Pair<Integer, ArrayList<String>> pair = (Pair<Integer, ArrayList<String>>) chunk.getData();
                int roomID = pair.getKey();
                // Hash the room ID to determine the Worker index
                int w1 = hashRequestToWorker(roomID, numOfWorkers);
                try{
                    workers.get(w1).writeObject(chunk);
                    workers.get(w1).flush();
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            case 4:
                // Extract data from the chunk and convert it to room object
                JSONObject data1 = new JSONObject( (String) chunk.getData());
                Room room1 = jsonConverter.convertToRoom(data1);
                int roomID1 = room1.getId();
                // Hash the room ID to determine the Worker index
                int w3 = hashRequestToWorker(roomID1, numOfWorkers);
                try{
                    Chunk c = new Chunk("i", 4, data1);
                    workers.get(w3).writeObject(c);
                    workers.get(w3).flush();
                    System.out.println("eleni");
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            // Default case: Throw an exception for unexpected request types
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    /**
     * Creates a hashing code from the room ID to determine the worker that should handle the request.
     *
     * @param roomID The ID of the room.
     * @param numOfWorkers The total number of workers.
     * @return The index of the worker that should handle the request.
     */
    public static int hashRequestToWorker(int roomID, int numOfWorkers) {
        // Modulo operation to map the segment ID to a Worker
        return roomID % numOfWorkers;
    }

    /**
     * Splits the rooms and assigns each room to a worker based on its ID.
     * Used for the basic initialization rooms assigned to workers.
     */
    private static void splitRooms() {
        for (Room room : rooms) {
            // Synchronize on the Workers list to avoid concurrent modification
            synchronized (workers) {
                // Calculate the Worker index for the current room
                int workerIndex = hashRequestToWorker(room.getId(), numOfWorkers);
                try {
                    // Send the room to the corresponding Worker as a Chunk object with chunk type ID 4
                    workers.get(workerIndex).writeObject(new Chunk("", 4, room));
                } catch (IOException e) {
                    // If an IOException occurs, wrap it in a RuntimeException and throw
                    throw new RuntimeException(e);
                }
            }
        }
    }
}


