/**
 * Represents a task with a stated start and end date or time.
 *
 * Written by Codex: Preserve the event's start and end text while inheriting
 * its name, completion status, and mark/unmark behavior from Task.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an unfinished event task.
     *
     * @param name the event description
     * @param from the supplied starting date or time
     * @param to the supplied ending date or time
     */
    public Event(String name, String from, String to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the list marker for an event.
     *
     * @return the letter E
     */
    @Override
    public String getTypeMarker() {
        return "E";
    }

    /**
     * Returns the event description together with its unmodified time range.
     *
     * @return the formatted event description
     */
    @Override
    public String getDisplayText() {
        return getName() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Converts this event into one line of Panda's storage format.
     *
     * @return the common task fields followed by the start and end values
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + escapeDataField(from)
                + " | " + escapeDataField(to);
    }
}
