package org.example.backend.domain;

import org.example.backend.utils.Pair;
import org.example.backend.utils.SimpleCalendar;
import org.example.backend.utils.json.JsonConverter;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

/** Worker Class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to handle requests from the Master and to map the data assigned from the Master.
 */

public class Worker {
    private static int id = 0;
    private static int reducerPort;
    private static int serverPort;
    private static String reducerHost;

    public static void init() {
        Properties prop = new Properties();
        String filename = "src/main/java/org/example/backend/config/worker.config";

        try (FileInputStream f = new FileInputStream(filename)) {
            prop.load(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        reducerPort = Integer.parseInt(prop.getProperty("reducerPort"));
        reducerHost = prop.getProperty("reducerHost");
    }

    public static void main(String[] args) {
        init();
        openServer();
    }

    private static void openServer() {
        try (ServerSocket providerSocket = new ServerSocket(serverPort, 100);
             Socket reducerSocket = new Socket(reducerHost, reducerPort)) {

            while (true) {
                Socket masterConnection = providerSocket.accept();
                System.out.println("Master connected");
                new ActionsForWorkers(masterConnection, reducerSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
