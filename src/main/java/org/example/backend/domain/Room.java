package org.example.backend.domain;

import java.util.Objects;

public class Room {
    private String name;
    private String area;
    private double price;
    private String roomImage;
    private int noOfReviews;
    private int noOfPersons;
    private double rating;

    private int id;

    public Room(){}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public void setNoOfPersons(int noOfPersons) {
        this.noOfPersons = noOfPersons;
    }

    public int getNoOfPersons() {
        return noOfPersons;
    }

    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    public int getNoOfReviews() {
        return noOfReviews;
    }

    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    public String getRoomImage() {
        return roomImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id && Objects.equals(name, room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", area='" + area + '\'' +
                ", price=" + price +
                ", roomImage='" + roomImage + '\'' +
                ", noOfReviews=" + noOfReviews +
                ", noOfPersons=" + noOfPersons +
                ", rating=" + rating +
                ", id=" + id +
                '}';
    }
}
