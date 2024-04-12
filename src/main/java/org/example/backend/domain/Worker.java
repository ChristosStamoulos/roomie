package org.example.backend.domain;

import org.example.backend.utils.Pair;
import org.example.backend.utils.SimpleCalendar;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

/** Worker Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to handle requests from the Master and to map the data assigned from the Master.
 */

public class Worker {
    private final int id;
    private static int masterPort;
    private static int ReducerPort;
    private static int serverPort;
    private ArrayList<Room> rooms;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

    Worker(int id){
        this.id = id;
        this.rooms = new ArrayList<Room>();
    }

    public static void init(){
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/worker.config";


        try (FileInputStream f = new FileInputStream(filename)){
            prop.load(f);
        }catch (IOException exception ) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified");
        }

        Worker.serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        Worker.masterPort = Integer.parseInt(prop.getProperty("masterPort"));
        System.out.println(Integer.parseInt(prop.getProperty("serverPort")));
    }

    /**
     * Adds room to the workers "memory"
     * @param room a Room object
     */
    private void addRoom(Room room){
        if(!rooms.contains(room)){
            rooms.add(room);
        }
    }

    /**
     * Finds the rooms a manager owns.
     *
     * @param mid   the manager's id
     * @return      an ArrayList of objects Room
     */
    private ArrayList<Room> findRoomsByManager(int mid){
        ArrayList<Room> mrooms = new ArrayList<Room>();
        for(Room r: rooms){
            if(r.getMid() == mid){
                mrooms.add(r);
            }
        }
        return mrooms;
    }

    /**
     * Finds room by a specific filter the client wants
     *
     * @param filter    the filter in json format
     * @return          an ArrayList of objects Room
     */
    private ArrayList<Room> findRoomByFilter(JSONObject filter){
        ArrayList<Room> mrooms = new ArrayList<Room>();
        for(Room r: rooms){
            if((filter.keySet().toArray()[0]).equals("area") && filter.get("area").equals(r.getArea())){//etc
                mrooms.add(r);
            }
        }
        return mrooms;
    }

    /**
     * Adds available dates for reservation in a room
     *
     * @param dates     an ArrayList of dates in String
     */
    private void addDatetoRoom(Pair<Integer, ArrayList<String>> dates){
        ArrayList<String> dat = dates.getValue();
        for(String d: dat){
            SimpleCalendar date = new SimpleCalendar(d);
            if(!rooms.get(dates.getKey()).getAvailableDates().contains(date)){
                rooms.get(dates.getKey()).addAvailableDate(date);
            }
        }
    }

    /**
     * Adds a reservation a user made
     *
     * @param dates a Pair object with key the room id and value the dates the user wants to reserve the room
     */
    private void addReservation(Pair<Integer, ArrayList<String>> dates){
        ArrayList<String> dat = dates.getValue();
        for(String d: dat){
            SimpleCalendar date = new SimpleCalendar(d);
            rooms.get(dates.getKey()).addReservationDate(date);
        }
    }

    public static void main(String[] args) {
        init();
        openServer();
    }
    static ServerSocket providerSocket;
    static Socket masterConnection = null;

    static void openServer() {
        try {
            providerSocket = new ServerSocket(Worker.serverPort);
            while (true) {
                masterConnection = providerSocket.accept();
                in = new ObjectInputStream(masterConnection.getInputStream());

                try {
                    while(!Thread.currentThread().isInterrupted()) {
                        Chunk data = (Chunk) in.readObject();

                        Thread workerThread = new ActionsForClients(data);
                        workerThread.start();

                        //System.out.println("Worker #" + id + " assigned data: " + data);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                //System.out.println("oups here");
//                Thread t = new ActionsForClients(masterConnection);
//                t.start();

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
