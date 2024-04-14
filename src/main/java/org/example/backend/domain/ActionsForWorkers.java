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
    ObjectInputStream in;
    Socket out;
    private  static Chunk masterInput;
    private static ArrayList<Room> rooms;
    private Chunk data;
    public ActionsForWorkers(Chunk data, ArrayList<Room> rooms, Socket out) {
        this.data = data;
        this.rooms = rooms;
        this.out = out;
    }

    public void run() {
        int typeOfRequest = data.getTypeID();
        switch(typeOfRequest){
            case 1:
                ArrayList<Room> filteredRooms = findRoomByFilter((JSONObject) data.getData());
                Chunk c = new Chunk(data.getUserID(), data.getTypeID(), filteredRooms);
                c.setSegmentID(data.getSegmentID());
                try {
                    ObjectOutputStream o = new ObjectOutputStream(out.getOutputStream());
                    o.writeObject(c);
                    o.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
                Chunk chunk = new Chunk(data.getUserID(), data.getTypeID(), roomsByManager);
                chunk.setSegmentID(data.getSegmentID());
                try {
                    ObjectOutputStream o = new ObjectOutputStream(out.getOutputStream());
                    o.writeObject(chunk);
                    o.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
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
