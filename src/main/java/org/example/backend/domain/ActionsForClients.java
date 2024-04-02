package org.example.backend.domain;

import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ActionsForClients extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    private  static Object masterInput;
    public ActionsForClients(Socket connection) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
          masterInput = in.readObject();
          Chunk chunk = (Chunk) masterInput;
          String strObject = (String) chunk.getData();
          JSONObject jsonObject = new JSONObject(strObject);
          System.out.println("hello from workers !this is what i have been sent :"+jsonObject.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
