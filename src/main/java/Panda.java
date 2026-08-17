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

        // Introduction
        System.out.println("Hello! I'm Panda.");
        System.out.println("What can I do for you?");

        // Main message loop
        Scanner scanner = new Scanner(System.in);
        String[] messages = new String[100];
        int messageCount = 0;
        String msg = scanner.nextLine();
        while (!msg.equals("bye")) {
            System.out.println(divider);
            if (msg.equals("list")) {
                // The case for list
                for (int i = 0; i < messageCount; i++) {
                    System.out.printf("%d. %s%n", i + 1, messages[i]);
                }
            }
            else {
                // General Echo case

                System.out.println(msg);
                messages[messageCount] = msg;
                messageCount++;

            }
            System.out.println(divider);
            msg = scanner.nextLine();
        }
        System.out.println(divider);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(divider);
    }
}
