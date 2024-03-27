//------------------------------------------------------------------Imports------------------------------------------------------------------
import java.net.*;
import java.io.*;
import java.util.*;
//----------------------------------------------------------------Master-Class---------------------------------------------------------------
public class Master{
    public static int num_of_workers;
    private static ObjectInputStream in; // Socket for Master to listen the requests from the Master
    private static ObjectOutputStream out; // Socket for each request to send the result to the Reducer
    private static int worker_port; // Port to listen the request
    private static int reducer_port; // Port to send the result
    private static int user_port;
    private Chunk chunk;
    ServerSocket workerSocket;
    ServerSocket reducerSocket;
    ServerSocket clientSocket;

    ServerSocket providerSocket;
    Socket socket = null;

    public static void main(String[] args){
        new Master().openMaster();
    }

    void openMaster(){
        try {
			providerSocket = new ServerSocket(52153);

			while (true) {
				socket = providerSocket.accept();

				//Thread t = new ActionsForClients(connection);
				//t.start();

			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				providerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}