package panda.parser;

import panda.exception.InvalidCommandException;

/**
 * Represents a command understood by the Panda chatbot.
 *
 * Written by Codex: Keep command keywords and argument rules in one type-safe place.
 */
public enum Command {
    BYE("bye", false),
    LIST("list", true),
    FIND("find", true),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    EVENT("event", true),
    DEADLINE("deadline", true),
    TODO("todo", true);

    /** Written by Codex: Store the exact lowercase word entered by the user. */
    private final String keyword;

    /** Written by Codex: Distinguish commands that may contain details after their keyword. */
    private final boolean acceptsArguments;

    /**
     * Creates a command definition.
     *
     * @param keyword the command word entered by the user
     * @param acceptsArguments whether text may follow the command word
     */
    Command(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Finds the command represented by a complete user message.
     *
     * The list command may include a date filter, while bye takes no arguments.
     *
     * @param message the complete user message
     * @return the matching command
     * @throws InvalidCommandException if the message does not match a supported command
     */
    public static Command fromMessage(String message) throws InvalidCommandException {
        for (Command command : values()) {
            if (command.matches(message)) {
                return command;
            }
        }
        throw new InvalidCommandException();
    }

    /**
     * Returns the command word entered by the user.
     *
     * @return the lowercase command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Checks whether a complete message represents this command.
     *
     * @param message the complete user message
     * @return true when the message follows this command's argument rule
     */
    public boolean matches(String message) {
        return message.equals(keyword) || acceptsArguments && message.startsWith(keyword + " ");
    }
}
