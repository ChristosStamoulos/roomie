import java.io.*;
import java.net.*;

public class Master extends Thread{

    public void run(){
         Socket workerSocket=null;
         ObjectOutputStream outWorker=null;
         ObjectInputStream  inWorker=null;

         try{
            workerSocket = new Socket("10.26.47.198",1234);
            outWorker = new ObjectOutputStream(workerSocket.getOutputStream());
			inWorker = new ObjectInputStream(workerSocket.getInputStream());

           

            System.out.println(inWorker.readUTF());

         } catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				inWorker.close();	outWorker.close();
				workerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

    }

    public static void main(String[] args) {
        new Master().start();
    }
}