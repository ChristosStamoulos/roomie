import java.io.*;
import java.net.*;
import java.util.Properties;

public class Worker {
	
    private static String host;
    private static int masterPort;
    private static int ReducerPort;
	private static int serverPort;

	public static void init(){
        Properties prop = new Properties();
        String filename = "app\\backend\\config\\worker.config";

        try (FileInputStream f = new FileInputStream(filename)){
            prop.load(f);
        }catch (IOException exception ) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified");
        }

        Worker.host = prop.getProperty("host");
        Worker.masterPort = Integer.parseInt(prop.getProperty("masterPort"));
		Worker.serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        System.out.println(Integer.parseInt(prop.getProperty("serverPort")));
    }

    public static void main(String[] args) {
        new Worker().openServer();

    }
    ServerSocket providerSocket;
	Socket connection = null;

    void openServer() {
		try {
			providerSocket = new ServerSocket(Worker.serverPort, 10);

			while (true) {
				connection = providerSocket.accept();

				Thread t = new ActionsForClients(connection);
				t.start();

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