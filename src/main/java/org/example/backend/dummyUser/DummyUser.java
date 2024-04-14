package org.example.backend.dummyUser;

import org.example.backend.domain.Chunk;
import org.example.backend.domain.Master;
import org.example.backend.domain.Room;
import org.example.backend.utils.Pair;
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
import java.util.ArrayList;
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
    private final int id; // unique identifier for each user
    private final String path; // path for the user configurations
    private static String host; // host name of the master
    private static int masterPort; // port to connect with the master
    private Socket masterSocket=null; // socket to connect with the master
    private ObjectOutputStream outToMaster=null; // stream to sent requests to master
    private ObjectInputStream inFromMaster=null; // stream to receive answers from the master
    private static Chunk masterInput=null; // package that includes useful information about the request

    /**
     * @Details Constructor of DummyUser class
     * @param id: The unique identifier of a Dummy User
     */
    DummyUser(int id){
        this.id = id;
        this.path =  "src\\main\\java\\org\\example\\backend\\dummyUser\\userData\\user.config";
    }

    /**
     * @Details Basic method that a subclass of Thread should use. It executes any target function belonging to the given thread object, that is currently active.
     */
    @Override
    public void run() {
        init(); //initialize user data
        ArrayList<Room> rooms = new ArrayList<>();
        int option;

        try {
            masterSocket = new Socket(host, masterPort);
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        while (true) {
            try {
                Chunk chunk = null;
                Scanner in = new Scanner(System.in);

                System.out.println("Please choose what you want to do by typing the corresponding number");
                System.out.println("1. Search by filters");
                System.out.println("2. Book a room");
                System.out.println("3. Rate a room");
                System.out.println("4. Exit");

                int choise = Integer.parseInt(in.nextLine());

                switch (choise) {
                    case 1:
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
                        if (!Objects.equals(place, "none")) jsonObject.getJSONObject("filters").put("area", place);
                        if (!Objects.equals(startDate, "none"))
                            jsonObject.getJSONObject("filters").put("startDate", new SimpleCalendar(startDate).toString());
                        if (!Objects.equals(finishDate, "none"))
                            jsonObject.getJSONObject("filters").put("finishDate", new SimpleCalendar(finishDate).toString());
                        if (!Objects.equals(numberOfPeople, "none"))
                            jsonObject.getJSONObject("filters").put("noOfPeople", Integer.parseInt(numberOfPeople));
                        if (!Objects.equals(lowPrice, "none"))
                            jsonObject.getJSONObject("filters").put("lowPrice", Integer.parseInt(lowPrice));
                        if (!Objects.equals(highPrice, "none"))
                            jsonObject.getJSONObject("filters").put("highPrice", Integer.parseInt(highPrice));
                        if (!Objects.equals(stars, "none"))
                            jsonObject.getJSONObject("filters").put("stars", Double.parseDouble(stars));

                        System.out.println(jsonObject.toString());
                        chunk = new Chunk(String.valueOf(this.id), 1, jsonObject.toString());

                        sentToMaster(masterSocket, chunk);
                        receiveFromMaster(masterSocket);

                        rooms = (ArrayList<Room>) masterInput.getData();

                        break;
                    case 2:
                        System.out.println("Choose the room you want to book. Please type the corresponding number");
                        int roomId = Integer.parseInt(in.nextLine());
                        ArrayList<String> dates = new ArrayList<>();
                        chunk = new Chunk(String.valueOf(this.id), 2, new Pair<Integer, ArrayList<String>>(rooms.get(roomId - 1).getId(), dates));
                        System.out.println("Successful booking of the room");
                        break;
                    case 3:
                        break;
                    case 4:
                        try {
                            outToMaster.close();
                            inFromMaster.close();
                            masterSocket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        System.exit(0);
                        break;
                }


            } catch (Exception e) {
                System.err.println("I/O error interacting with the cmd" + e);
            }
        }
    }

    /**
     * Sends the request with the output stream
     * @param masterSocket: Socket connected with the master
     * @param chunk: Package that includes the useful data of the user's request
     */
    public void sentToMaster(Socket masterSocket, Chunk chunk){
        try{
            outToMaster = new ObjectOutputStream(masterSocket.getOutputStream());
            try{
                outToMaster.writeObject(chunk);
                outToMaster.flush();
                System.out.println("Files are sent to master!");
            } catch (IOException | JSONException e) {
                System.err.println("Json file not found");
                e.printStackTrace();
            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Receives an answer with the input stream
     * @param masterSocket: Socket connected with the master
     */
    public void receiveFromMaster(Socket masterSocket){
        try{
            inFromMaster = new ObjectInputStream(masterSocket.getInputStream());
            try{
                masterInput = (Chunk) inFromMaster.readObject();
                System.out.println("Files are sent back from master!");
            } catch (IOException | JSONException e) {
                System.err.println("Json file not found");
                e.printStackTrace();
            } catch (ClassNotFoundException ex) {
                System.err.println("Class not found" + ex);
            }
            System.out.println((String) masterInput.getData());

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * @Details Initializes the useful data of the user
     */
    private void init(){
        Properties prop = new Properties();

        try (FileInputStream f = new FileInputStream(path)){
            prop.load(f);
        }catch (IOException exception) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified for the path:" + path);
        }

        host = prop.getProperty("host");
        masterPort = Integer.parseInt(prop.getProperty("masterPort"));
        System.out.println(Integer.parseInt(prop.getProperty("masterPort")));
    }

    /**
     * @Details Checks the input from the user, so it satisfies the concept of cover kernel failure
     * @return
     */
    private int checkInput(){
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

    /**
     * @Details Main class of Dummy User
     * @param args: Default parameters
     */
    public static void main(String[] args) {
        //int numOfUsers = 2;
          DummyUser dummyUser = new DummyUser(1);
          dummyUser.start();
        /* DummyUser[] dummyUsers = new DummyUser[numOfUsers];
        for (int i = 1; i <= numOfUsers; i++) {
            dummyUsers[i-1] = new DummyUser(i);
            dummyUsers[i-1].start();
        }*/
    }
}