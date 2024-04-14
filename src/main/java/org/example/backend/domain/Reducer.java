package org.example.backend.domain;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Reducer {

    private static String masterHost;
    private static int masterPort;
    private static int workerPort;

    private static ServerSocket redSocket;
    private static Socket reducerConnection;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

    Reducer(){

    }

    public static void init(){
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/worker.config";


        try (FileInputStream f = new FileInputStream(filename)){
            prop.load(f);
        }catch (IOException exception ) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified");
        }

        Reducer.masterHost = prop.getProperty("masterHost");
        Reducer.masterPort = Integer.parseInt(prop.getProperty("masterPort"));
        Reducer.workerPort = Integer.parseInt(prop.getProperty("workerPort"));
        System.out.println(Integer.parseInt(prop.getProperty("workerPort")));
    }

    public static void main(String[] args){

        try {
            redSocket = new ServerSocket(Reducer.workerPort);

            while(true){
                reducerConnection = redSocket.accept();
                in = new ObjectInputStream(reducerConnection.getInputStream());

                try {
                    Chunk data = (Chunk) in.readObject();
                    System.out.println(data.getData().toString());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                redSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
