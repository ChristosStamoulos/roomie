package org.example.backend.domain;

import org.example.backend.utils.Pair;
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
import java.util.Objects;
import java.util.Properties;

import static java.lang.Math.abs;

/** Master Class
 *
 * @authors Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024
 *
 * This class is implemented to handle requests from the User, schedules them for mapping to Workers
 * and processes the results back to User.
 */

public class Master extends Thread{
    public static int num_of_workers;
    private static String host;
    private static int workerPort;
    private static int ReducerPort;
    private static int userPort;

    private static Object userInput=null;

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
    }

    @Override
    public void run() {

        ObjectInputStream inUser = null;
        ServerSocket providerSocket = null;
        Socket userConnection = null;


        try {
            providerSocket = new ServerSocket(Master.userPort, 10);
            while (true) {

                userConnection = providerSocket.accept();
                inUser = new ObjectInputStream(userConnection.getInputStream());
                try {
                    userInput = inUser.readObject();
                    Chunk chunk = (Chunk) userInput;
                    String strObject = (String) chunk.getData();
                    JSONObject jsonObject = new JSONObject(strObject);
                    System.out.println(jsonObject.toString());
                    String keyOfInterest = "roomName";
                    Pair<String,Object> curentPair = null;

                    for (Pair<Chunk, Integer> chunkaki : this.map((Chunk) userInput)) {
                        curentPair = (Pair<String,Object>)chunkaki.getKey().getData();
                        if(Objects.equals(curentPair.getKey(), keyOfInterest)){
                            System.out.println("Sending the room to worker:"+ this.findWorkerID(chunkaki));
                            conectToWorkers();
                            break;
                        }


                    }


                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
                inUser.close();
                userConnection.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }




    }

    public void conectToWorkers() {
        Socket workerSocket = null;
        ObjectOutputStream outWorker = null;
        ObjectInputStream inWorker = null;
        ServerSocket userSocket = null;
        try {
            workerSocket = new Socket(Master.host, Master.workerPort);
            outWorker = new ObjectOutputStream(workerSocket.getOutputStream());
            inWorker = new ObjectInputStream(workerSocket.getInputStream());

            outWorker.writeObject(userInput);
            outWorker.flush();
            System.out.println("Files are sent to workers!");

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                inWorker.close();
                outWorker.close();
                workerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public ArrayList<Pair<Chunk,Integer>> map(Chunk chunk){
        ArrayList<Chunk> chunks = new ArrayList<>();
        String data = (String) chunk.getData();
        JSONObject filter = new JSONObject(data);
        JSONObject roomData = filter.getJSONObject(String.valueOf(filter.keySet().toString().replace("[", "").replace("]", "")));
        int i =0;
        for(String key : roomData.keySet()){
            i++;
            Object pair = new Pair<String,Object>(key,roomData.get(key));

            chunks.add(new Chunk("i", i, pair));

        }


        ArrayList<Pair<Chunk,Integer>>mapper = new ArrayList<>();

        for(Chunk chunkaki : chunks){
            Pair<Chunk,Integer> mapPair = new Pair<Chunk,Integer>(chunkaki, chunk.getLenght());
            mapper.add(mapPair);
        }
        return mapper;
    }

    public int hasecode(Object str){
        return str.hashCode() * 31;
    }

    public int findWorkerID(Pair<Chunk,Integer> pair){
        Chunk chunk = pair.getKey();
        Pair<String,Object> data = (Pair<String,Object>)chunk.getData();
        Object value =  data.getValue();


        return abs(hasecode(value)%Master.num_of_workers);


    }
    public static void main(String[] args) {

        Master master = new Master();
        master.init();
        master.start();
        for (int i = 1; i <= Master.num_of_workers; i++) {
            Worker worker = new Worker(i);
            Worker.init();
            worker.openServer();
        }

    }
}


