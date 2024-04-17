package org.example.backend.domain;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
/** Reducer Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to handle the room results from  Workers reducing them and sending them back to Master groped by their unique segment ID.
 */
public class Reducer {
    private static int serverPort;
    private static String masterHost;
    private static int masterPort;
    private static int expectedChunks;
    private static Map<Integer, ArrayList<Chunk>> chunkMap = new HashMap<>();

    private static final int numberOfChunks = 1;
    /**
     * Initialises the variables from the config file
     */
    public static void init() {
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/reducer.config";

        try (FileInputStream f = new FileInputStream(filename)) {
            prop.load(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        masterHost = prop.getProperty("masterHost");
        masterPort = Integer.parseInt(prop.getProperty("masterPort"));
        serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        expectedChunks = Integer.parseInt(prop.getProperty("expectedChunks"));
    }

    public static void main(String[] args) {
        init();
        startReducerServer();
    }
    /**
     * Opens a Reducer server to listen for requests from workers,
     * and handles those requests in handleWorkerRequest method.
     */
    private static void startReducerServer() {
        try (ServerSocket providerSocket = new ServerSocket(serverPort, 100)) {
            while (true) {
                Socket workerSocket = providerSocket.accept();
                System.out.println("Worker connected");

                new Thread(() -> handleWorkerRequest(workerSocket)).start();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Handles a request made by a worker.
     * If the amount of a specific type of request has reached its expected
     * limits, then the reduction process begins
     */
    private static void handleWorkerRequest(Socket workerSocket) {
        try {
            ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream());
            while (true) {
                Chunk chunk = (Chunk) in.readObject(); // Read chunks sent by the worker
                // Add the chunk to the map based on its ID
                synchronized (chunkMap) {
                    int chunkId = chunk.getSegmentID();
                    if (!chunkMap.containsKey(chunkId)) {
                        chunkMap.put(chunkId, new ArrayList<>());
                    }
                    chunkMap.get(chunkId).add(chunk);

                    if (chunkMap.get(chunkId).size() == expectedChunks) {
                        ArrayList<Chunk> chunks = chunkMap.get(chunkId);
                        Chunk mergedChunk = mergeChunks(chunks);
                        sentToMaster(mergedChunk);

                        chunkMap.remove(chunkId); // Clear the chunks from the map
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * The reducer acts as a client to master,
     * and now it's time to send back the reduced
     * form of data that he has received
     */
    private static void sentToMaster(Chunk chunk) {
        try {
            Socket masterSocket = new Socket(masterHost, masterPort); // Connect to the master
            ObjectOutputStream out = new ObjectOutputStream(masterSocket.getOutputStream());

            out.writeObject(chunk); // Send the merged chunk to the master
            out.flush();

            // Close connections
            out.close();                                    ///////////////////////////////////////////////////////////////////////
            masterSocket.close();                           ///////////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     Helper method for handleWorkerRequest.
     Returns a final chunk that contains a list of merged data
     This list contains all the data gathered by a group of specific requests
     */
    private static Chunk mergeChunks(ArrayList<Chunk> chunks) {
        String userID = chunks.get(0).getUserID();
        int id = chunks.get(0).getSegmentID();
        int type = chunks.get(0).getSegmentID();

        ArrayList<Room> workerChunk = new ArrayList<>();
        ArrayList<Room> finalList = new ArrayList<>();
        for (int i = 0; i < expectedChunks; i++) {
            workerChunk = (ArrayList<Room>) chunks.get(i).getData();
            for (int j = 0; j < workerChunk.size(); j++) {
                finalList.add(workerChunk.get(j));
            }
        }
        Chunk finalChunk = new Chunk(userID, type, finalList);
        finalChunk.setSegmentID(id);
        return finalChunk;
    }
}

