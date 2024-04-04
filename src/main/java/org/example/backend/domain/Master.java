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
public class Master{
    public static int num_of_workers;
    private static String host;
    private static int workerPort;
    private static int ReducerPort;
    private static int userPort;
    private static Object userInput=null;
    private static ArrayList<Room> rooms;
    private static JsonConverter jsonConverter;
    private static ArrayList<ObjectOutputStream> workers;

    public static void init(){
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/master.config";


        try (FileInputStream f = new FileInputStream(filename)){
            prop.load(f);
        }catch (IOException exception ) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified");
        }

        Master.host = prop.getProperty("host");
        Master.workerPort = Integer.parseInt(prop.getProperty("workerPort"));
        Master.ReducerPort = Integer.parseInt(prop.getProperty("reducerPort"));
        Master.num_of_workers = Integer.parseInt(prop.getProperty("numberOfWorkers"));
        Master.userPort = Integer.parseInt(prop.getProperty("userPort"));
        System.out.println(Integer.parseInt(prop.getProperty("workerPort")));

        jsonConverter = new JsonConverter();
        rooms = jsonConverter.getRooms();

        Master.workers = new ArrayList<ObjectOutputStream>();
    }


    public Pair<ArrayList<Chunk>,Integer> splitFilterData(Chunk chunk){
        ArrayList<Chunk> chunks = new ArrayList<>();
        String data = (String) chunk.getData();
        JSONObject filter = new JSONObject(data);
        JSONObject roomData = filter.getJSONObject(String.valueOf(filter.keySet().toString().replace("[", "").replace("]", "")));

        int i =0;
        for(String key : roomData.keySet()){
            i++;
            Object pair = new Pair<String,Object>(key,roomData.get(key));

            chunks.add(new Chunk("i", i, chunk.getTypeID(), pair));

        }


        return new Pair<ArrayList<Chunk>,Integer>(chunks,chunk.getLenght());

    }

    public ArrayList<Pair<Chunk,Integer>> map(Pair<ArrayList<Chunk>,Integer> splt){

        ArrayList<Pair<Chunk,Integer>> maper = new ArrayList<>();
        for(Chunk chunkaki : splt.getKey()){
            Pair<Chunk,Integer> map = new Pair<Chunk,Integer>(chunkaki ,splt.getValue());
            maper.add(map);
        }
        return  maper;
    }



    public int findWorkerID(Room room){

           int  workerId =room.getName().hashCode()%num_of_workers;

        return abs(workerId);
    }

    private static void processRequest(int type, Chunk chunk){
        switch (type){
            case 1:

                break;
            case 2:
                break;
            case 3:
                JSONObject data = new JSONObject( (String) chunk.getData());
                Room room = jsonConverter.convertToRoom(data);
                int w = 0;//findWorkerID(room);
                try{
                    workers.get(w).writeObject((String)data.toString());
                    workers.get(w).flush();
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

    public static void main(String[] args) {

        Master master = new Master();

        master.init();

        try {
            ServerSocket clientSocket = new ServerSocket(Master.userPort);

            Thread client = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket connectionSocket = clientSocket.accept();
                        System.out.println("Client connected");
                        try{
                            ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());
                            ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
                            Chunk data = (Chunk) in.readObject();

                            processRequest(data.getTypeID() , (Chunk) data);

                            System.out.println(data.getData().toString());
                            System.out.println(data.getTypeID());

                            Chunk c1 = new Chunk("i", 2, 0, "heyyyyyy");
                            out.writeObject(c1);
                            out.flush();

                        }catch (ClassNotFoundException e){
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            client.start();

            Thread worker = new Thread(() -> {
                Socket workerSocket = null;
                int i =0 ;
                while (!Thread.currentThread().isInterrupted()) {

                    try{
                         workerSocket = new Socket(Master.host, Master.workerPort);
                    }catch(UnknownHostException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    ObjectOutputStream outWorker=null;
                    ObjectInputStream inWorker=null;
                    try {
                        //outWorker = new ObjectOutputStream(workerSocket.getOutputStream());
                        workers.add(new ObjectOutputStream(workerSocket.getOutputStream()));
                        //inWorker = new ObjectInputStream(workerSocket.getInputStream());
                        //System.out.println(inWorker.readUTF());
                    } catch (IOException e) {
                        //System.out.println("heyyyyyyyy");
                        e.printStackTrace();
                    }
                    h();
                }
            });
            worker.start();

        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public static void handleWorkers(){
        synchronized (workers) {
            //workers.add(out);
            if (workers.size() >= Master.num_of_workers) workers.notifyAll();
        }
    }

    public static void h(){
        try {
            synchronized (workers){
                while (workers.size() < Master.num_of_workers){
                    System.err.println("Master | Waiting for workers to connect...");
                    workers.wait();
                }
                System.err.println("Master | All workers connected!");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


