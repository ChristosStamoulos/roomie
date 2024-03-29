import java.io.*;
import java.net.*;

public class ActionsForClients extends Thread {
	ObjectInputStream in;
	ObjectOutputStream out;

	public ActionsForClients(Socket connection) {
		try {
			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {

			out.writeUTF("Hi blaka");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}
