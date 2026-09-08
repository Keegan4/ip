package panda.exception;

/**
 * Signals that an update command does not contain a valid name change.
 */
public class InvalidUpdateException extends PandaException {
    /**
     * Creates the standard error explaining the supported update syntax.
     */
    public InvalidUpdateException() {
        super("OOPS!!! This panda needs a valid update. "
                + "Try: update <task number> /name <new name>.");
    }
}
