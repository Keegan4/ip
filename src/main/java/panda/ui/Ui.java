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
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Panda.");
        System.out.println("What can I do for you?");
    }

    /**
     * Reports whether another command is available from the input stream.
     *
     * @return true when another complete input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the complete command line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays errors found while loading stored tasks.
     *
     * @param errors the loading errors to present
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
    public void showTaskListHeader() {
        System.out.println("Here are the tasks in your list:");
    }

    /**
     * Displays the heading used for task-name search results.
     */
    public void showMatchingTaskListHeader() {
        System.out.println("Here are the matching tasks in your list:");
    }

    /**
     * Displays one task with its original one-based task number.
     *
     * @param taskNumber the task's one-based position in the complete list
     * @param task the task to display
     */
    public void showTask(int taskNumber, Task task) {
        String status = task.isDone() ? "X" : " ";
        System.out.printf("%d.[%s][%s] %s%n", taskNumber, task.getTypeMarker(),
                status, task.getDisplayText());
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task the task that was marked
     */
    public void showMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.printf("  [X] %s%n", task.getName());
    }

    /**
     * Displays confirmation that a task was marked as unfinished.
     *
     * @param task the task that was unmarked
     */
    public void showUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.printf("  [ ] %s%n", task.getName());
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task the removed task
     * @param remainingTaskCount the number of tasks left in the list
     */
    public void showDeleted(Task task, int remainingTaskCount) {
        String status = task.isDone() ? "X" : " ";
        System.out.println("Noted. I've removed this task:");
        System.out.printf("  [%s][%s] %s%n", task.getTypeMarker(), status,
                task.getDisplayText());
        showTaskCount(remainingTaskCount);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task the new task
     * @param taskCount the new total number of tasks
     */
    public void showAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.printf("  [%s][ ] %s%n", task.getTypeMarker(), task.getDisplayText());
        showTaskCount(taskCount);
    }

    /**
     * Displays an expected user-facing error.
     *
     * @param exception the expected application error
     */
    public void showError(PandaException exception) {
        System.out.println(exception.getMessage());
    }

    /**
     * Displays Panda's closing message.
     */
    public void showGoodbye() {
        showDivider();
        System.out.println("Bye. Hope to see you again soon!");
        showDivider();
    }

    /**
     * Displays the total task count after a list mutation.
     *
     * @param taskCount the current number of tasks
     */
    private void showTaskCount(int taskCount) {
        System.out.printf("Now you have %d tasks in the list.%n", taskCount);
    }
}
