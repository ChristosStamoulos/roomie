import org.json.*;
import java.io.*;
import java.util.Scanner;;

public class json {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        System.out.println("in = ");
        String inp = in.nextLine();
        try{
            FileWriter file = new FileWriter("data.json");
            file.write("{\n");
            int i = 0;
            while(!inp.equals("n")){
                JSONObject obj = new JSONObject();
                file.write("\"room" + String.valueOf(i) + "\":");
                System.out.print("room name = ");
                String ans = in.nextLine();
                obj.put("roomName", ans);

                System.out.print("noOfPersons = ");
                ans = in.nextLine();
                obj.put("noOfPersons = ", ans);

                System.out.print("area = ");
                ans = in.nextLine();
                obj.put("area", ans);
                
                System.out.print("price = ");
                ans = in.nextLine();
                obj.put("price", ans);
                
                System.out.print("stars = ");
                ans = in.nextLine();
                obj.put("stars", ans);
                
                System.out.print("noOfReviews = ");
                ans = in.nextLine();
                obj.put("noOfReviews", ans);
                
                System.out.print("roomImage = ");
                ans = in.nextLine();
                obj.put("roomImage", ans);

                file.write(obj.toString());
                file.flush();
                System.out.println("more?");
                inp = in.nextLine();
                if(!inp.equals("n")){
                    file.write(",\n");
                }
                i++;
            }
            file.write("}");
            file.flush();
        }catch (IOException e) {
            e.fillInStackTrace();
        }
        
    }
}
