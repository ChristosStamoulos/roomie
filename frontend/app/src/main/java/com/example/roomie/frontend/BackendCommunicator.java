package com.example.roomie.frontend;

import com.example.roomie.backend.domain.Chunk;

import org.json.JSONException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class BackendCommunicator {

    private static BackendCommunicator instance = null;

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private boolean connectionEstablished;

    /**
     * Default Constructor
     */
    BackendCommunicator(){
        connectionEstablished = false;
    }

    public void attemptConnection(){
        try {
            this.socket = new Socket("192.168.1.3", 8080);
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            this.connectionEstablished = true;
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static synchronized BackendCommunicator getInstance() {
        if (instance == null) instance = new BackendCommunicator();
        return instance;
    }

    public void sendMasterInfo(Chunk chunk) {
        if (!connectionEstablished) attemptConnection();

        try {
            this.outputStream.writeObject(chunk);
            this.outputStream.flush();
            String answer = (String) this.inputStream.readObject();
        } catch (IOException e) {
            System.err.print(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Chunk sendClientInfo(){
        Chunk masterInput = null;

        try{
            masterInput = (Chunk) this.inputStream.readObject();
            System.out.println("Files are sent back from master!");
            return masterInput;
        } catch (IOException e) {
            // Handle IOException and JSONException if occurred during reading object or processing JSON
            System.err.println("Json file not found");
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            // Handle ClassNotFoundException if the class of the serialized object cannot be found
            System.err.println("Class not found" + ex);
        }
        return masterInput;
    }
}
