import java.io.*;
import java.net.*;

public class MySocketProgram {
    public static void main(String[] args) {
        // Define the host and port you want to connect to
        String host = "localhost"; // Change this to the host you want to connect to
        int port = 8080; // Change this to the port you want to connect to

        try {
            // Create a new socket object
            Socket socket = new Socket(host, port);

            // Now you can use the socket to communicate with the server
            // For example, you can get input and output streams from the socket
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            // Remember to close the socket when you're done
            socket.close();
        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }
}
