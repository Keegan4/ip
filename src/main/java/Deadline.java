/**
 * Represents a task that must be completed by a stated date or time.
 *
 * Written by Codex: Store the deadline text without parsing it while inheriting
 * the task name, completion status, and mark/unmark behavior from Task.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an unfinished deadline task.
     *
     * @param name the task description
     * @param by the date or time text supplied by the user
     */
    public Deadline(String name, String by) {
        super(name);
        this.by = by;
    }

    /**
     * Returns the list marker for a deadline.
     *
     * @return the letter D
     */
    @Override
    public String getTypeMarker() {
        return "D";
    }

    /**
     * Returns the task description together with its unmodified deadline text.
     *
     * @return the formatted deadline description
     */
    @Override
    public String getDisplayText() {
        return getName() + " (by: " + by + ")";
    }

    /**
     * Converts this deadline into one line of Panda's storage format.
     *
     * @return the common task fields followed by the deadline value
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + escapeDataField(by);
    }
}
