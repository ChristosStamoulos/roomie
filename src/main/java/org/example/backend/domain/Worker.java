package org.example.backend.domain;

import org.example.backend.utils.Pair;
import org.example.backend.utils.SimpleCalendar;
import org.example.backend.utils.json.JsonConverter;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
    private final int id;
    private static int masterPort;
    private static int reducerPort;
    private static int serverPort;
    private static ArrayList<Room> rooms;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    static ServerSocket providerSocket;
    static Socket masterConnection = null;
    private static Socket reducerSocket = null;
    private static String reducerHost;
    private static JsonConverter jsonConverter;

    Worker(int id){
        this.id = id;
        this.rooms = new ArrayList<Room>();
    }

    public static void init(){
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/worker.config";


        try (FileInputStream f = new FileInputStream(filename)){
            prop.load(f);
        }catch (IOException exception ) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified");
        }

        Worker.serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        Worker.masterPort = Integer.parseInt(prop.getProperty("masterPort"));
        Worker.reducerPort = Integer.parseInt(prop.getProperty("reducerPort"));
        Worker.reducerHost = prop.getProperty("reducerHost");
        System.out.println(Integer.parseInt(prop.getProperty("serverPort")));
        jsonConverter = new JsonConverter();
    }


    /**
     * Adds room to the workers "memory"
     * @param room a Room object
     */
    private void addRoom(Room room){
        if(!rooms.contains(room)){
            rooms.add(room);
        }
    }

    public static void main(String[] args) {
        init();
        //openServer();


            try {
                providerSocket = new ServerSocket(Worker.serverPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //reducerSocket = new Socket(Worker.reducerHost, Worker.reducerPort);


            Thread master = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        masterConnection = providerSocket.accept();
                        in = new ObjectInputStream(masterConnection.getInputStream());
                        System.out.println("Master connected");
                        try {
                            System.out.println(in.readObject().toString());
                            Chunk data = (Chunk) in.readObject();
                            System.out.println(data.getData().toString());
                            reducerSocket = new Socket(Worker.reducerHost, Worker.reducerPort);
                            if (data.getTypeID() == 1) {
                                rooms.add(jsonConverter.convertToRoom(new JSONObject(data.getData())));
                            } else {
                                Thread workerThread = new ActionsForWorkers(data, rooms, reducerSocket);//,reducerSocket);
                                workerThread.start();
                            }

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            master.start();

//            Thread reducer = new Thread(() -> {
//                while (!Thread.currentThread().isInterrupted()) {
//
//                    try {
//                        reducerSocket = new Socket(Worker.reducerHost, Worker.reducerPort);
//                        ObjectOutputStream ou = new ObjectOutputStream(reducerSocket.getOutputStream());
//                        ou.writeObject(new Chunk("i", 3, new ArrayList<>()));
//                        ou.flush();
//
//                    } catch (UnknownHostException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            reducer.start();

    }

    static void openServer() {
        try {
            providerSocket = new ServerSocket(Worker.serverPort);
            //reducerSocket = new Socket(Worker.reducerHost, Worker.reducerPort);
            while (true) {
                masterConnection = providerSocket.accept();
                System.out.println("hhhhhhhh");
                in = new ObjectInputStream(masterConnection.getInputStream());

                try {
                    while(!Thread.currentThread().isInterrupted()) {

                        System.out.println(in.readObject().toString());
                        Chunk data = (Chunk) in.readObject();
                        System.out.println(data.getData().toString());
                        if(data.getTypeID() == 1){
                            rooms.add(jsonConverter.convertToRoom(new JSONObject( data.getData())));
                        }else {
                            //Thread workerThread = new ActionsForWorkers(data, rooms);//,reducerSocket);
                            //workerThread.start();
                        }
                        //System.out.println("Worker #" + id + " assigned data: " + data);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                //System.out.println("oups here");
//                Thread t = new ActionsForClients(masterConnection);
//                t.start();

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
