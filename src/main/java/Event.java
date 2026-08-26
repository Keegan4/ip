import java.time.LocalDateTime;

/**
 * Represents a task with a stated start and end date or time.
 *
 * Both endpoints are parsed when the event is created, ensuring that every
 * stored event has valid start and end values.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an unfinished event task.
     *
     * @param name the event description
     * @param from the supplied starting date and time
     * @param to the supplied ending date and time
     * @throws InvalidDateException if either endpoint is not a valid date and time
     */
    public Event(String name, String from, String to) throws InvalidDateException {
        super(name);
        this.from = processDate(from);
        this.to = processDate(to);
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
     * Returns the event description together with its formatted time range.
     *
     * @return the formatted event description
     */
    @Override
    public String getDisplayText() {
        return getName() + " (from: " + formatDate(from)
                + " to: " + formatDate(to) + ")";
    }

    /**
     * Converts this event into one line of Panda's storage format.
     *
     * @return the common task fields followed by the start and end values
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + escapeDataField(formatDate(from))
                + " | " + escapeDataField(formatDate(to));
    }
}
