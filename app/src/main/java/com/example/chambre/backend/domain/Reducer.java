package com.example.chambre.backend.domain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** Reducer Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to handle the room results from Workers reducing them and sending them back to Master groped by their unique segment ID.
 */
public class Reducer {
    private static int serverPort;       //The port number on which the server listens for incoming connections from Workers.
    private static String masterHost;    //The hostname of the Master server.
    private static int masterPort;       //The port number of the Master server.
    private static int expectedChunks;   //The number of expected chunks to be received before processing.
    //A map to store chunks received from Workers based on their segment IDs.
    private static Map<Integer, ArrayList<Chunk>> chunkMap = new HashMap<>();
    //The number of chunks to be processed concurrently.
    private static final int numberOfChunks = 1;

    /**
     * Initialises the variables from the config file
     */
    public static void init() {
        // Load configuration from file
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/reducer.config";

        try (FileInputStream f = new FileInputStream(filename)) {
            prop.load(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Read properties from the configuration file
        masterHost = prop.getProperty("masterHost");
        masterPort = Integer.parseInt(prop.getProperty("masterPort"));
        serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        expectedChunks = Integer.parseInt(prop.getProperty("expectedChunks"));
    }

    /**
     * Main class of Reducer
     * @param args Default parameters
     */
    public static void main(String[] args) {
        // Initialize the system components
        init();
        // Start the reducer server
        startReducerServer();
    }

    /**
     * Starts the Reducer server, which listens for incoming Worker connections.
     */
    private static void startReducerServer() {
        try (ServerSocket providerSocket = new ServerSocket(serverPort, 100)) {
            // Continuously accept incoming worker connections
            while (true) {
                Socket workerSocket = providerSocket.accept();
                System.out.println("Worker connected");

                // Handle each worker request in a separate thread
                new Thread(() -> handleWorkerRequest(workerSocket)).start();
            }
        } catch (IOException ex) {
            // If an exception occurs, wrap it in a RuntimeException and throw it
            throw new RuntimeException(ex);
        }
    }

    /**
     * Handles a Worker request by reading chunks sent by the Worker.
     *
     * @param workerSocket The socket connected to the Worker.
     */
    private static void handleWorkerRequest(Socket workerSocket) {
        try {
            ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream());
            while (true) {
                Chunk chunk = (Chunk) in.readObject(); // Read chunks sent by the Worker
                // Add the chunk to the map based on its ID
                synchronized (chunkMap) {
                    int chunkId = chunk.getSegmentID();
                    if (!chunkMap.containsKey(chunkId)) {
                        chunkMap.put(chunkId, new ArrayList<>());
                    }
                    chunkMap.get(chunkId).add(chunk);

                    // Check if all expected chunks are received
                    if (chunkMap.get(chunkId).size() == expectedChunks) {
                        ArrayList<Chunk> chunks = chunkMap.get(chunkId);
                        // Merge chunks into a single chunk
                        Chunk mergedChunk = mergeChunks(chunks);
                        // Send merged chunk to the Master
                        sentToMaster(mergedChunk);
                        // Clear the chunks from the map
                        chunkMap.remove(chunkId); // Clear the chunks from the map
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }
    }

    /**
     * Sends the given chunk to the Master.
     *
     * @param chunk The chunk to be sent to the Master.
     */
    private static void sentToMaster(Chunk chunk) {
        try {
            Socket masterSocket = new Socket(masterHost, masterPort); // Connect to the master
            ObjectOutputStream out = new ObjectOutputStream(masterSocket.getOutputStream());

            out.writeObject(chunk); // Send the merged chunk to the master
            out.flush();

            // Close connection
            out.close();
            masterSocket.close();
        } catch (IOException e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }
    }

    /**
     * Merges the list of chunks into a single chunk.
     *
     * @param chunks The list of chunks to be merged.
     * @return The merged chunk containing the combined list of rooms.
     */
    private static Chunk mergeChunks(ArrayList<Chunk> chunks) {
        // Extract userID, segmentID, and type from the first chunk
        String userID = chunks.get(0).getUserID();
        int id = chunks.get(0).getSegmentID();
        int type = chunks.get(0).getSegmentID();

        ArrayList<Room> workerChunk = new ArrayList<>();
        ArrayList<Room> finalList = new ArrayList<>();
        // Iterate over each chunk to extract the list of rooms and add them to the final list
        for (int i = 0; i < expectedChunks; i++) {
            workerChunk = (ArrayList<Room>) chunks.get(i).getData();
            for (int j = 0; j < workerChunk.size(); j++) {
                finalList.add(workerChunk.get(j));
            }
        }
        // Create a new chunk with the merged list of rooms and set the userID, segmentID, and type
        Chunk finalChunk = new Chunk(userID, type, finalList);
        finalChunk.setSegmentID(id);
        return finalChunk;
    }
}

