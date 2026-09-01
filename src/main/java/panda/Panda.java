package panda;

import java.nio.file.Path;
import java.util.List;

import panda.exception.DataLoadingException;
import panda.exception.PandaException;
import panda.parser.Parser;
import panda.storage.Storage;
import panda.task.Task;
import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Runs the Panda task manager and coordinates its user interfaces.
 *
 * Coordinates command parsing, task storage, and user-facing output.
 */
public class Panda {
    /**
     * Builds a relative default path from OS-independent components.
     */
    private static final Path DEFAULT_DATA_FILE_PATH =
            Path.of("src", "main", "data", "info.txt");

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final List<PandaException> loadingErrors;

    /**
     * Creates Panda with a user interface and storage for the supplied file.
     *
     * @param filePath the task data file path.
     */
    public Panda(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        List<PandaException> errors;
        try {
            Storage.LoadResult loadResult = storage.load();
            loadedTasks = new TaskList(loadResult.tasks());
            errors = loadResult.errors();
        } catch (DataLoadingException exception) {
            loadedTasks = new TaskList();
            errors = List.of(exception);
        }
        tasks = loadedTasks;
        loadingErrors = errors;
    }

    /**
     * Starts Panda using the default data file or an optional test file.
     *
     * @param args an optional first argument overriding the data file path.
     */
    public static void main(String[] args) {
        String filePath = args.length > 0
                ? args[0] : DEFAULT_DATA_FILE_PATH.toString();
        new Panda(filePath).run();
    }

    /**
     * Returns Panda's response to one command.
     *
     * @param message the complete user command.
     * @return the response to display.
     */
    public String getResponse(String message) {
        if (isExitCommand(message)) {
            return ui.showGoodbye();
        }

        try {
            Parser.ParsedCommand parsedCommand = parser.parse(message);

            switch (parsedCommand.command()) {
                case LIST:
                    List<TaskList.NumberedTask> displayedTasks =
                            parsedCommand.filterDate() == null
                                    ? tasks.getTasks()
                                    : tasks.getTasksOn(parsedCommand.filterDate());
                    return ui.showTaskList(displayedTasks);
                case FIND:
                    List<TaskList.NumberedTask> matchingTasks =
                            tasks.getTasksMatching(parsedCommand.searchTerm());
                    return ui.showMatchingTaskList(matchingTasks);
                case MARK:
                    Task markedTask = tasks.mark(parsedCommand.taskNumber());
                    storage.save(tasks.getTaskSnapshot());
                    return ui.showMarked(markedTask);
                case UNMARK:
                    Task unmarkedTask = tasks.unmark(parsedCommand.taskNumber());
                    storage.save(tasks.getTaskSnapshot());
                    return ui.showUnmarked(unmarkedTask);
                case DELETE:
                    Task removedTask = tasks.delete(parsedCommand.taskNumber());
                    storage.save(tasks.getTaskSnapshot());
                    return ui.showDeleted(removedTask, tasks.getTaskCount());
                case EVENT, DEADLINE, TODO:
                    Task newTask = parsedCommand.task();
                    tasks.add(newTask);
                    storage.save(tasks.getTaskSnapshot());
                    return ui.showAdded(newTask, tasks.getTaskCount());
                case BYE:
                    throw new IllegalStateException("The bye command should exit before dispatch.");
                default:
                    throw new IllegalStateException("This should not be reachable");
            }
        } catch (PandaException exception) {
            return ui.showError(exception);
        }
    }

    /**
     * Checks whether a message is the exact command that ends the application.
     *
     * @param message the complete user command.
     * @return true only for an argument-free {@code bye} command.
     */
    public boolean isExitCommand(String message) {
        return parser.isExitCommand(message);
    }

    /**
     * Loads tasks and runs the command-processing loop.
     */
    public void run() {
        ui.showWelcome();

        if (!loadingErrors.isEmpty()) {
            ui.showLoadingErrors(loadingErrors);
        }
        // Treat a closed input stream as a graceful end to the session.
        while (ui.hasNextCommand()) {
            String message = ui.readCommand();
            if (parser.isExitCommand(message)) {
                break;
            }
            ui.showDivider();
            try {
                Parser.ParsedCommand parsedCommand = parser.parse(message);
                switch (parsedCommand.command()) {
                    case LIST:
                        List<TaskList.NumberedTask> displayedTasks =
                                parsedCommand.filterDate() == null
                                        ? tasks.getTasks()
                                        : tasks.getTasksOn(parsedCommand.filterDate());
                        ui.showTaskList(displayedTasks);
                        break;
                    case FIND:
                        List<TaskList.NumberedTask> matchingTasks =
                                tasks.getTasksMatching(parsedCommand.searchTerm());
                        ui.showMatchingTaskList(matchingTasks);
                        break;
                    case MARK:
                        Task markedTask = tasks.mark(parsedCommand.taskNumber());
                        ui.showMarked(markedTask);
                        storage.save(tasks.getTaskSnapshot());
                        break;
                    case UNMARK:
                        Task unmarkedTask = tasks.unmark(parsedCommand.taskNumber());
                        ui.showUnmarked(unmarkedTask);
                        storage.save(tasks.getTaskSnapshot());
                        break;
                    case DELETE:
                        Task removedTask = tasks.delete(parsedCommand.taskNumber());
                        ui.showDeleted(removedTask, tasks.getTaskCount());
                        storage.save(tasks.getTaskSnapshot());
                        break;
                    case EVENT, DEADLINE, TODO:
                        Task newTask = parsedCommand.task();
                        tasks.add(newTask);
                        ui.showAdded(newTask, tasks.getTaskCount());
                        storage.save(tasks.getTaskSnapshot());
                        break;
                    case BYE:
                        throw new IllegalStateException("The bye command should exit before dispatch.");

                    default:
                        throw new IllegalStateException("This should not be reachable");
                }
            } catch (PandaException exception) {
                // Show expected input errors and continue accepting commands.
                ui.showError(exception);
            }
            ui.showDivider();
        }
        ui.showGoodbye();
    }
}
