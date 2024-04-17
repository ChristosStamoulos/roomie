package org.example.backend.domain;

import org.example.backend.utils.Pair;
import org.example.backend.utils.SimpleCalendar;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

/** ActionsForWorkers Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to handle requests from the Worker,
 * processes the request the worked assigned and
 * sends the intermediate data to the reducer
 */
public class ActionsForWorkers extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Chunk data;
    private ArrayList<Room> rooms;
    private int reducerPort;
    private String reducerHost;

    /**
     * Constructor
     *
     * @param data  the data to be processed
     * @param rooms the rooms in thw worker's memory
     */
    public ActionsForWorkers(Chunk data, ArrayList<Room> rooms) {
        this.data = data;
        this.rooms = rooms;
    }

    /**
     * Initializes the ports and host for the reducer from the config file
     */
    public void init(){
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/worker.config";

        try (FileInputStream f = new FileInputStream(filename)) {
            prop.load(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        reducerPort = Integer.parseInt(prop.getProperty("reducerPort"));
        reducerHost = prop.getProperty("reducerHost");
    }

    /**
     * Starts a connection with the reducer
     * Processes the data and sends them to the reducer
     */
    public void run() {
        try {
            init();
            Socket reducerSocket = new Socket(reducerHost, reducerPort);
            out = new ObjectOutputStream(reducerSocket.getOutputStream());
            processRequest(data.getTypeID(), data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process the request the worker has assigned
     *
     * @param type  the type of the request
     * @param chunk the data to be processed
     */
    private void processRequest(int type, Chunk chunk) {
        switch (type) {
            case 1:     //user's request, searches for rooms with given filters
                ArrayList<Room> filteredRooms = findRoomByFilter(new JSONObject( (String)data.getData()));
                Chunk c = new Chunk(data.getUserID(), data.getTypeID(), filteredRooms);
                c.setSegmentID(data.getSegmentID());
                sendReducer(c);
                break;
            case 2:     //user's request, adds a reservation to the room
                addReservation((Pair<Integer, ArrayList<SimpleCalendar>>) data.getData());
                break;
            case 3:     //user's request, adds a rating to a room
                addRating((Pair<Integer, Integer>) data.getData());
                break;
            case 5:     //manager's request, adds a date of availability to the room
                addDatetoRoom((Pair<Integer, ArrayList<String>>) data.getData());
                break;
            case 6:     //manager's request, finds the rooms the manager owns and the total reservations
                //to be added, part B
                break;
            case 7:     //manager's request, finds the rooms the manager owns
                ArrayList<Room> roomsByManager = findRoomsByManager((Integer) data.getData());
                Chunk c1 = new Chunk(data.getUserID(), data.getTypeID(), roomsByManager);
                chunk.setSegmentID(data.getSegmentID());
                sendReducer(c1);
                break;
            default:
                System.out.println("Invalid request type");
        }
    }

    /**
     * Sends the reducer the processed data
     *
     * @param data  a Chunk Object of the process data
     */
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
        filter = filter.getJSONObject("filters");
        ArrayList<Room> mrooms = new ArrayList<Room>();

        String area = ((String) filter.get("area")).toLowerCase();
        int lowPrice = (Integer) filter.get("lowPrice");
        int highPrice = (Integer) filter.get("highPrice");
        SimpleCalendar finishDate = new SimpleCalendar((String) filter.get("finishDate"));
        SimpleCalendar startDate = new SimpleCalendar((String) filter.get("startDate"));
        int noOfPeople = (Integer) filter.get("noOfPeople");
        double stars = (Integer) filter.get("stars");

        for(Room r: rooms){
            int filterCounter = 0;
            if(area.equals("default")){
                filterCounter++;
            }else if(area.equals(r.getArea().toLowerCase())){
                filterCounter++;
            }
            if(lowPrice == 0 || lowPrice <= r.getPrice()){
                filterCounter++;
            }
            if(highPrice == 0 || highPrice >= r.getPrice()){
                filterCounter++;
            }

            if(finishDate.equals(new SimpleCalendar("01/01/0001"))){
                filterCounter++;
            }else{
                int countAvailability = 0;
                for(SimpleCalendar date: r.getAvailableDates()){
                    if(finishDate.after(date)){
                        countAvailability++;
                    }
                }
                if(countAvailability>0){
                    filterCounter++;
                }
            }
            if(stars == 0.0 || stars == r.getRating()){
                filterCounter++;
            }
            if(noOfPeople == 0 || noOfPeople == r.getNoOfPersons()){
                filterCounter++;
            }
            if(startDate.equals(new SimpleCalendar("01/01/0001"))){
                filterCounter++;
            }else {
                int countAvailability = 0;
                for (SimpleCalendar date : r.getAvailableDates()) {
                    if (startDate.before(date)) {
                        countAvailability++;
                    }
                }
                if (countAvailability > 0) {
                    filterCounter++;
                }
            }
            if(filterCounter == 7){
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
    private synchronized void addDatetoRoom(Pair<Integer, ArrayList<String>> dates){
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
    private synchronized void addReservation(Pair<Integer, ArrayList<SimpleCalendar>> dates){
        ArrayList<SimpleCalendar> dat = dates.getValue();
        for(SimpleCalendar d: dat){
            rooms.get(dates.getKey()).addReservationDate(d);
        }
    }

    /**
     * Adds a rating from the user to the room
     *
     * @param rating a Pair<Integer,Integer> with key the room id and value the rating
     */
    public void addRating(Pair<Integer, Integer> rating){
        int roomId = rating.getKey();
        int rate = rating.getValue();
        synchronized (rooms.get(roomId)){
            rooms.get(roomId).addRating(rate);
        }
    }
}
