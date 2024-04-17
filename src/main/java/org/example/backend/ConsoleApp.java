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

/** ConsoleApp Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented for the manager's operations.
 * A manager can add more rooms to the app, see the total reservations, add available dates for hosting
 * and see the rooms they own.
 */
public class ConsoleApp {

    private static String host;
    private static int masterPort;

    /**
     * Prints the menu of options
     */
    private static void printMenu(){
        System.out.println(
                """
                   1. Add room
                   2. Add more available dates for your rooms.
                   3. See the reservations of your rooms.
                   4  See the rooms you own.
                   5. Exit
                       """);
    }

    /**
     * Initialises the variables from the configuration file
     */
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
        ArrayList<Room> rooms = new ArrayList<>();

        init();

        try{

            while(true){
                printMenu();
                System.out.print("Your choice: ");
                int choice = Integer.parseInt(in.nextLine());
                Object room = null;

                connectionSocket = new Socket(host, masterPort);
                out = new ObjectOutputStream(connectionSocket.getOutputStream());

                switch (choice){
                    case 1:

                        System.out.println("Give the relative path to the file with the room data");
                        String roomPath = in.nextLine();
                        try{
                            String text = new String(Files.readAllBytes(Paths.get(roomPath)), StandardCharsets.UTF_8);
                            JSONObject obj = new JSONObject(text);
                            System.out.println(obj.get("room"));
                            room = (Object) obj.get("room").toString();

                            Chunk c = new Chunk("12", 4, room);//type 4 add room
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
                        if(!rooms.isEmpty()) {
                            System.out.println("Choose the room you want to add dates for reservations.");
                            int choiceOfRoom = Integer.parseInt(in.nextLine());
                            ArrayList<String> dates = new ArrayList<>();
                            System.out.println("Give the dates in this format dd/MM/yyyy\nPress 'y' if you want to add more dates else 'n'.");
                            String more = "y";
                            while (more.equals("y")) {
                                System.out.print("Add date: ");
                                String date = in.nextLine();
                                dates.add(date);
                                System.out.print("More? ");
                                more = in.nextLine();
                            }
                            out.writeObject(new Chunk("i", 5, new Pair<Integer, ArrayList<String>>(rooms.get(choiceOfRoom - 1).getId(), dates)));
                        }else{
                            System.err.println("There are no rooms to choose from.");
                        }
                        break;
                    case 3:
                        System.out.print("What is your id? ");
                        id = Integer.parseInt(in.nextLine());

                        Chunk c =new Chunk("i", 7, id);
                        out.writeObject(c);
                        out.flush();
                        Chunk data = null;
                        try{
                            inp = new ObjectInputStream(connectionSocket.getInputStream());
                            data = (Chunk) inp.readObject();
                        } catch (ClassNotFoundException e){
                            System.err.println("Class not found exception.");
                        }
                        ArrayList<Room> reservations = (ArrayList<Room>) ((Chunk) data).getData();
                        int j = 0;
                        for(Room p: reservations){
                            System.out.println("\n" + (++j) +". " + p.getName() + "\nReservations");
                            for(SimpleCalendar d : p.getReservationDates()) {
                                System.out.println("    " + d.toString());
                            }
                        }
                        break;
                    case 4:
                        System.out.print("What is your id? ");
                        id = Integer.parseInt(in.nextLine());
                        out.writeObject(new Chunk("2", 7, id));//search by manager id code 7

                        Chunk dat = null;
                        try{
                            inp = new ObjectInputStream(connectionSocket.getInputStream());
                            dat = (Chunk) inp.readObject();
                        } catch (ClassNotFoundException e){
                            System.err.println("Class not found exception.");
                        }
                        rooms = (ArrayList<Room>) ((Chunk) dat.getData()).getData();
                        if(rooms.isEmpty()){
                            System.out.println("You have no rooms in the system.");
                        }else {
                            int i = 0;
                            for (Room r : rooms) {
                                System.out.println(String.valueOf(++i) + ". " + r.toString() +"\n");
                            }
                        }
                        break;
                    case 5:
                        in.close();
                        System.exit(0);
                }
            }
        }catch(UnknownHostException e){
            System.err.println("Host is unknown.");
        }catch(IOException e){
            System.err.println("IO Exception.");
        }finally {
            try {
                connectionSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
}
