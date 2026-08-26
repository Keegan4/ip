import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns Panda's ordered collection of tasks and all operations on that list.
 * Command-processing code can work with one-based task numbers without
 * depending directly on {@link ArrayList} indexing details.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param initialTasks the tasks in their stored order
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Marks the identified task as completed.
     *
     * @param taskNumber the one-based task number
     * @return the task that was marked
     * @throws InvalidTaskNumberException if no task has that number
     */
    public Task mark(int taskNumber) throws InvalidTaskNumberException {
        Task task = getTask(taskNumber);
        task.mark();
        return task;
    }

    /**
     * Marks the identified task as unfinished.
     *
     * @param taskNumber the one-based task number
     * @return the task that was unmarked
     * @throws InvalidTaskNumberException if no task has that number
     */
    public Task unmark(int taskNumber) throws InvalidTaskNumberException {
        Task task = getTask(taskNumber);
        task.unmark();
        return task;
    }

    /**
     * Removes the identified task from the list.
     *
     * @param taskNumber the one-based task number
     * @return the removed task
     * @throws InvalidTaskNumberException if no task has that number
     */
    public Task delete(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns all tasks together with their stable one-based numbers.
     *
     * @return the numbered tasks in list order
     */
    public List<NumberedTask> getTasks() {
        return createNumberedTasks(null);
    }

    /**
     * Returns dated tasks that occur on the supplied date.
     * Date-free to-dos are not included.
     *
     * @param date the date used to filter tasks
     * @return matching tasks with their numbers from the complete list
     */
    public List<NumberedTask> getTasksOn(LocalDate date) {
        return createNumberedTasks(date);
    }

    /**
     * Returns a read-only snapshot suitable for saving.
     *
     * @return the tasks in list order
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Resolves a one-based task number to its task object.
     */
    private Task getTask(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Ensures a one-based task number identifies an existing task.
     */
    private void validateTaskNumber(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new InvalidTaskNumberException(taskNumber);
        }
    }

    /**
     * Builds display entries while preserving numbers from the complete list.
     * A null filter selects every task.
     */
    private List<NumberedTask> createNumberedTasks(LocalDate filterDate) {
        ArrayList<NumberedTask> numberedTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (filterDate == null || task.occursOn(filterDate)) {
                numberedTasks.add(new NumberedTask(i + 1, task));
            }
        }
        return List.copyOf(numberedTasks);
    }

    /**
     * Pairs a task with its one-based position in the complete task list.
     *
     * @param number the one-based task number
     * @param task the corresponding task
     */
    public record NumberedTask(int number, Task task) {
    }
}
