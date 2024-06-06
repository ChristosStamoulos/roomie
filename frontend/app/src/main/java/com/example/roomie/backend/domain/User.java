package com.example.roomie.backend.domain;

import java.io.Serializable;

public class User implements Serializable {

    private String username;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private int id;

    /**
     * Default Constructor
     */
    public User(){}

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
     * Gets the email of the user
     *
     * @return the email as a String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user
     *
     * @param email the email as a String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the id of the user
     *
     * @return the id as an Integer
     */
    public int getId() { return id; }

    /**
     * Sets the id of the user
     *
     * @param id the id as an Integer
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        User user = (User) obj;
        return(this.username.equals(user.username) && this.password.equals(user.password));
    }
}
