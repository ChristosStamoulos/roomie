package com.example.roomie.backend.domain;

import java.util.ArrayList;

public class User {

    private String username;
    private String password;
    private String name;
    private String phoneNumber;
    private ArrayList<Room> rooms;


    /**
     * Default Constructor
     */
    User(){}

    /**
     * Gets username of the user
     *
     * @return the username as String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username of the user
     *
     * @param username the username as String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of a user
     *
     * @return the password as a String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of a user
     *
     * @param password the password as String
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the name of the user
     *
     * @return the name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user
     *
     * @param name the name as a String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the phone number of the user
     *
     * @return the phone number as a String
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user
     *
     * @param phoneNumber the phone number as a String
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the rooms teh user has done a reservation
     *
     * @return the rooms as an ArrayList of Room Objects
     */
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    /**
     * Sets the rooms teh user has done a reservation
     *
     * @param rooms an ArrayList of Room Objects
     */
    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }
}
