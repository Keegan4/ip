import java.util.Scanner;
public class Panda {
    /**
     * Starts the Panda chatbot and displays its greeting and closing messages.
     *  The ascii art and dividers are created by codex.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        String divider = "____________________________________________________________";
        String banner = """
                 ____    _    _   _ ____    _
                |  _ \\  / \\  | \\ | |  _ \\  / \\
                | |_) |/ _ \\ |  \\| | | | |/ _ \\
                |  __// ___ \\| |\\  | |_| / ___ \\
                |_|  /_/   \\_\\_| \\_|____/_/   \\_\\
                """;

        System.out.println(divider);
        System.out.println(banner);
        System.out.println("Hello! I'm Panda.");
        System.out.println("What can I do for you?");
        Scanner scanner = new Scanner(System.in);
        String msg = scanner.nextLine();
        while (!msg.equals("bye")) {

            System.out.println(msg);
            System.out.println(divider);
            msg = scanner.nextLine();
        }
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(divider);
    }
}
