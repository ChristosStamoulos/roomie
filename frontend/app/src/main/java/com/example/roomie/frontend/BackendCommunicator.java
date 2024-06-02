package com.example.roomie.frontend;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BackendCommunicator {

    private static BackendCommunicator instance = null;

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /**
     * Default Constructor
     */
    BackendCommunicator(){}

    public void attemptConnection(){
        try {
            this.socket = new Socket("192.168.1.3", 8080);
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static synchronized BackendCommunicator getInstance() {
        if (instance == null) instance = new BackendCommunicator();
        return instance;
    }
}
