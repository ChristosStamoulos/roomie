package org.example.backend;

import org.example.backend.domain.Chunk;
import org.example.backend.domain.Master;
import org.example.backend.domain.Room;
import org.example.backend.utils.Pair;
import org.example.backend.utils.SimpleCalendar;
import org.example.backend.utils.json.JsonConverter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class ConsoleApp {

    private static String host;
    private static int masterPort;

    private static void printMenu(){
        System.out.println(
                """
                   1. Add room
                   2. Add more available dates for your rooms.
                   3. See the reservations of your rooms.
                   4. Exit
                       """);
    }

    private static void init(){
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/user.config";


        try (FileInputStream f = new FileInputStream(filename)){
            prop.load(f);
        }catch (IOException exception ) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified");
        }

        host = prop.getProperty("host");
        masterPort = Integer.parseInt(prop.getProperty("masterPort"));
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Socket connectionSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream inp = null;
        int id;
        int segmentID = 0;

        init();

        try{
            connectionSocket = new Socket(host, masterPort);
            out = new ObjectOutputStream(connectionSocket.getOutputStream());
            inp = new ObjectInputStream(connectionSocket.getInputStream());
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
                            String text = new String(Files.readAllBytes(Paths.get(roomPath)), StandardCharsets.UTF_8);
                            JSONObject obj = new JSONObject(text);
                            System.out.println(obj.get("room"));
                            room = (Object) obj.get("room").toString();

                            Chunk c = new Chunk("12", 3, room);//type 3 add room
                            out.writeObject(c);
                            out.flush();
                            System.out.println("The room was added to Chambre.");
                        }catch(FileNotFoundException e){
                            System.err.println("The file was not found. Try again with the correct path.\n");
                        }catch(IOException e){
                            System.err.println("IO exception " + e.getMessage());
                        }catch(JSONException e){
                            System.err.println("The file you provided was not in correct json format. Please try again.\n");
                        }
                        break;
                    case 2:
                        System.out.print("What is your id? ");
                        id = Integer.parseInt(in.nextLine());
                        out.writeObject(new Chunk("2", 4, id));//add dates code 4

                        Chunk dat = null;
                        try{
                            dat = (Chunk) inp.readObject();
                        } catch (ClassNotFoundException e){
                            System.err.println("Class not found exception.");
                        }
                        ArrayList<Room> r = (ArrayList<Room>) dat.getData();
                        if(r.isEmpty()){
                            System.out.println("You have no rooms in the system.");
                        }else{
                            int c = 0;
                            for(Room r1: r){
                                System.out.println(String.valueOf(++c) + r1.toString());
                            }
                            System.out.println("Choose the room you want to add dates for reservations.");
                            int choiceOfRoom = in.nextInt();
                            ArrayList<String> dates = new ArrayList<>();
                            System.out.println("Give the dates in this format dd/MM/yyyy\nPress 'y' if you want to add more dates else 'n'.");
                            String more = "";
                            while(more.equals("y")){
                                System.out.print("Add date: ");
                                String date = in.nextLine();
                                dates.add(date);
                                System.out.print("More? ");
                                more = in.nextLine();
                            }
                            out.writeObject(new Chunk("i", 4, new Pair<Integer, ArrayList<String>>(r.get(choiceOfRoom-1).getId(), dates)));
                        }

                        break;
                    case 3:
                        System.out.print("What is your id? ");
                        id = Integer.parseInt(in.nextLine());
                        //out.writeObject(new Chunk("2", segmentID, 5, id));//reservations code 5
                        System.out.print("Enter the period of time you'd like to see the reservations.\n Date format -> dd/MM/yyy\n");
                        System.out.print("Start date: ");
                        String startDate = in.nextLine();
                        System.out.print("\nEnd date: ");
                        String endDate = in.nextLine();
                        Chunk c =new Chunk("i", 5, (Object) new Pair<Integer, Pair<String, String>>(id, new Pair<String, String>(startDate, endDate)));//code 5 for reservation
                        out.writeObject(c);
                        out.flush();
                        Chunk data = null;
                        try{
                           data = (Chunk) inp.readObject();
                        } catch (ClassNotFoundException e){
                            System.err.println("Class not found exception.");
                        }
                        ArrayList<Pair<String, Integer>> reservations = (ArrayList<Pair<String,Integer>>) data.getData();
                        for(Pair<String, Integer> p: reservations){
                            System.out.println(p.getKey() + ": " + p.getValue());
                        }
                        break;
                    case 4:
                        in.close();
                        System.exit(0);
                }
                segmentID++;
            }
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

    }
}
