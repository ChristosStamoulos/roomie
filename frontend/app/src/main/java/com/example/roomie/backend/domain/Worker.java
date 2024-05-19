package com.example.roomie.backend.domain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

/** Worker Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to handle requests from the Master and to map the data assigned from the Master.
 */

public class Worker {
    private static int id = 0;
    private static int serverPort;
    private static ArrayList<Room> rooms;

    /**
     * Initialises the variables from the config file
     */
    public static void init() {
        Properties prop = new Properties();
        String filename = "app\\src\\main\\java\\com\\example\\roomie\\backend\\config\\worker.config";

        try (FileInputStream f = new FileInputStream(filename)) {
            prop.load(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        rooms = new ArrayList<Room>();
    }

    public static void main(String[] args) {
        init();
        openServer();
    }

    /**
     * Opens Worker server to listen for requests from master,
     * and handles those requests in ActionsForWorkers
     */
    private static void openServer() {
        try (ServerSocket providerSocket = new ServerSocket(serverPort, 100);
             ) {

            while (true) {
                Socket masterConnection = providerSocket.accept();
                System.out.println("Master connected");
                ObjectInputStream in = new ObjectInputStream(masterConnection.getInputStream());

                try {
                    while(!Thread.currentThread().isInterrupted()) {
                        Chunk data = (Chunk) in.readObject();
                        if(data.getTypeID() == 4){
                            rooms.add((Room) data.getData());
                        }else {
                            Thread worker = new ActionsForWorkers(data, rooms);
                            worker.start();
                        }
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
