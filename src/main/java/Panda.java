import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Runs the Panda task manager's command-line interface.
 *
 * Written by Codex: Coordinates command parsing, task storage, and user-facing output.
 */
public class Panda {
    /**
     * Written by Codex: Build a relative default path from OS-independent components.
     */
    private static final Path DEFAULT_DATA_FILE_PATH =
            Path.of("src", "main", "data", "info.txt");

    private final Storage storage;
    private final Ui ui;

    /**
     * Creates Panda with a user interface and storage for the supplied file.
     *
     * @param filePath the task data file path
     */
    public Panda(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
    }

    /**
     * Starts Panda using the default data file or an optional test file.
     *
     * @param args an optional first argument overriding the data file path
     */
    public static void main(String[] args) {
        String filePath = args.length > 0
                ? args[0] : DEFAULT_DATA_FILE_PATH.toString();
        new Panda(filePath).run();
    }

    /**
     * Loads tasks and runs the command-processing loop.
     *
     */
    public void run() {
        ui.showWelcome();

        // Main message loop
        // Written by Codex: Let ArrayList grow as tasks are added and manage element removal.
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<PandaException> loadingErrors = new ArrayList<>();
        try {
            Storage.LoadResult loadResult = storage.load();
            tasks.addAll(loadResult.tasks());
            loadingErrors.addAll(loadResult.errors());
        } catch (DataLoadingException exception) {
            // Written by Codex: Treat a file-access failure as a startup loading error.
            loadingErrors.add(exception);
        }
        if (!loadingErrors.isEmpty()) {
            ui.showLoadingErrors(loadingErrors);
        }
        // Written by Codex: Treat a closed input stream as a graceful end to the session.
        while (ui.hasNextCommand()) {
            String msg = ui.readCommand();
            if (Command.BYE.matches(msg)) {
                break;
            }
            ui.showDivider();
            try {
                // Written by Codex: Convert the message into a fixed command before dispatching it.
                Command command = Command.fromMessage(msg);
                switch (command) {
                case LIST -> {
                    String dateText = msg.substring(command.getKeyword().length()).trim();
                    LocalDate filterDate = dateText.isEmpty()
                            ? null : Task.processListDate(dateText);
                    ui.showTaskListHeader();
                    for (int i = 0; i < tasks.size(); i++) {
                        Task task = tasks.get(i);
                        if (filterDate != null && !task.occursOn(filterDate)) {
                            continue;
                        }
                        ui.showTask(i + 1, task);
                    }
                }
                case MARK -> {
                    // Written by Codex: Convert invalid mark arguments into a PandaException.
                    int taskNumber = parseTaskNumber(msg, command, tasks.size());
                    Task task = tasks.get(taskNumber - 1);
                    task.mark();
                    ui.showMarked(task);
                    storage.save(tasks);
                }
                case UNMARK -> {
                    // Written by Codex: Convert invalid unmark arguments into a PandaException.
                    int taskNumber = parseTaskNumber(msg, command, tasks.size());
                    Task task = tasks.get(taskNumber - 1);
                    task.unmark();
                    ui.showUnmarked(task);
                    storage.save(tasks);
                }
                case DELETE -> {
                    // Written by Codex: Let ArrayList remove the task and close the index gap.
                    int taskNumber = parseTaskNumber(msg, command, tasks.size());
                    Task removedTask = tasks.remove(taskNumber - 1);
                    ui.showDeleted(removedTask, tasks.size());
                    storage.save(tasks);
                }
                case EVENT -> {
                    // Split event input before its constructor validates both date/time values.
                    String eventDetails = msg.substring(command.getKeyword().length()).trim();
                    ensureDescription(eventDetails, command);
                    // Written by Codex: Treat a leading time marker as a missing event description.
                    if (eventDetails.startsWith("/from ") || eventDetails.startsWith("from ")) {
                        throw new EmptyDescriptionException(command.getKeyword());
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
                        ui.showAdded(task, tasks.size());
                        storage.save(tasks);
                    }
                }
                case DEADLINE -> {
                    // Split deadline input before its constructor validates the date/time value.
                    String deadlineDetails = msg.substring(command.getKeyword().length()).trim();
                    ensureDescription(deadlineDetails, command);
                    // Written by Codex: Treat a leading time marker as a missing deadline description.
                    if (deadlineDetails.startsWith("/by ") || deadlineDetails.startsWith("by ")) {
                        throw new EmptyDescriptionException(command.getKeyword());
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
                        ui.showAdded(task, tasks.size());
                        storage.save(tasks);
                    }
                }
                case TODO -> {
                    // Written by Codex: Reject a Todo that has no description to store.
                    String taskName = msg.substring(command.getKeyword().length()).trim();
                    ensureDescription(taskName, command);
                    Task task = new Todo(taskName);
                    tasks.add(task);
                    ui.showAdded(task, tasks.size());
                    storage.save(tasks);
                }
                case BYE -> throw new IllegalStateException("The bye command should exit before dispatch.");
                }
            } catch (PandaException exception) {
                // Written by Codex: Show expected input errors and continue accepting commands.
                ui.showError(exception);
            }
            ui.showDivider();
        }
        ui.showGoodbye();
    }

    /**
     * Parses and validates the one-based task number used by mark, unmark, and delete.
     *
     * Written by Codex: Translate Java number-format failures into a domain-specific error.
     *
     * @param message the complete user command
     * @param command the command being processed
     * @param taskListSize the number of tasks currently stored
     * @return the valid one-based task number
     * @throws InvalidTaskNumberException if the argument is missing, non-numeric, or out of range
     */
    private static int parseTaskNumber(String message, Command command, int taskListSize)
            throws InvalidTaskNumberException {
        String taskNumberText = message.substring(command.getKeyword().length()).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new InvalidTaskNumberException(command.getKeyword());
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
     * @param command the task-creation command being processed
     * @throws EmptyDescriptionException if the description is blank
     */
    private static void ensureDescription(String description, Command command)
            throws EmptyDescriptionException {
        if (description.isBlank()) {
            throw new EmptyDescriptionException(command.getKeyword());
        }
    }
}
