package org.example.backend.domain;

import org.example.backend.utils.Pair;
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

public class mas extends Thread{

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

        mas.host = prop.getProperty("host");
        mas.workerPort = Integer.parseInt(prop.getProperty("workerPort"));
        mas.ReducerPort = Integer.parseInt(prop.getProperty("reducerPort"));
        mas.num_of_workers = Integer.parseInt(prop.getProperty("numberOfWorkers"));
        System.out.println(Integer.parseInt(prop.getProperty("workerPort")));
    }


    public void run(){
        Socket workerSocket=null;
        ObjectOutputStream outWorker=null;
        ObjectInputStream inWorker=null;

        try{
            workerSocket = new Socket(mas.host,mas.workerPort);
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
        mas.init();
        //new mas().start();


        try {
            ServerSocket clientSocket = new ServerSocket(8080);

            Thread client = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket connectionSocket = clientSocket.accept();
                        System.out.println("Client connected");
                        try{
                            ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());
                            ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
                            Chunk data = (Chunk) in.readObject();
                            System.out.println(data.getData().toString());
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
                //while (!Thread.currentThread().isInterrupted()) {
                    try{
                        workerSocket = new Socket("localhost", 52153);
                    }catch(UnknownHostException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    ObjectOutputStream outWorker=null;
                    ObjectInputStream inWorker=null;
                    try {
                        outWorker = new ObjectOutputStream(workerSocket.getOutputStream());
                        inWorker = new ObjectInputStream(workerSocket.getInputStream());
                        System.out.println(inWorker.readUTF());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //}
            });
            worker.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
