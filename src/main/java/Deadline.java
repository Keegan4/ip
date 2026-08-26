import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a stated date or time.
 *
 * The deadline is parsed when the task is created, ensuring that every stored
 * deadline has a valid date and time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates an unfinished deadline task.
     *
     * @param name the task description
     * @param by the date and time text supplied by the user
     * @throws InvalidDateException if {@code by} is not a valid date and time
     */
    public Deadline(String name, String by) throws InvalidDateException {
        super(name);
        this.by = processDate(by);
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
     * Returns the task description together with its formatted deadline.
     *
     * @return the formatted deadline description
     */
    @Override
    public String getDisplayText() {
        return getName() + " (by: " + formatDateForDisplay(by) + ")";
    }

    /**
     * Checks whether this deadline is due on the supplied date.
     *
     * @param date the date used to filter the task list
     * @return true when the deadline falls on {@code date}
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Converts this deadline into one line of Panda's storage format.
     *
     * @return the common task fields followed by the deadline value
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + escapeDataField(formatDateForStorage(by));
    }
}
