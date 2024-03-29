import java.io.Serializable;

public class Chunk implements Serializable {    
    private final String userID;
    private final int segmentID;

    public Chunk(String userID, int segmentID){//, Data data){
        this.userID = userID;
        this.segmentID = segmentID;
        //this.data = data;
    }

    public String getUserID() {
        return userID;
    }

    public int getSegmentID() {
        return segmentID;
    }

    // public Data getData(){
    //     return data;
    // }
}