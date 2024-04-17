package org.example.backend.utils.json;

import org.example.backend.domain.Room;
import org.example.backend.utils.SimpleCalendar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/** JsonConverter Class
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024
 *
 * This class is implemented to convert json objects, the rooms in data into objects of type Room.
 */
public class JsonConverter {
    private static final String path = "src/main/java/org/example/backend/data/data.json";
    private ArrayList<Room> rooms;

    private void readFile() {
        try {
            String text = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(text);
            JSONArray jsonRooms = new JSONArray(obj.getJSONArray("Rooms"));
            rooms = new ArrayList<Room>();
            for (int i = 0; i < jsonRooms.length(); i++) {
                JSONObject jsonRoom = (JSONObject) jsonRooms.get(i);
                Room room = convertToRoom(jsonRoom);
                rooms.add(room);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a json object to a Room Object
     *
     * @param jsonRoom  a Json object
     * @return  a Room object
     */
    public Room convertToRoom(JSONObject jsonRoom){
        Room room = new Room();
        room.setArea((String) jsonRoom.get("area"));
        room.setName((String) jsonRoom.get("roomName"));
        room.setPrice(Double.parseDouble((String) jsonRoom.get("price")));
        room.setRating(Double.parseDouble((String) jsonRoom.get("stars")));
        room.setNoOfReviews(Integer.parseInt((String) jsonRoom.get("noOfReviews")));
        room.setNoOfPersons(Integer.parseInt((String) jsonRoom.get("noOfPersons")));
        room.setRoomImage((String) jsonRoom.get("roomImage"));
        room.setMid(Integer.parseInt((String) jsonRoom.get("mid")));
        JSONArray jsonAvailableDates = new JSONArray( jsonRoom.getJSONArray("availableDates"));
        ArrayList<SimpleCalendar> avDates = new ArrayList<SimpleCalendar>();
        for(int i = 0; i<jsonAvailableDates.length(); i++){
            JSONObject date = (JSONObject) jsonAvailableDates.get(i);
            avDates.add(new SimpleCalendar((String) date.get("date")));
        }
        JSONArray jsonReservationDates = new JSONArray( jsonRoom.getJSONArray("reservationDates"));
        ArrayList<SimpleCalendar> resDates = new ArrayList<SimpleCalendar>();
        for(int i = 0; i<jsonReservationDates.length(); i++){
            JSONObject date = (JSONObject) jsonReservationDates.get(i);
            resDates.add(new SimpleCalendar((String) date.get("date")));
        }
        room.setAvailableDates(avDates);
        room.setReservationDates(resDates);

        return room;
    }

    /**
     * Gets the coverted rooms
     *
     * @return  an ArrayList of Rooms
     */
    public ArrayList<Room> getRooms() {
        readFile();
        return rooms;
    }
}
