package org.example.backend.dummyUser;

import org.example.backend.domain.Chunk;
import org.example.backend.domain.Master;
import org.json.JSONException;
import org.json.JSONObject;
import org.example.backend.utils.SimpleCalendar;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

/** DummyUser Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to represent a user.
 */
public class DummyUser extends Thread{
    private final int id;
    private final String path;
    private static String host;
    private  static int masterPort;
    private boolean automatic = false;

    private static Chunk chunkMessage;

    Socket toMasterSocket=null;
    ObjectOutputStream outToMaster=null;
    ObjectInputStream inFromMaster=null;

    DummyUser(int id){
        this.id = id;
        this.path =  "src\\main\\java\\org\\example\\backend\\"+ "\\dummyUser\\userData\\" + "userData" + id + "\\";

    }

    @Override
    public void run() {
        try {
            login();
            int option;
            Chunk chunk= null ;
            Scanner in = new Scanner(System.in);

            System.out.println("DummyUser " + id + " welcome!\n" + "Please follow the rules below to add a filter to your search");
            System.out.println("1.Area        |  For filtering your search by place, please enter the place you want else type 'none'");
            String place = in.nextLine();
            System.out.println("2.Start Date  |  For filtering your search by the date, please enter the starting date you want (such as '12/11/2024') else type 'none'");
            String startDate = in.nextLine();
            System.out.println("2.Finish Date |  For filtering your search by the date, please enter the finishing date you want (such as '14/11/2024') else type 'none'");
            String finishDate = in.nextLine();
            System.out.println("3.People      |  For filtering your search by the number of people, please enter the number you want else type 'none'");
            String numberOfPeople = in.nextLine();
            System.out.println("4.Low Price   |  For filtering your search by the price, please enter the low boundary price you want else type 'none'");
            String lowPrice = in.nextLine();
            System.out.println("4.High Price  |  For filtering your search by the price, please enter the high boundary price you want else type 'none'");
            String highPrice = in.nextLine();
            System.out.println("5.Stars       |  For filtering your search by the number of stars, please enter the number you want else type 'none'");
            String stars = in.nextLine();

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/java/org/example/backend/data/sampleFilters.json")));
            JSONObject jsonObject = new JSONObject(jsonData);
            if(!Objects.equals(place, "none")) jsonObject.getJSONObject("filters").put("area", place);
            if(!Objects.equals(startDate, "none")) jsonObject.getJSONObject("filters").put("startDate", new SimpleCalendar(startDate));
            if(!Objects.equals(finishDate, "none")) jsonObject.getJSONObject("filters").put("finishDate", new SimpleCalendar(finishDate));
            if(!Objects.equals(numberOfPeople, "none")) jsonObject.getJSONObject("filters").put("noOfPeople", Integer.parseInt(numberOfPeople));
            if(!Objects.equals(lowPrice, "none")) jsonObject.getJSONObject("filters").put("lowPrice", Integer.parseInt(lowPrice));
            if(!Objects.equals(highPrice, "none")) jsonObject.getJSONObject("filters").put("highPrice", Integer.parseInt(highPrice));
            if(!Objects.equals(stars, "none")) jsonObject.getJSONObject("filters").put("stars", Double.parseDouble(stars));

            System.out.println(jsonObject.toString());

            chunk = new Chunk(String.valueOf(this.id),1, jsonObject);

            connectMaster();



        }catch(Exception e) {

        }finally{

        }
    }


    /* Initialize the user */
    private void login(){
        try {
            Properties prop = new Properties();
            String filename = "src\\main\\java\\org\\example\\backend\\dummyUser\\userData\\user.config";

            try (FileInputStream f = new FileInputStream(filename)){
                prop.load(f);
            }catch (IOException exception) {
                System.err.println("I/O Error\n" + "The system cannot find the path specified");
            }

            this.host = prop.getProperty("host");
            this.masterPort = Integer.parseInt(prop.getProperty("masterPort"));
            System.out.println(Integer.parseInt(prop.getProperty("masterPort")));
            this.automatic = Boolean.parseBoolean(prop.getProperty("automatic"));


            int answer;
            do {

                if (automatic){
                    answer = 1;
                    break;
                }
                System.out.print("DummyUser " + id + ": 1.New User, 2.Existing User\n\t-> ");
                System.out.println("Please select\n");
                answer = getInput();
            } while (answer != 1 && answer != 2);

            switch (answer) {
                case 1: {
                    init();
                    System.out.println("DummyUser " + id + " created new user!");
                    break;
                }
                case 2: {
                    System.out.println("DummyUser " + id + " welcome back!");
                    break;
                }
            }
        }catch (Exception e){
            System.err.println("Login Error:\n " + " Dummy User " + id + " couln't login");
        }
    }

    public void connectMaster(){
        Chunk chunk= null ;
        try{
            toMasterSocket = new Socket(DummyUser.host,DummyUser.masterPort);
            outToMaster = new ObjectOutputStream(toMasterSocket.getOutputStream());


            try{
                String jsonData = new String(Files.readAllBytes(Paths.get("src/main/java/org/example/backend/data/sampleRoom.json")));
                JSONObject jsonObject = new JSONObject(jsonData);
                chunk = new Chunk(String.valueOf(this.id),1,(Object) jsonObject.toString());
                outToMaster.writeObject(chunk);
                outToMaster.flush();
                System.out.println("files are sent to master!");
            } catch (IOException | JSONException e) {
                System.err.println("Json file not found");
            e.printStackTrace();
            }


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                inFromMaster.close();
                outToMaster.close();
                toMasterSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void init(){

    }


    private int getInput(){
        Scanner input = new Scanner(System.in);

        int get = 0;
        try{
            get = input.nextInt();
            return get;
        }catch (Exception e){
            get = 0;
        }

        input.close();
        return get;
    }

    public static void main(String[] args) {
        int numOfUsers = 2;
          DummyUser dummyUser = new DummyUser(1);
          dummyUser.start();
       /* DummyUser[] dummyUsers = new DummyUser[numOfUsers];
        for (int i = 1; i <= numOfUsers; i++) {
            dummyUsers[i-1] = new DummyUser(i);
            dummyUsers[i-1].start();
        }*/
    }
}