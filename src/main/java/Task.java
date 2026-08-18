/**
 * Represents one task in Panda's in-memory task list.
 *
 * Written by Codex: Keep a task's name and completion status together so that
 * status changes are handled by the task itself.
 */
public abstract class Task {
    private final String name;
    private boolean done;

    /**
     * Creates a new unfinished task with the given name.
     *
     * @param name the text entered for the task
     */
    public Task(String name) {
        this.name = name;
        this.done = false;
    }

    /**
     * Returns the task's name.
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the task has been completed.
     *
     * @return true when the task is done
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        done = true;
    }

    /**
     * Marks this task as unfinished.
     */
    public void unmark() {
        done = false;
    }

    /**
     * Returns the letter used to identify this kind of task in the list.
     *
     * Written by Codex: Let each task subtype supply its own display marker.
     *
     * @return the task type marker
     */
    public abstract String getTypeMarker();

    /**
     * Returns the task text to show in confirmations and task lists.
     *
     * Written by Codex: Provide a default display value that dated task
     * subclasses can extend with their own details.
     *
     * @return the formatted task description
     */
    public String getDisplayText() {
        return name;
    }
}
