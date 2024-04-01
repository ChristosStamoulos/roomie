package org.example.backend.domain;

import org.example.backend.utils.Pair;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

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
        System.out.println(Integer.parseInt(prop.getProperty("workerPort")));
    }


    public void run(){
        Socket workerSocket=null;
        ObjectOutputStream outWorker=null;
        ObjectInputStream inWorker=null;

        try{
            workerSocket = new Socket(Master.host,Master.workerPort);
            outWorker = new ObjectOutputStream(workerSocket.getOutputStream());
            inWorker = new ObjectInputStream(workerSocket.getInputStream());
            System.out.println(inWorker.readUTF());

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
        Object data = chunk.getData();
        JSONObject filters = (JSONObject)data;

        int i = 0;
        for (String key : filters.keySet()){
            i++;
            Object pair = new Pair<String,Object>(key,filters.get(key));

            chunks.add(new Chunk("i", i, pair));
        }

        ArrayList<Pair<Chunk,Integer>>maper = new ArrayList<>();

        for(Chunk chunkaki : chunks){
            Pair<Chunk,Integer> mapPair = new Pair<Chunk,Integer>(chunkaki, chunk.getLenght());
            maper.add(mapPair);
        }
        return maper;
    }

    public int hasecode(Object str){
        return str.hashCode() * 31;
    }

    public int findWorkerID(Pair<Chunk,Integer> pair){
        Chunk chunk = pair.getKey();
        Pair<String,Object> data = (Pair<String,Object>)chunk.getData();
        Object value =  data.getValue();

        return hasecode(value)%2;


    }
    public static void main(String[] args) {
        Master.init();
        new Master().start();
        for (int i = 1; i <= Master.num_of_workers; i++) {
            Worker worker = new Worker(i);
            Worker.init();
            worker.openServer();
        }
    }
}


