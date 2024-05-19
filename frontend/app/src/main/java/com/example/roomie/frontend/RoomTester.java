package com.example.roomie.frontend;

import com.example.roomie.backend.domain.Room;
import com.example.roomie.backend.utils.SimpleCalendar;

import java.util.ArrayList;

public class RoomTester {

    public ArrayList<Room> rooms = new ArrayList<>();

    private ArrayList<Room> generateRooms(){
        Room r = new Room();
        r.setArea("Athens");
        r.setPrice(34);
        r.setRoomImage("loversroom");
        r.setNoOfReviews(12);
        r.setMid(1);
        r.setRating(4.4);
        r.setNoOfPersons(5);
        r.setName("lover's room");
        r.addAvailableDate(new SimpleCalendar("19/04/2024"));
        r.addAvailableDate(new SimpleCalendar("20/04/2024"));
        r.addReservationDate(new SimpleCalendar("27/07/2024"));

        rooms.add(r);

        r = new Room();
        r.setArea("Crete");
        r.setPrice(40);
        r.setRoomImage("eleni.png");
        r.setNoOfReviews(3);
        r.setMid(2);
        r.setRating(1.3);
        r.setNoOfPersons(3);
        r.setName("eleni's rooms");
        r.addAvailableDate(new SimpleCalendar("19/05/2024"));
        r.addAvailableDate(new SimpleCalendar("20/05/2024"));
        r.addReservationDate(new SimpleCalendar("28/04/2024"));

        rooms.add(r);

        r = new Room();
        r.setArea("Skiathos");
        r.setPrice(78);
        r.setRoomImage("lenas.png");
        r.setNoOfReviews(56);
        r.setMid(1);
        r.setRating(4.5);
        r.setNoOfPersons(3);
        r.setName("lena's room");
        r.addAvailableDate(new SimpleCalendar("15/10/2024"));
        r.addAvailableDate(new SimpleCalendar("16/10/2024"));
        r.addReservationDate(new SimpleCalendar("22/09/2024"));

        rooms.add(r);

        r = new Room();
        r.setArea("Aegina");
        r.setPrice(400);
        r.setRoomImage("entireVilla.png");
        r.setNoOfReviews(40);
        r.setMid(2);
        r.setRating(5);
        r.setNoOfPersons(5);
        r.setName("Entire villa");
        r.addAvailableDate(new SimpleCalendar("19/04/2024"));
        r.addAvailableDate(new SimpleCalendar("15/01/2025"));
        r.addReservationDate(new SimpleCalendar("20/01/2025"));

        rooms.add(r);

        r = new Room();
        r.setArea("Loutraki");
        r.setPrice(80);
        r.setRoomImage("OliveGroves.png");
        r.setNoOfReviews(139);
        r.setMid(1);
        r.setRating(4.4);
        r.setNoOfPersons(6);
        r.setName("OliveGroves");
        r.addAvailableDate(new SimpleCalendar("19/06/2024"));
        r.addAvailableDate(new SimpleCalendar("21/10/2024"));
        r.addReservationDate(new SimpleCalendar("22/07/2024"));

        rooms.add(r);

        r = new Room();
        r.setArea("Athens");
        r.setPrice(123);
        r.setRoomImage("romanos.png");
        r.setNoOfReviews(314);
        r.setMid(3);
        r.setRating(4.9);
        r.setNoOfPersons(3);
        r.setName("Romanos");
        r.addAvailableDate(new SimpleCalendar("07/09/2024"));
        r.addAvailableDate(new SimpleCalendar("08/09/2024"));
        r.addReservationDate(new SimpleCalendar("16/06/2024"));

        rooms.add(r);

        return rooms;
    }

    public ArrayList<Room> getRooms() {
        generateRooms();
        return rooms;
    }
}
