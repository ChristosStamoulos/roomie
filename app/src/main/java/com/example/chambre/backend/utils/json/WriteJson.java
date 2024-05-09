package com.example.chambre.backend.utils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class WriteJson {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        System.out.println("in = ");
        String inp = in.nextLine();
        try{
            FileWriter file = new FileWriter("src/main/java/org/example/backend/data/dta.json");
            JSONObject rooms = new JSONObject();
            JSONArray arr = new JSONArray();
            while(!inp.equals("n")){
                JSONObject obj = new JSONObject();
                System.out.print("room name = ");
                String ans = in.nextLine();
                obj.put("roomName", ans);

                System.out.print("noOfPersons = ");
                ans = in.nextLine();
                obj.put("noOfPersons", ans);

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
                System.out.print("Manager's id = ");
                ans = in.nextLine();
                obj.put("mid", ans);
                arr.put(obj);
                System.out.println("more?");
                inp = in.nextLine();
            }
            rooms.put("Rooms", arr);
            file.write(rooms.toString());
            file.flush();
        }catch (IOException | JSONException e) {
            e.fillInStackTrace();
        }

    }
}
