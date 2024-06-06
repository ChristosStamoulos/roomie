package com.example.roomie.backend.utils.json;

import com.example.roomie.backend.domain.Room;
import com.example.roomie.backend.domain.User;
import com.example.roomie.backend.utils.SimpleCalendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private static final String path = "src/main/java/com/example/roomie/backend/data/data.json";
    private ArrayList<Room> rooms;
    private ArrayList<User> users;

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
        } catch (JSONException e) {
            throw new RuntimeException(e);
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
        try {
            room.setArea((String) jsonRoom.get("area"));
            room.setLat(Double.parseDouble((String) jsonRoom.get("latitude")));
            room.setLon(Double.parseDouble((String) jsonRoom.get("longitude")));
            room.setDesc((String) jsonRoom.get("description"));
            room.setName((String) jsonRoom.get("roomName"));
            room.setPrice(Integer.parseInt((String) jsonRoom.get("price")));
            room.setRating(Double.parseDouble((String) jsonRoom.get("stars")));
            room.setNoOfReviews(Integer.parseInt((String) jsonRoom.get("noOfReviews")));
            room.setNoOfPersons(Integer.parseInt((String) jsonRoom.get("noOfPersons")));
            room.setNoOfRooms(Integer.parseInt((String) jsonRoom.get("noOfRooms")));
            room.setNoOfBathrooms(Integer.parseInt((String) jsonRoom.get("noOfBathrooms")));
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

            int noOfImgs = Integer.parseInt((String) jsonRoom.get("noOfImgs"));
            ArrayList<String> imgs = new ArrayList<>();
            String imgName = (String) jsonRoom.get("roomImage");
            for(int i = 1; i <= noOfImgs; i++){
                imgs.add(imgName + "_" + String.valueOf(i) + ".png");
            }
            room.setRoomImage(imgs);
            return room;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

    private void readUserFile(){
        try {
            String text = new String(Files.readAllBytes(Paths.get("src/main/java/com/example/roomie/backend/data/users.json")), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(text);
            JSONArray jsonUsers = new JSONArray(obj.getJSONArray("Users"));
            users = new ArrayList<User>();
            for (int i = 0; i < jsonUsers.length(); i++) {
                JSONObject jsonUser = (JSONObject) jsonUsers.get(i);

                User user = new User();
                user.setId(i);
                user.setUsername(jsonUser.getString("username"));
                user.setPassword(jsonUser.getString("password"));
                user.setName(jsonUser.getString("name"));
                user.setEmail(jsonUser.getString("email"));
                user.setPhoneNumber(jsonUser.getString("tel"));
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<User> getUsers(){
        readUserFile();
        return users;
    }
}
