//------------------------------------------------------------------Imports------------------------------------------------------------------
import java.net.*;
import java.io.*;
import java.util.*;

//----------------------------------------------------------------Worker-Class---------------------------------------------------------------
public class Worker extends Thread{
    private final int id;
    private static String host; //
    private static ObjectInputStream in; // Socket for Worker to listen the requests from the Master
    private static ObjectOutputStream out; // Socket for each request to send the result to the Reducer
    private static int listenPort; // Port to listen the request
    private static int requestReducerPort; // Port to send the result
    private Chunk chunk;
    public Socket requestSocket;
//-------------------------------------------------------------Worker-Constructor------------------------------------------------------------
    public Worker(int id, Socket socket, Chunk chunk){
        this.id = id;
        this.chunk = chunk;
        this.requestSocket = socket;
    }
//---------------------------------------------------------------Initialization--------------------------------------------------------------
    public static void init(){
        Properties prop = new Properties();
        String filename = "backend\\config\\worker.config";

        try (FileInputStream f = new FileInputStream(filename)){
            prop.load(f);
        }catch (IOException exception ) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified");
        }

        Worker.host = prop.getProperty("host");
        Worker.listenPort = Integer.parseInt(prop.getProperty("listenPort"));
        Worker.requestReducerPort = Integer.parseInt(prop.getProperty("requestReducerPort"));
        System.out.println(Integer.parseInt(prop.getProperty("listenPort")));
    }

    @Override
	public void run(){
        try{
            Worker.out.writeObject(chunk);
        }catch(IOException e){
            System.err.println("I/O Error\n" + "Error");
        }
        System.out.println("hey i'm working!!");
    }

    public static void main(String[] args) {
        Worker.init();
        Socket connectionSocket; 
        System.out.println(Worker.host);

        try{
            connectionSocket = new Socket(Worker.host, Worker.listenPort);
            Worker.in = new ObjectInputStream(connectionSocket.getInputStream());
            System.out.println("Found Master!");
            while(in.available() > 0){
                String message = in.readUTF();
            System.out.println("Server says: " + message);
            }
            connectionSocket.close();
            in.close();;

        }catch(IOException e){
            System.err.println("I/O Error\n" + "Error occured while trying to create the socket for conection enstablishment");
        }


        try{
            Chunk chunk = (Chunk) Worker.in.readObject();
            Socket chunkSocket = new Socket(Worker.host, Worker.requestReducerPort);
            Worker worker = new Worker(1, chunkSocket, chunk);
            worker.start();
        }catch(ClassNotFoundException e){
            System.err.println("Unexpected error: " + e);
        }catch(IOException e){
            System.err.println("I/O Error\n" + "Error occured while trying to create the socket for the request");
        }
    }
}

