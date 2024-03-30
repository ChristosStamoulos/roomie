import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

/** DummyUser Class
 *
 * @authors Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024
 *
 * This class is implemented to represent a user.
 */

public class DummyUser extends Thread{
    private final int id;
    private final String path;
    private String host;
    private int masterPort;
    private boolean automatic = false;

    DummyUser(int id){
        this.id = id;
        this.path = System.getProperty("user.dir") + "\\dummyuser\\UserData\\" + "User" + id + "\\";
    }

    @Override
    public void run() {
        try {
            login();

            

        }catch(Exception e) {

        }finally{

        }
    }


    /* Initialize the user */
    private void login(){
        try {
            Properties prop = new Properties();
            String filename = System.getProperty("user.dir") + "\\app\\backend\\DummyUser\\UserData\\user.config";

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

        DummyUser[] dummyUsers = new DummyUser[numOfUsers];
        for (int i = 1; i <= numOfUsers; i++) {
            dummyUsers[i-1] = new DummyUser(i);
            dummyUsers[i-1].start();
        }
    }
}
