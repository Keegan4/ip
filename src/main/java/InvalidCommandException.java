/**
 * Signals that the user entered an unsupported command.
 *
 * Written by Codex: Separate unknown commands from valid commands with bad arguments.
 */
public class InvalidCommandException extends PandaException {
    /**
     * Creates the standard panda-themed unknown-command error.
     */
    public InvalidCommandException() {
        super("OOPS!!! I'm bamboo-zled; I don't know what that means :-(");
    }
}
