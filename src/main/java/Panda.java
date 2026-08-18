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
        // Written by Codex: Store each task's name and status in a Task object.
        Task[] tasks = new Task[100];
        int taskCount = 0;
        String msg = scanner.nextLine();
        while (!msg.equals("bye")) {
            System.out.println(divider);
            if (msg.equals("list")) {
                // Written by Codex: Show every task with its current completion marker.
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String status = tasks[i].isDone() ? "X" : " ";
                    System.out.printf("%d.[%s] %s%n", i + 1, status, tasks[i].getName());
                }
            }
            else if (msg.startsWith("mark ")) {
                // Written by Codex: Parse a mark command and update the selected task in-place.
                String taskNumberText = msg.substring("mark ".length()).trim();
                try {
                    int taskNumber = Integer.parseInt(taskNumberText);
                    if (taskNumber >= 1 && taskNumber <= taskCount) {
                        tasks[taskNumber - 1].mark();
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.printf("  [X] %s%n", tasks[taskNumber - 1].getName());
                    } else {
                        System.out.println("I couldn't find that task.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println("Please provide a valid task number.");
                }
            }
            else if (msg.startsWith("unmark ")) {
                // Written by Codex: Reverse the completion state of the selected task.
                String taskNumberText = msg.substring("unmark ".length()).trim();
                try {
                    int taskNumber = Integer.parseInt(taskNumberText);
                    if (taskNumber >= 1 && taskNumber <= taskCount) {
                        tasks[taskNumber - 1].unmark();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.printf("  [ ] %s%n", tasks[taskNumber - 1].getName());
                    } else {
                        System.out.println("I couldn't find that task.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println("Please provide a valid task number.");
                }
            }
            else {
                // Written by Codex: Add ordinary user input as a new, initially unfinished task.
                System.out.println(msg);
                tasks[taskCount] = new Task(msg);
                taskCount++;

            }
            System.out.println(divider);
            msg = scanner.nextLine();
        }
        System.out.println(divider);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(divider);
    }
}
