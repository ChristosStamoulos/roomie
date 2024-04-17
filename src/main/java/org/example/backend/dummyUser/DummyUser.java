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
    private Socket masterSocket = null; // socket to connect with the master
    private ObjectOutputStream outToMaster = null; // stream to sent requests to master
    private ObjectInputStream inFromMaster = null; // stream to receive answers from the master
    private static Chunk masterInput = null; // package that includes useful information about the request

    /**
     * @Details Constructor of DummyUser class
     * @param id: The unique identifier of a Dummy User
     */
    DummyUser(int id){
        this.id = id;
        this.path =  "src\\main\\java\\org\\example\\backend\\dummyUser\\userData\\user.config";
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
    }

    private static void printMenu(){
        System.out.println(
                """
                   Please choose what you want to do by typing the corresponding number
                   1. Search by filters.
                   2. Book a room.
                   3. Rate a room.
                   4. Exit
                       """);
    }

    /**
     * @Details Basic method that a subclass of Thread should use. It executes any target function belonging to the given thread object, that is currently active.
     */
    @Override
    public void run() {
        Scanner in = new Scanner(System.in);

        init(); //initialize user data
        ArrayList<Room> rooms = new ArrayList<>();
        try {
            while (true) {
                Chunk chunk = null;
                printMenu();
                System.out.print("Your choice: ");
                int choice = Integer.parseInt(in.nextLine());

                masterSocket = new Socket(host, masterPort);

                switch (choice) {
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

                        for(int i=0; i< rooms.size(); i++){
                            System.out.println((i+1) + ": " +  rooms.get(i));
                        }

                        break;
                    case 2:
                        ArrayList<SimpleCalendar> dates = new ArrayList<>();

                        System.out.println("Choose the room you want to book. Please type the corresponding number");
                        int roomId = Integer.parseInt(in.nextLine());

                        System.out.println("Please enter the starting date you want (such as '12/11/2024')");
                        String bookStartDate = in.nextLine();
                        System.out.println("Please enter the finishing date you want (such as '24/03/2025')");
                        String bookFinishDate = in.nextLine();

                        SimpleCalendar bookStartDateSimple = new SimpleCalendar(bookStartDate);
                        SimpleCalendar bookFinishDateSimple = new SimpleCalendar(bookFinishDate);

                        // Add each day from the start date to the end date to the list
                        while (bookStartDateSimple.compareTo(bookFinishDateSimple) <= 0) {
                            dates.add(bookStartDateSimple);
                            bookStartDateSimple = bookStartDateSimple.addDays(1); // Move to the next day
                            System.out.println(bookStartDateSimple.toString());
                        }

                        chunk = new Chunk(String.valueOf(this.id), 2, new Pair<>(rooms.get(roomId - 1).getId(), dates));
                        sentToMaster(masterSocket, chunk);

                        System.out.println("Successful booking of the room");
                        break;
                    case 3:
                        System.out.println("Choose the room you want to rate. Please type the corresponding number");
                        int rateId = Integer.parseInt(in.nextLine());

                        System.out.println("Please type the rating. Ratings represent the number of stars so please enter an integer from 1-5");
                        int rating = Integer.parseInt(in.nextLine());

                        chunk = new Chunk(String.valueOf(this.id), 3, new Pair<>(rooms.get(rateId - 1).getId(),rating));
                        sentToMaster(masterSocket, chunk);

                        System.out.println("Successful rating of the room");
                        break;
                    case 4:
                        in.close();
                        System.exit(0);
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                masterSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
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
     * @Details Main class of Dummy User
     * @param args: Default parameters
     */
    public static void main(String[] args) {
        DummyUser dummyUser = new DummyUser(1);
        dummyUser.start();
    }
}