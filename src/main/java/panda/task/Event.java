package panda.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import panda.exception.InvalidDateException;

/**
 * Represents a task with a stated start and end date or time.
 *
 * Both endpoints are parsed when the event is created, ensuring that every
 * stored event has valid start and end values.
 */
public class Event extends Task {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an unfinished event task.
     *
     * @param name the event description.
     * @param startDateTimeText the supplied starting date and time.
     * @param endDateTimeText the supplied ending date and time.
     * @throws InvalidDateException if either endpoint is not a valid date and time.
     */
    public Event(String name, String startDateTimeText, String endDateTimeText)
            throws InvalidDateException {
        super(name);
        startDateTime = parseDateTime(startDateTimeText);
        endDateTime = parseDateTime(endDateTimeText);
    }

    /**
     * Returns the list marker for an event.
     *
     * @return the letter E.
     */
    @Override
    public String getTypeMarker() {
        return "E";
    }

    /**
     * Returns the event description together with its formatted time range.
     *
     * @return the formatted event description.
     */
    @Override
    public String getDisplayText() {
        return getName() + " (from: " + formatDateTimeForDisplay(startDateTime)
                + " to: " + formatDateTimeForDisplay(endDateTime) + ")";
    }

    /**
     * Checks whether this event is occurring on the supplied date.
     * Both the start and end dates are included in the event's date range.
     *
     * @param date the date used to filter the task list.
     * @return true when {@code date} falls within the event's date range.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Converts this event into one line of Panda's storage format.
     *
     * @return the common task fields followed by the start and end values.
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | "
                + escapeDataField(formatDateTimeForStorage(startDateTime))
                + " | " + escapeDataField(formatDateTimeForStorage(endDateTime));
    }
}
