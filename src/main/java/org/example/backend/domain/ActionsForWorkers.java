package org.example.backend.domain;

import org.example.backend.utils.Pair;
import org.example.backend.utils.SimpleCalendar;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ActionsForWorkers extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Chunk data;
    private ArrayList<Room> rooms = new ArrayList<>();

    public ActionsForWorkers(Chunk data) {
        this.data = data;
    }


    public void run() {
        try {
            Socket reducerSocket = new Socket("localhost", 52256);
            out = new ObjectOutputStream(reducerSocket.getOutputStream());
            processRequest(data.getTypeID(), data);
            sendReducer(data);                      //na fygei meta
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processRequest(int type, Chunk chunk) {
        switch (type) {
            case 1:
                ArrayList<Room> filteredRooms = findRoomByFilter(new JSONObject( (String)data.getData()));
                Chunk c = new Chunk(data.getUserID(), data.getTypeID(), filteredRooms);
                c.setSegmentID(data.getSegmentID());
                System.out.println(c.getData().toString());
                System.out.println(c.getTypeID());
                sendReducer(c);
                break;
            case 2:
                addReservation((Pair<Integer, ArrayList<String>>) data.getData());
                break;
            case 3:
                break;
            case 4:
                addDatetoRoom((Pair<Integer, ArrayList<String>>) data.getData());
                break;
            case 5:
                ArrayList<Room> roomsByManager = findRoomsByManager((Integer) data.getData());
                Chunk c1 = new Chunk(data.getUserID(), data.getTypeID(), roomsByManager);
                chunk.setSegmentID(data.getSegmentID());
                sendReducer(c1);
                break;
            default:
                System.out.println("Invalid request type");
        }
    }

    private void sendReducer(Chunk data) {
        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeStreams() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
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
}
