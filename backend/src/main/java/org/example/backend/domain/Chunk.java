package org.example.backend.domain;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/** Chunk Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to represent a packet with information about the current request.
 */
public class Chunk implements Serializable {
    private final String userID;
    private static int segmentID;
    private final int typeID;
    private final Object data;

    public Chunk(String userID, int typeID, Object data){
        this.userID = userID;
        this.typeID = typeID;
        this.data = data;
    }

    public void setSegmentID(int id){
        this.segmentID = id;
    }
    public String getUserID() {
        return userID;
    }
    public int getSegmentID() {
        return segmentID;
    }
    public int getTypeID() {
        return typeID;
    }

    public Object getData(){
        return data;
    }

    public int getLenght(){
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(this); // Serialize the current Chunk object
            objectStream.close();
            return byteStream.toByteArray().length;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    @Override
    public boolean equals(Object other) {
        if (other == null){
            return false;
        }
        if (this == other){
            return true;
        }
        if (!(other instanceof Chunk)){
            return false;
        }
        Chunk o = (Chunk) other;
        return (this.segmentID == o.segmentID);
    }
}
