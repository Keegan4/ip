import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

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

    /**
     * Starts the Panda chatbot and displays its greeting and closing messages.
     *  The ascii art and dividers are created by codex.
     * @param args an optional first argument overriding the data file path
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
        // Written by Codex: Allow tests to supply a fixture while retaining the existing default path.
        Path dataFile = args.length > 0 ? Path.of(args[0]) : DEFAULT_DATA_FILE_PATH;
        ArrayList<DataLoadingException> loadingErrors = new ArrayList<>();
        try {
            loadingErrors = loadTasks(tasks, dataFile);
        } catch (DataLoadingException exception) {
            // Written by Codex: Treat a file-access failure as a startup loading error.
            loadingErrors.add(exception);
        }
        if (!loadingErrors.isEmpty()) {
            // Written by Codex: Prompt the user about every invalid record at the UI boundary.
            System.out.println(divider);
            for (DataLoadingException loadingError : loadingErrors) {
                System.out.println(loadingError.getMessage());
            }
            System.out.println(divider);
        }
        // Written by Codex: Treat a closed input stream as a graceful end to the session.
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            if (Command.BYE.matches(msg)) {
                break;
            }
            System.out.println(divider);
            try {
                // Written by Codex: Convert the message into a fixed command before dispatching it.
                Command command = Command.fromMessage(msg);
                switch (command) {
                case LIST -> {
                    // Written by Codex: Show every task with its current completion marker.
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        Task task = tasks.get(i);
                        String status = task.isDone() ? "X" : " ";
                        System.out.printf("%d.[%s][%s] %s%n", i + 1, task.getTypeMarker(), status,
                                task.getDisplayText());
                    }
                }
                case MARK -> {
                    // Written by Codex: Convert invalid mark arguments into a PandaException.
                    int taskNumber = parseTaskNumber(msg, command, tasks.size());
                    Task task = tasks.get(taskNumber - 1);
                    task.mark();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.printf("  [X] %s%n", task.getName());
                    saveTasks(tasks, dataFile);
                }
                case UNMARK -> {
                    // Written by Codex: Convert invalid unmark arguments into a PandaException.
                    int taskNumber = parseTaskNumber(msg, command, tasks.size());
                    Task task = tasks.get(taskNumber - 1);
                    task.unmark();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.printf("  [ ] %s%n", task.getName());
                    saveTasks(tasks, dataFile);
                }
                case DELETE -> {
                    // Written by Codex: Let ArrayList remove the task and close the index gap.
                    int taskNumber = parseTaskNumber(msg, command, tasks.size());
                    Task removedTask = tasks.remove(taskNumber - 1);
                    String status = removedTask.isDone() ? "X" : " ";
                    System.out.println("Noted. I've removed this task:");
                    System.out.printf("  [%s][%s] %s%n", removedTask.getTypeMarker(), status,
                            removedTask.getDisplayText());
                    System.out.printf("Now you have %d tasks in the list.%n", tasks.size());
                    saveTasks(tasks, dataFile);
                }
                case EVENT -> {
                    // Written by Codex: Split event input without validating either date/time value.
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
                        System.out.println("Got it. I've added this task:");
                        System.out.printf("  [%s][ ] %s%n", task.getTypeMarker(), task.getDisplayText());
                        System.out.printf("Now you have %d tasks in the list.%n", tasks.size());
                        saveTasks(tasks, dataFile);
                    }
                }
                case DEADLINE -> {
                    // Written by Codex: Split deadline input without validating the date/time text.
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
                        System.out.println("Got it. I've added this task:");
                        System.out.printf("  [%s][ ] %s%n", task.getTypeMarker(), task.getDisplayText());
                        System.out.printf("Now you have %d tasks in the list.%n", tasks.size());
                        saveTasks(tasks, dataFile);
                    }
                }
                case TODO -> {
                    // Written by Codex: Reject a Todo that has no description to store.
                    String taskName = msg.substring(command.getKeyword().length()).trim();
                    ensureDescription(taskName, command);
                    Task task = new Todo(taskName);
                    tasks.add(task);
                    System.out.println("Got it. I've added this task:");
                    System.out.printf("  [%s][ ] %s%n", task.getTypeMarker(), task.getName());
                    System.out.printf("Now you have %d tasks in the list.%n", tasks.size());
                    saveTasks(tasks, dataFile);
                }
                case BYE -> throw new IllegalStateException("The bye command should exit before dispatch.");
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

    /**
     * Loads all valid task records from a data file into the supplied task list.
     *
     * Written by Codex: Keep valid records while skipping malformed records,
     * and use a temporary list so file-access failures do not load partial data.
     *
     * @param tasks the application's task list
     * @param dataFile the file containing stored task records
     * @return the errors for malformed records that were skipped
     * @throws DataLoadingException if the file exists but cannot be read
     */
    private static ArrayList<DataLoadingException> loadTasks(ArrayList<Task> tasks, Path dataFile)
            throws DataLoadingException {
        ArrayList<DataLoadingException> loadingErrors = new ArrayList<>();
        if (Files.notExists(dataFile)) {
            // Written by Codex: A first run has no data file and should start with an empty list.
            return loadingErrors;
        }

        ArrayList<Task> loadedTasks = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(dataFile, StandardCharsets.UTF_8)) {
            int lineNumber = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                lineNumber++;
                if (!line.isBlank()) {
                    try {
                        loadedTasks.add(parseStoredTask(line, lineNumber));
                    } catch (DataLoadingException exception) {
                        // Written by Codex: Record this invalid line for the UI, then continue loading.
                        loadingErrors.add(exception);
                    }
                }
            }
        } catch (IOException exception) {
            throw new DataLoadingException(dataFile.toString(), exception);
        }
        tasks.addAll(loadedTasks);
        return loadingErrors;
    }

    /**
     * Converts one pipe-separated data record into its corresponding Task subtype.
     *
     * Written by Codex: Validate the stored type, status, and required fields
     * before creating a task object.
     *
     * @param line one complete line from the data file
     * @param lineNumber the one-based line number used in error messages
     * @return the task represented by the stored record
     * @throws DataLoadingException if the record does not follow the storage format
     */
    private static Task parseStoredTask(String line, int lineNumber)
            throws DataLoadingException {
        String[] fields = splitStoredFields(line);
        if (fields.length < 3 || fields[2].isBlank()) {
            throw new DataLoadingException(lineNumber, "no task description.");
        }

        Task task = switch (fields[0]) {
        case "T" -> {
            ensureStoredFieldCount(fields, 3, lineNumber, "todo");
            yield new Todo(fields[2]);
        }
        case "D" -> {
            ensureStoredFieldCount(fields, 4, lineNumber, "deadline");
            ensureStoredValue(fields[3], lineNumber, "no deadline time.");
            yield new Deadline(fields[2], fields[3]);
        }
        case "E" -> {
            ensureStoredFieldCount(fields, 5, lineNumber, "event");
            ensureStoredValue(fields[3], lineNumber, "no event start time.");
            ensureStoredValue(fields[4], lineNumber, "no event end time.");
            yield new Event(fields[2], fields[3], fields[4]);
        }
        default -> throw new DataLoadingException(lineNumber,
                "an invalid task type; expected T, D, or E.");
        };

        if (fields[1].equals("1")) {
            task.mark();
        } else if (!fields[1].equals("0")) {
            throw new DataLoadingException(lineNumber,
                    "an invalid completion status; expected 0 or 1.");
        }
        return task;
    }

    /**
     * Checks that a stored record has the number of fields required by its type.
     *
     * @param fields the parsed record fields
     * @param expectedCount the required number of fields
     * @param lineNumber the data file line number
     * @param taskType the task type used in the error message
     * @throws DataLoadingException if the field count is incorrect
     */
    private static void ensureStoredFieldCount(String[] fields, int expectedCount,
            int lineNumber, String taskType) throws DataLoadingException {
        if (fields.length != expectedCount) {
            throw new DataLoadingException(lineNumber,
                    "an invalid " + taskType + " field count; expected "
                            + expectedCount + " fields.");
        }
    }

    /**
     * Checks that a required stored field contains a value.
     *
     * @param value the stored field value
     * @param lineNumber the data file line number
     * @param errorMessage the reason reported when the field is blank
     * @throws DataLoadingException if the value is blank
     */
    private static void ensureStoredValue(String value, int lineNumber, String errorMessage)
            throws DataLoadingException {
        if (value.isBlank()) {
            throw new DataLoadingException(lineNumber, errorMessage);
        }
    }

    /**
     * Splits a stored record while preserving escaped pipes and backslashes.
     *
     * Written by Codex: Read user-supplied delimiter characters without
     * mistaking them for boundaries between stored fields.
     *
     * @param line one stored task record
     * @return the decoded fields in the record
     */
    private static String[] splitStoredFields(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (escaping) {
                if (character != '\\' && character != '|') {
                    currentField.append('\\');
                }
                currentField.append(character);
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }
        if (escaping) {
            currentField.append('\\');
        }
        fields.add(currentField.toString().trim());
        return fields.toArray(String[]::new);
    }

    /**
     * Rewrites the data file so it exactly matches the in-memory task list.
     *
     * Written by Codex: Saving the complete list makes additions, status
     * changes, and deletions persistent through the same simple operation.
     *
     * @param tasks the current tasks in display order
     * @param dataFile the file that stores the tasks
     * @throws DataSavingException if the destination cannot be created or written
     */
    private static void saveTasks(ArrayList<Task> tasks, Path dataFile)
            throws DataSavingException {
        StringBuilder storedData = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                storedData.append(System.lineSeparator());
            }
            storedData.append(tasks.get(i).toDataString());
        }

        try {
            Path parentDirectory = dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.writeString(dataFile, storedData, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DataSavingException(exception);
        }
    }
}
