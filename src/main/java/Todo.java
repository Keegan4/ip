/**
 * Represents a task with no date or time attached.
 *
 * Written by Codex: Model to-dos as a Task subtype so they inherit their name,
 * completion status, and mark/unmark behavior.
 */
public class Todo extends Task {
    /**
     * Creates an unfinished to-do with the given description.
     *
     * @param name the description entered for the to-do
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Returns the list marker for a to-do.
     *
     * @return the letter T
     */
    @Override
    public String getTypeMarker() {
        return "T";
    }
}
