/**
 * Signals that a supplied date and time is invalid or incorrectly formatted.
 */
public class InvalidDateException extends PandaException {
    /**
     * Creates a user-facing error that explains Panda's accepted format.
     */
    public InvalidDateException() {
        super("OOPS!!! This panda needs a valid date and time in "
                + "yyyy-MM-dd HH:mm format.");
    }
}
