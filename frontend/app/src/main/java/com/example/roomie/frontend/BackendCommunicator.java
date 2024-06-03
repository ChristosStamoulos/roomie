package com.example.roomie.frontend;

import android.util.Log;

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

            Log.d("BC", "yo");
            this.socket = new Socket("192.168.1.3", 9090);

            Log.d("BC", "yopo");
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
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Log.d("BC", "yoooooooo");
            this.outputStream.writeObject(chunk);
            this.outputStream.flush();
        } catch (IOException e) {
            System.err.print(e);
        }
    }

    public Chunk sendClientInfo(){
        Chunk masterInput = null;
        try {
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
