package panda.parser;

import java.time.LocalDate;

import panda.exception.EmptyDescriptionException;
import panda.exception.InvalidDateException;
import panda.exception.InvalidTaskNumberException;
import panda.exception.MissingDateTimeException;
import panda.exception.PandaException;
import panda.task.Deadline;
import panda.task.Event;
import panda.task.Task;
import panda.task.TaskList;
import panda.task.Todo;

/**
 * Converts raw user input into structured commands that Panda can execute.
 * Syntax validation belongs here, while task-list operations remain in
 * {@link TaskList}.
 */
public class Parser {
    /**
     * Checks whether a message is the exact command that ends the program.
     *
     * @param message the complete user input
     * @return true only for the argument-free {@code bye} command
     */
    public boolean isExitCommand(String message) {
        return Command.BYE.matches(message);
    }

    /**
     * Parses one complete user message.
     *
     * @param message the complete user input
     * @return a structured command containing its relevant argument
     * @throws PandaException if the command or any argument is invalid
     */
    public ParsedCommand parse(String message) throws PandaException {
        Command command = Command.fromMessage(message);
        return switch (command) {
        case BYE -> ParsedCommand.withoutArgument(command);
        case LIST -> parseList(message, command);
        case MARK, UNMARK, DELETE -> parseTaskNumber(message, command);
        case TODO -> parseTodo(message, command);
        case DEADLINE -> parseDeadline(message, command);
        case EVENT -> parseEvent(message, command);
        };
    }

    /**
     * Parses an optional date filter from a list command.
     */
    private ParsedCommand parseList(String message, Command command)
            throws InvalidDateException {
        String dateText = getArguments(message, command);
        LocalDate filterDate = dateText.isEmpty()
                ? null : Task.processListDate(dateText);
        return ParsedCommand.withFilterDate(command, filterDate);
    }

    /**
     * Parses the one-based task number used by mark, unmark, and delete.
     */
    private ParsedCommand parseTaskNumber(String message, Command command)
            throws InvalidTaskNumberException {
        String taskNumberText = getArguments(message, command);
        try {
            return ParsedCommand.withTaskNumber(command,
                    Integer.parseInt(taskNumberText));
        } catch (NumberFormatException exception) {
            throw new InvalidTaskNumberException(command.getKeyword());
        }
    }

    /**
     * Parses a date-free to-do task.
     */
    private ParsedCommand parseTodo(String message, Command command)
            throws EmptyDescriptionException {
        String description = getArguments(message, command);
        ensureDescription(description, command);
        return ParsedCommand.withTask(command, new Todo(description));
    }

    /**
     * Parses a deadline description and its {@code /by} date-time value.
     */
    private ParsedCommand parseDeadline(String message, Command command)
            throws EmptyDescriptionException, MissingDateTimeException,
            InvalidDateException {
        String details = getArguments(message, command);
        ensureDescription(details, command);
        if (details.startsWith("/by ") || details.startsWith("by ")) {
            throw new EmptyDescriptionException(command.getKeyword());
        }

        String separator = details.contains(" /by ") ? " /by " : " by ";
        int separatorIndex = details.indexOf(separator);
        if (separatorIndex <= 0
                || separatorIndex + separator.length() >= details.length()) {
            throw new MissingDateTimeException(
                    "deadline <description> /by <date or time>.");
        }

        String description = details.substring(0, separatorIndex).trim();
        String by = details.substring(separatorIndex + separator.length()).trim();
        return ParsedCommand.withTask(command, new Deadline(description, by));
    }

    /**
     * Parses an event description and its {@code /from} and {@code /to} values.
     */
    private ParsedCommand parseEvent(String message, Command command)
            throws EmptyDescriptionException, MissingDateTimeException,
            InvalidDateException {
        String details = getArguments(message, command);
        ensureDescription(details, command);
        if (details.startsWith("/from ") || details.startsWith("from ")) {
            throw new EmptyDescriptionException(command.getKeyword());
        }

        String fromSeparator = details.contains(" /from ") ? " /from " : " from ";
        String toSeparator = details.contains(" /to ") ? " /to " : " to ";
        int fromIndex = details.indexOf(fromSeparator);
        int toIndex = details.indexOf(toSeparator,
                fromIndex < 0 ? 0 : fromIndex + fromSeparator.length());
        if (fromIndex <= 0 || toIndex <= fromIndex + fromSeparator.length()
                || toIndex + toSeparator.length() >= details.length()) {
            throw new MissingDateTimeException(
                    "event <description> /from <start> /to <end>.");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + fromSeparator.length(), toIndex).trim();
        String to = details.substring(toIndex + toSeparator.length()).trim();
        return ParsedCommand.withTask(command, new Event(description, from, to));
    }

    /**
     * Returns the trimmed portion of a message after its command keyword.
     */
    private String getArguments(String message, Command command) {
        return message.substring(command.getKeyword().length()).trim();
    }

    /**
     * Ensures a task-creation command contains a description.
     */
    private void ensureDescription(String description, Command command)
            throws EmptyDescriptionException {
        if (description.isBlank()) {
            throw new EmptyDescriptionException(command.getKeyword());
        }
    }

    /**
     * Holds the single typed argument relevant to a parsed command.
     * Fields that do not apply to a particular command are null; the factory
     * methods keep those combinations consistent inside the parser.
     *
     * @param command the recognized command type
     * @param task a parsed task for task-creation commands
     * @param taskNumber a number for mark, unmark, or delete
     * @param filterDate an optional date supplied to list
     */
    public record ParsedCommand(Command command, Task task, Integer taskNumber,
            LocalDate filterDate) {
        /**
         * Creates a parsed command without an argument.
         */
        private static ParsedCommand withoutArgument(Command command) {
            return new ParsedCommand(command, null, null, null);
        }

        /**
         * Creates a parsed task-creation command.
         */
        private static ParsedCommand withTask(Command command, Task task) {
            return new ParsedCommand(command, task, null, null);
        }

        /**
         * Creates a parsed numbered command.
         */
        private static ParsedCommand withTaskNumber(Command command, int taskNumber) {
            return new ParsedCommand(command, null, taskNumber, null);
        }

        /**
         * Creates a parsed list command with an optional date filter.
         */
        private static ParsedCommand withFilterDate(Command command, LocalDate filterDate) {
            return new ParsedCommand(command, null, null, filterDate);
        }
    }
}
