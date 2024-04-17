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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Reducer {
    private static ServerSocket providerSocket;
    private static int serverPort;
    private static String masterHost;
    private static int masterPort;
    private static ArrayList<ArrayList<Room>> roomsOfSameRequest = new ArrayList<ArrayList<Room>>();
    private static ArrayList<Room> mergedRooms = new ArrayList<Room>();
    private static HashMap<Integer,Integer> searches = new HashMap<Integer,Integer>();

    private  static final  int numberOfChunks = 3;

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
            int conectionCaunter = 0;
            while (true) {
                Socket workerConnection = providerSocket.accept();
                System.out.println("Worker connected");
                conectionCaunter++;
                try (ObjectInputStream in = new ObjectInputStream(workerConnection.getInputStream())) {
                    Chunk data = (Chunk) in.readObject();
                    if(!searches.containsKey(data.getSegmentID())){
                        searches.put(data.getSegmentID(),0);
                    }else{
                        int sum = searches.get((data.getSegmentID()));
                        sum++;
                        searches.put((data.getSegmentID()) ,sum);
                    }
                    reduce(data);
                    System.out.println(data.getData().toString());
                    System.out.println(data.getSegmentID());

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void reduce(Chunk payLoadToReduce) {

        if (searches.get((payLoadToReduce.getSegmentID()))<numberOfChunks) {
            roomsOfSameRequest.add((ArrayList<Room>) payLoadToReduce.getData());
        } else {

            for (ArrayList<Room> workerRooms : roomsOfSameRequest) {
                for (Room room : workerRooms) {
                    Room roomOfInterst = room;
                    if (mergedRooms.isEmpty()) {
                        mergedRooms.add(roomOfInterst);
                    } else {
                        if (!mergedRooms.contains(roomOfInterst)) {
                            mergedRooms.add(roomOfInterst);
                        }
                    }
                }
            }
            sentBackToMaster();
        }

    }



    public static void sentBackToMaster(){
        //try {
            //Socket masterSocket = new Socket(masterHost,masterPort);
           // ObjectOutputStream out = new ObjectOutputStream(masterSocket.getOutputStream());
            System.out.println("my  final list size: "+mergedRooms.size());
            mergedRooms.clear();
            roomsOfSameRequest.clear();
       // } catch (UnknownHostException e) {
           // throw new RuntimeException(e);
        //} catch (IOException e) {
          //  throw new RuntimeException(e);
        //}
    }

}

