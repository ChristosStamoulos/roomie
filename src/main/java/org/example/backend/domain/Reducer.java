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

public class Reducer {
    private static int serverPort;
    private static String masterHost;
    private static int masterPort;
    private static Map<Integer, ArrayList<Chunk>> chunkMap = new HashMap<>();
    private static final int expectedChunks = 3;

    private  static final  int numberOfChunks = 1;

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
    }

    public static void main(String[] args) {
        init();
        startReducerServer();
    }

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
    private static void handleWorkerRequest(Socket workerSocket){
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

    private static void sentToMaster(Chunk chunk){
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

    private static Chunk mergeChunks(ArrayList<Chunk> chunks) {
        Chunk chunk = chunks.get(0);

        return new Chunk(chunk.getUserID(), chunk.getTypeID(), chunks);
    }
}

