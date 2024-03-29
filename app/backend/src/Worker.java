import java.io.*;
import java.net.*;

public class Worker {

    public static void main(String[] args) {
        new Worker().openServer();

    }
    ServerSocket providerSocket;
	Socket connection = null;

    void openServer() {
		try {
			providerSocket = new ServerSocket(1234, 10);

			while (true) {
				connection = providerSocket.accept();

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