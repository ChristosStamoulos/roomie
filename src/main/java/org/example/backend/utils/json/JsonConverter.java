package org.example.backend.utils.json;

import org.example.backend.domain.Room;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
                Room room = new Room();
                room.setArea((String) jsonRoom.get("area"));
                room.setName((String) jsonRoom.get("roomName"));
                room.setPrice(Double.parseDouble((String) jsonRoom.get("price")));
                room.setRating(Double.parseDouble((String) jsonRoom.get("stars")));
                room.setNoOfReviews(Integer.parseInt((String) jsonRoom.get("noOfReviews")));
                room.setNoOfPersons(Integer.parseInt((String) jsonRoom.get("noOfPersons")));
                room.setRoomImage((String) jsonRoom.get("roomImage"));
                room.setId(i);
                System.out.println(room.toString());
                rooms.add(room);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Room> getRooms() {
        readFile();
        return rooms;
    }
}
