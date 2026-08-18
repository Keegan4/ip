import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the Panda task manager's command-line interface.
 *
 * Written by Codex: Coordinates command parsing, task storage, and user-facing output.
 */
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
        // Written by Codex: Let ArrayList grow as tasks are added and manage element removal.
        ArrayList<Task> tasks = new ArrayList<>();
        // Written by Codex: Treat a closed input stream as a graceful end to the session.
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            if (msg.equals("bye")) {
                break;
            }
            System.out.println(divider);
            try {
                if (msg.equals("list")) {
                    // Written by Codex: Show every task with its current completion marker.
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        Task task = tasks.get(i);
                        String status = task.isDone() ? "X" : " ";
                        System.out.printf("%d.[%s][%s] %s%n", i + 1, task.getTypeMarker(), status,
                                task.getDisplayText());
                    }
                } else if (msg.equals("mark") || msg.startsWith("mark ")) {
                    // Written by Codex: Convert invalid mark arguments into a PandaException.
                    int taskNumber = parseTaskNumber(msg, "mark", tasks.size());
                    Task task = tasks.get(taskNumber - 1);
                    task.mark();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.printf("  [X] %s%n", task.getName());
                } else if (msg.equals("unmark") || msg.startsWith("unmark ")) {
                    // Written by Codex: Convert invalid unmark arguments into a PandaException.
                    int taskNumber = parseTaskNumber(msg, "unmark", tasks.size());
                    Task task = tasks.get(taskNumber - 1);
                    task.unmark();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.printf("  [ ] %s%n", task.getName());
                } else if (msg.equals("delete") || msg.startsWith("delete ")) {
                    // Written by Codex: Let ArrayList remove the task and close the index gap.
                    int taskNumber = parseTaskNumber(msg, "delete", tasks.size());
                    Task removedTask = tasks.remove(taskNumber - 1);
                    String status = removedTask.isDone() ? "X" : " ";
                    System.out.println("Noted. I've removed this task:");
                    System.out.printf("  [%s][%s] %s%n", removedTask.getTypeMarker(), status,
                            removedTask.getDisplayText());
                    System.out.printf("Now you have %d tasks in the list.%n", tasks.size());
                } else if (msg.equals("event") || msg.startsWith("event ")) {
                    // Written by Codex: Split event input without validating either date/time value.
                    String eventDetails = msg.substring("event".length()).trim();
                    ensureDescription(eventDetails, "event");
                    // Written by Codex: Treat a leading time marker as a missing event description.
                    if (eventDetails.startsWith("/from ") || eventDetails.startsWith("from ")) {
                        throw new EmptyDescriptionException("event");
                    }
                    String fromSeparator = eventDetails.contains(" /from ") ? " /from " : " from ";
                    String toSeparator = eventDetails.contains(" /to ") ? " /to " : " to ";
                    int fromIndex = eventDetails.indexOf(fromSeparator);
                    int toIndex = eventDetails.indexOf(toSeparator,
                            fromIndex < 0 ? 0 : fromIndex + fromSeparator.length());
                    if (fromIndex <= 0 || toIndex <= fromIndex + fromSeparator.length()
                            || toIndex + toSeparator.length() >= eventDetails.length()) {
                        throw new MissingDateTimeException(
                                "event <description> /from <start> /to <end>.");
                    } else {
                        String taskName = eventDetails.substring(0, fromIndex).trim();
                        String from = eventDetails.substring(fromIndex + fromSeparator.length(), toIndex).trim();
                        String to = eventDetails.substring(toIndex + toSeparator.length()).trim();
                        Task task = new Event(taskName, from, to);
                        tasks.add(task);
                        System.out.println("Got it. I've added this task:");
                        System.out.printf("  [%s][ ] %s%n", task.getTypeMarker(), task.getDisplayText());
                        System.out.printf("Now you have %d tasks in the list.%n", tasks.size());
                    }
                } else if (msg.equals("deadline") || msg.startsWith("deadline ")) {
                    // Written by Codex: Split deadline input without validating the date/time text.
                    String deadlineDetails = msg.substring("deadline".length()).trim();
                    ensureDescription(deadlineDetails, "deadline");
                    // Written by Codex: Treat a leading time marker as a missing deadline description.
                    if (deadlineDetails.startsWith("/by ") || deadlineDetails.startsWith("by ")) {
                        throw new EmptyDescriptionException("deadline");
                    }
                    String separator = deadlineDetails.contains(" /by ") ? " /by " : " by ";
                    int separatorIndex = deadlineDetails.indexOf(separator);
                    if (separatorIndex <= 0 || separatorIndex + separator.length() >= deadlineDetails.length()) {
                        throw new MissingDateTimeException(
                                "deadline <description> /by <date or time>.");
                    } else {
                        String taskName = deadlineDetails.substring(0, separatorIndex).trim();
                        String by = deadlineDetails.substring(separatorIndex + separator.length()).trim();
                        Task task = new Deadline(taskName, by);
                        tasks.add(task);
                        System.out.println("Got it. I've added this task:");
                        System.out.printf("  [%s][ ] %s%n", task.getTypeMarker(), task.getDisplayText());
                        System.out.printf("Now you have %d tasks in the list.%n", tasks.size());
                    }
                } else if (msg.equals("todo") || msg.startsWith("todo ")) {
                    // Written by Codex: Reject a Todo that has no description to store.
                    String taskName = msg.substring("todo".length()).trim();
                    ensureDescription(taskName, "todo");
                    Task task = new Todo(taskName);
                    tasks.add(task);
                    System.out.println("Got it. I've added this task:");
                    System.out.printf("  [%s][ ] %s%n", task.getTypeMarker(), task.getName());
                    System.out.printf("Now you have %d tasks in the list.%n", tasks.size());
                } else {
                    // Written by Codex: Represent unknown input with a dedicated exception.
                    throw new InvalidCommandException();
                }
            } catch (PandaException exception) {
                // Written by Codex: Show expected input errors and continue accepting commands.
                System.out.println(exception.getMessage());
            }
            System.out.println(divider);
        }
        System.out.println(divider);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(divider);
    }

    /**
     * Parses and validates the one-based task number used by mark, unmark, and delete.
     *
     * Written by Codex: Translate Java number-format failures into a domain-specific error.
     *
     * @param message the complete user command
     * @param command the command name
     * @param taskListSize the number of tasks currently stored
     * @return the valid one-based task number
     * @throws InvalidTaskNumberException if the argument is missing, non-numeric, or out of range
     */
    private static int parseTaskNumber(String message, String command, int taskListSize)
            throws InvalidTaskNumberException {
        String taskNumberText = message.substring(command.length()).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new InvalidTaskNumberException(command);
        }
        if (taskNumber < 1 || taskNumber > taskListSize) {
            throw new InvalidTaskNumberException(taskNumber);
        }
        return taskNumber;
    }

    /**
     * Ensures a task-creation command includes a description.
     *
     * Written by Codex: Reuse one validation rule for Todo, Deadline, and Event.
     *
     * @param description the parsed task description
     * @param taskType the command's task type
     * @throws EmptyDescriptionException if the description is blank
     */
    private static void ensureDescription(String description, String taskType)
            throws EmptyDescriptionException {
        if (description.isBlank()) {
            throw new EmptyDescriptionException(taskType);
        }
    }
}
