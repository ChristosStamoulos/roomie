import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.UnknownHostException;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

public class ConsoleApp {

    private static void printMenu(){
        System.out.println(
         """
            1. Add room
            2. Add more available dates for your rooms.
            3. See the reservations of your rooms.
            4. Exit
                """);
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Socket connectionSocket = null;
        ObjectOutputStream out = null;
        int id;
        int segmentID = 0;
        
        while(true){
            printMenu();
            System.out.print("Your choice: ");
            int choice = Integer.parseInt(in.nextLine());
            Object room = null;
            switch (choice){
                case 1:
                    System.out.println("Give the relative path to the file with the room data");
                    String roomPath = in.nextLine();
                    try{
                        String text = new String(Files.readAllBytes(Paths.get(roomPath)),StandardCharsets.UTF_8);
                        JSONObject obj = new JSONObject(text);
                        System.out.println(obj.get("room"));
                        room = (Object) obj.get("room").toString();
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    try{
                        connectionSocket = new Socket("localhost", 52153);
                        out = new ObjectOutputStream(connectionSocket.getOutputStream());
                        Chunk c = new Chunk("12", segmentID, room);
                        out.writeObject(c);
                        out.flush();
                    }catch(UnknownHostException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }finally {
                        try {
                            connectionSocket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    System.out.print("What is your id? ");
                    id = Integer.parseInt(in.nextLine());
                    try{
                        connectionSocket = new Socket("localhost", 52153);
                        out = new ObjectOutputStream(connectionSocket.getOutputStream());
                        out.writeObject(new Chunk("2", segmentID, id));
                    }catch(UnknownHostException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }finally {
                        try {
                            connectionSocket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                break;
                case 3:
                    System.out.print("What is your id? ");
                    id = Integer.parseInt(in.nextLine());
                    try{
                        connectionSocket = new Socket("localhost", 52153);
                        out = new ObjectOutputStream(connectionSocket.getOutputStream());
                        out.writeObject(new Chunk("2", segmentID, id));
                    }catch(UnknownHostException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }finally {
                        try {
                            connectionSocket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                break;
                case 4:
                    in.close();
                    System.exit(0);
            }
            segmentID++;
        }
    }
}
