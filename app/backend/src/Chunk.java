import java.io.Serializable;

public class Chunk implements Serializable {    
    private final String userID;
    private final int segmentID;
    private final Object data;

    public Chunk(String userID, int segmentID, Object data){
        this.userID = userID;
        this.segmentID = segmentID;
        this.data = data;
    }

    public String getUserID() {
        return userID;
    }

    public int getSegmentID() {
        return segmentID;
    }

    public Object getData(){
        return data;
    }
}