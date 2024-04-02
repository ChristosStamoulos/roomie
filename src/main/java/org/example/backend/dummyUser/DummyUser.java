package org.example.backend.dummyUser;

import org.example.backend.domain.Chunk;
import org.example.backend.domain.Master;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;



/** DummyUser Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024
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