import java.io.*;
import java.net.*;
import java.util.Properties;

public class Worker {
	private final int id;
    private static int masterPort;
    private static int ReducerPort;
	private static int serverPort;

	Worker(int id){
		this.id = id;
	}

	public static void init(){
        Properties prop = new Properties();
        String filename = "app\\backend\\config\\worker.config";

        try (FileInputStream f = new FileInputStream(filename)){
            prop.load(f);
        }catch (IOException exception ) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified");
        }

		Worker.serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        Worker.masterPort = Integer.parseInt(prop.getProperty("masterPort"));
        System.out.println(Integer.parseInt(prop.getProperty("serverPort")));
    }

    public static void main(String[] args) {
		init();
    }
    ServerSocket providerSocket;
	Socket masterConnection = null;

    void openServer() {
		try {
			providerSocket = new ServerSocket(Worker.serverPort, 10);

			while (true) {
				masterConnection = providerSocket.accept();

				Thread t = new ActionsForClients(masterConnection);
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