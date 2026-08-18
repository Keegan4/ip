/**
 * Represents one task in Panda's in-memory task list.
 *
 * Written by Codex: Keep a task's name and completion status together so that
 * status changes are handled by the task itself.
 */
public class Task {
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
}
