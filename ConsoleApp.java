public class ConsoleApp {
    

    private static void printMenu(){
        System.out.println(
        """
            1. Add room
            2. Add more available dates for your rooms.
            3. See the reservations of your rooms.
                """);
    }
    public static void main(String[] args) {
        printMenu();
    }
}
