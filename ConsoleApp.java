import java.util.*;

public class ConsoleApp {

    private static void printMenu(){
        System.out.println(
        """
            1. Add room
            2. Add more available dates for your rooms.
            3. See the reservations of your rooms.
            4. Exit
                """);
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while(true){
            printMenu();
            System.out.print("Your choice: ");
            int choice = Integer.parseInt(in.nextLine());
            
            switch (choice){
                case 1:
                    System.out.println("Give the relative path to the file with the room data");
                    String roomPath = in.nextLine();
                    break;
                case 2:
                break;
                case 3:
                break;
                case 4:
                    System.exit(0);
            }
        }
        
        
    }
}
