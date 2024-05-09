package com.example.chambre.backend.domain;

import com.example.chambre.backend.utils.SimpleCalendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/** Room Class
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024
 *
 * This class represents the entity Room.
 */
public class Room implements Serializable {
    private String name;                    //the name of the room
    private String area;                    //the area the room is located
    private double price;                   //the price of a room for a night
    private String roomImage;               //a path to the room's image
    private int noOfReviews;                //the number of reviews
    private int noOfPersons;                //the number of people the room can host
    private double rating;                  //the total rating of the room
    private int mid;                        //manager's id

    private ArrayList<SimpleCalendar> availableDates;   //the available dates for rating the room
    private ArrayList<SimpleCalendar> reservationDates; //the dates the room is reserved

    private int id;                         //the room's id
    private static int idCount = 0;         //a counter for the ids of the rooms

    /**
     * Constructor
     */
    public Room(){
        this.availableDates = new ArrayList<SimpleCalendar>();
        this.reservationDates = new ArrayList<SimpleCalendar>();
        this.id = idCount++;
    }

    /**
     * Sets an id for the room
     *
     * @param id    the room's id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the room's id
     *
     * @return  the id
     */
    public int getId() {
        return id;
    }

    /**
     * Set's the manager's id
     *
     * @param mid the manager's id
     */
    public void setMid(int mid) {
        this.mid = mid;
    }

    /**
     * Gets the manager's id
     *
     * @return the mid
     */
    public int getMid() {
        return mid;
    }

    /**
     * Sets the name of the room
     *
     * @param name the name of the room
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the room
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the area of the room
     *
     * @param area  the area of the room
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Gets the area of the room
     *
     * @return the area
     */
    public String getArea() {
        return area;
    }

    /**
     * Sets the price of the room for one night
     *
     * @param price the price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the price of the room for a night
     *
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the rating of the room
     *
     * @param rating the rating
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Gets the rating of the room
     *
     * @return  the rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * Sets the number of people the room can host
     *
     * @param noOfPersons the number of people
     */
    public void setNoOfPersons(int noOfPersons) {
        this.noOfPersons = noOfPersons;
    }

    /**
     * Gets the number of people the room can host
     *
     * @return  the number of people
     */
    public int getNoOfPersons() {
        return noOfPersons;
    }

    /**
     * Sets the number of reviews the room has
     *
     * @param noOfReviews   the number of reviews
     */
    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    /**
     * Gets the number of reviews the room has
     *
     * @return the number of reviews
     */
    public int getNoOfReviews() {
        return noOfReviews;
    }

    /**
     * Sets a path to the room's image
     *
     * @param roomImage the path
     */
    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    /**
     * Gets the path to the room's image
     *
     * @return  the path
     */
    public String getRoomImage() {
        return roomImage;
    }

    /**
     * Sets a list of available dates the room can be reserved
     *
     * @param availableDates an ArrayList of SimpleCalendar dates
     */
    public void setAvailableDates(ArrayList<SimpleCalendar> availableDates) {
        this.availableDates = availableDates;
    }

    /**
     * Sets a list of dates the room is reserved
     *
     * @param reservationDates  an ArrayList of SimpleCalendar dates
     */
    public void setReservationDates(ArrayList<SimpleCalendar> reservationDates) {
        this.reservationDates = reservationDates;
    }

    /**
     * Gets the dates the room is reserved
     *
     * @return an ArrayList of SimpleCalendar objects
     */
    public ArrayList<SimpleCalendar> getReservationDates() {
        return reservationDates;
    }

    /**
     * Gets the dates the room is available
     *
     * @return an ArrayList of SimpleCalendar objects
     */
    public ArrayList<SimpleCalendar> getAvailableDates() {
        return availableDates;
    }

    /**
     * Adds an available date for the room
     *
     * @param date  a SimpleCalendar date
     */
    public void addAvailableDate(SimpleCalendar date){
        if(!this.availableDates.contains(date)) {
            this.availableDates.add(date);
        }
    }

    /**
     * Adds a reservation to the room
     *
     * @param date  a SimpleCalendar date
     */
    public void addReservationDate(SimpleCalendar date){
        if(!this.reservationDates.contains(date)) {
            this.reservationDates.add(date);
            this.availableDates.remove(date);
        }
    }

    /**
     * Adds a rating to the room and calculates the total rating
     *
     * @param rate  the rating a user made
     */
    public void addRating(int rate){
        this.rating = (this.rating*this.noOfReviews + rate)/(this.noOfReviews + 1);
        this.noOfReviews++;
    }

    /**
     * Checks if two Object Room are equal
     *
     * @param o the other object
     * @return  true if they are equal, else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id && Objects.equals(name, room.name);
    }

    /**
     * Calculates the hash code of the room
     *
     * @return  the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Formats the object Room to a string
     *
     * @return a String
     */
    @Override
    public String toString() {
        return "Room name: " + name + '\n' +
                "area: " + area + '\n' +
                "price: " + price + " per night\n" +
                "noOfReviews: " + noOfReviews +
                "\nnoOfPersons: " + noOfPersons +
                "\nrating:" + rating;
    }
}
