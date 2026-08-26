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

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final List<PandaException> loadingErrors;

    /**
     * Creates Panda with a user interface and storage for the supplied file.
     *
     * @param filePath the task data file path
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

        if (!loadingErrors.isEmpty()) {
            ui.showLoadingErrors(loadingErrors);
        }
        // Written by Codex: Treat a closed input stream as a graceful end to the session.
        while (ui.hasNextCommand()) {
            String msg = ui.readCommand();
            if (parser.isExitCommand(msg)) {
                break;
            }
            ui.showDivider();
            try {
                Parser.ParsedCommand parsedCommand = parser.parse(msg);
                switch (parsedCommand.command()) {
                case LIST -> {
                    ui.showTaskListHeader();
                    List<TaskList.NumberedTask> displayedTasks =
                            parsedCommand.filterDate() == null
                                    ? tasks.getTasks()
                                    : tasks.getTasksOn(parsedCommand.filterDate());
                    for (TaskList.NumberedTask numberedTask : displayedTasks) {
                        ui.showTask(numberedTask.number(), numberedTask.task());
                    }
                }
                case FIND -> {
                    ui.showMatchingTaskListHeader();
                    List<TaskList.NumberedTask> matchingTasks =
                            tasks.getTasksMatching(parsedCommand.searchTerm());
                    for (TaskList.NumberedTask numberedTask : matchingTasks) {
                        ui.showTask(numberedTask.number(), numberedTask.task());
                    }
                }
                case MARK -> {
                    Task task = tasks.mark(parsedCommand.taskNumber());
                    ui.showMarked(task);
                    storage.save(tasks.asList());
                }
                case UNMARK -> {
                    Task task = tasks.unmark(parsedCommand.taskNumber());
                    ui.showUnmarked(task);
                    storage.save(tasks.asList());
                }
                case DELETE -> {
                    Task removedTask = tasks.delete(parsedCommand.taskNumber());
                    ui.showDeleted(removedTask, tasks.size());
                    storage.save(tasks.asList());
                }
                case EVENT, DEADLINE, TODO -> {
                    Task task = parsedCommand.task();
                    tasks.add(task);
                    ui.showAdded(task, tasks.size());
                    storage.save(tasks.asList());
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
}
