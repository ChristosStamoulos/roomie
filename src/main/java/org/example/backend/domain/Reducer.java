package org.example.backend.domain;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Reducer {
    private static ServerSocket providerSocket;
    private static int serverPort;
    private static String masterHost;
    private static int masterPort;

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
                Socket workerConnection = providerSocket.accept();
                System.out.println("Worker connected");
                try (ObjectInputStream in = new ObjectInputStream(workerConnection.getInputStream())) {
                    Chunk data = (Chunk) in.readObject();
                    System.out.println(data.getData().toString());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

