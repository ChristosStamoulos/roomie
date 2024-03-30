import java.io.*;
import java.net.*;
import java.util.Properties;

public class Master extends Thread{
    public static int num_of_workers;
    private static String host;
    private static int workerPort;
    private static int ReducerPort;

     public static void init(){
        Properties prop = new Properties();
        String filename = "app\\backend\\config\\master.config";

        try (FileInputStream f = new FileInputStream(filename)){
            prop.load(f);
        }catch (IOException exception ) {
            System.err.println("I/O Error\n" + "The system cannot find the path specified");
        }

        Master.host = prop.getProperty("host");
        Master.workerPort = Integer.parseInt(prop.getProperty("worker_port"));
        Master.ReducerPort = Integer.parseInt(prop.getProperty("reducer_port"));
        Master.num_of_workers = Integer.parseInt(prop.getProperty("numberOfWorkers"));
        System.out.println(Integer.parseInt(prop.getProperty("worker_port")));
    }


    public void run(){
         Socket workerSocket=null;
         ObjectOutputStream outWorker=null;
         ObjectInputStream  inWorker=null;

         try{
            workerSocket = new Socket(Master.host,Master.workerPort);
            outWorker = new ObjectOutputStream(workerSocket.getOutputStream());
			inWorker = new ObjectInputStream(workerSocket.getInputStream());
            System.out.println(inWorker.readUTF());

         } catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				inWorker.close();	
                outWorker.close();
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