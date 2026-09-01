package panda.ui;

import java.util.List;
import java.util.Scanner;

import panda.exception.PandaException;
import panda.task.Task;

/**
 * Handles all command-line input and output for Panda.
 * Keeping presentation here allows the application coordinator to focus on
 * command execution instead of console formatting details.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = """
             ____    _    _   _ ____    _
            |  _ \\  / \\  | \\ | |  _ \\  / \\
            | |_) |/ _ \\ |  \\| | | | |/ _ \\
            |  __// ___ \\| |\\  | |_| / ___ \\
            |_|  /_/   \\_\\_| \\_|____/_/   \\_\\
            """;

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Panda's greeting.
     */
    public String showWelcome() {
        String welcomeMessage = BANNER + "\nHello! I'm Panda.\nWhat can I do for you?";
        System.out.println(DIVIDER);
        System.out.println(welcomeMessage);
        return welcomeMessage;
    }

    /**
     * Reports whether another command is available from the input stream.
     *
     * @return true when another complete input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the complete command line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays errors found while loading stored tasks.
     *
     * @param errors the loading errors to present.
     */
    public void showLoadingErrors(List<PandaException> errors) {
        showDivider();
        for (PandaException error : errors) {
            showError(error);
        }
        showDivider();
    }

    /**
     * Displays a divider between command responses.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays the heading used for full and filtered task lists.
     */
    public String showTaskListHeader() {
        String response = "Here are the tasks in your list:\n";
        System.out.print(response);
        return response;
    }

    /**
     * Displays the heading used for task-name search results.
     */
    public String showMatchingTaskListHeader() {
        String response = "Here are the matching tasks in your list:\n";
        System.out.print(response);
        return response;
    }

    /**
     * Displays one task with its original one-based task number.
     *
     * @param taskNumber the task's one-based position in the complete list.
     * @param task the task to display.
     */
    public String showTask(int taskNumber, Task task) {
        String status = task.isDone() ? "X" : " ";
        String response = String.format("%d.[%s][%s] %s%n", taskNumber, task.getTypeMarker(),
                status, task.getDisplayText());
        System.out.print(response);
        return response;
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task the task that was marked.
     */
    public String showMarked(Task task) {
        String response = String.format(
                "Nice! I've marked this task as done:%n  [X] %s%n", task.getName());
        System.out.print(response);
        return response;
    }

    /**
     * Displays confirmation that a task was marked as unfinished.
     *
     * @param task the task that was unmarked.
     */
    public String showUnmarked(Task task) {
        String response = String.format(
                "OK, I've marked this task as not done yet:%n  [ ] %s%n", task.getName());
        System.out.print(response);
        return response;
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task the removed task.
     * @param remainingTaskCount the number of tasks left in the list.
     */
    public String showDeleted(Task task, int remainingTaskCount) {
        String status = task.isDone() ? "X" : " ";
        String response = String.format(
                "Noted. I've removed this task:%n  [%s][%s] %s%n%s",
                task.getTypeMarker(), status, task.getDisplayText(),
                formatTaskCount(remainingTaskCount));
        System.out.print(response);
        return response;
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task the new task.
     * @param taskCount the new total number of tasks.
     */
    public String showAdded(Task task, int taskCount) {
        String response = String.format(
                "Got it. I've added this task:%n  [%s][ ] %s%n%s",
                task.getTypeMarker(), task.getDisplayText(), formatTaskCount(taskCount));
        System.out.print(response);
        return response;
    }

    /**
     * Displays an expected user-facing error.
     *
     * @param exception the expected application error.
     */
    public String showError(PandaException exception) {
        System.out.println(exception.getMessage());
        return exception.getMessage();
    }

    /**
     * Displays Panda's closing message.
     */
    public String showGoodbye() {
        showDivider();
        System.out.println("Bye. Hope to see you again soon!");
        showDivider();
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Formats the total task count after a list mutation.
     *
     * @param taskCount the current number of tasks.
     * @return the grammatically correct task-count message.
     */
    private String formatTaskCount(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        return String.format("Now you have %d %s in the list.%n", taskCount, taskNoun);
    }
}
