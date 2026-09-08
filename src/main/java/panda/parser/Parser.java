package panda.parser;

import java.time.LocalDate;

import panda.exception.EmptyDescriptionException;
import panda.exception.EmptySearchTermException;
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
     * Creates a command parser.
     */
    public Parser() {
    }

    /**
     * Checks whether a message is the exact command that ends the program.
     *
     * @param message the complete user input.
     * @return true only for the argument-free {@code bye} command.
     */
    public boolean isExitCommand(String message) {
        return Command.BYE.matches(message);
    }

    /**
     * Parses one complete user message.
     *
     * @param message the complete user input.
     * @return a structured command containing its relevant argument.
     * @throws PandaException if the command or any argument is invalid.
     */
    public ParsedCommand parse(String message) throws PandaException {
        Command command = Command.findCommand(message);
        switch (command) {
            case BYE:
                return ParsedCommand.createWithoutArgument(command);
            case LIST:
                return parseList(message, command);
            case FIND:
                return parseFind(message, command);
            case MARK, UNMARK, DELETE:
                return parseTaskNumber(message, command);
            case TODO:
                return parseTodo(message, command);
            case DEADLINE:
                return parseDeadline(message, command);
            case EVENT:
                return parseEvent(message, command);
            default:
                throw new IllegalStateException("Unsupported command: " + command);
        }
    }

    /**
     * Parses the keyword used to search task names.
     */
    private ParsedCommand parseFind(String message, Command command)
            throws EmptySearchTermException {
        String searchTerm = getArguments(message, command);
        if (searchTerm.isBlank()) {
            throw new EmptySearchTermException();
        }
        return ParsedCommand.withSearchTerm(command, searchTerm);
    }

    /**
     * Parses an optional date filter from a list command.
     */
    private ParsedCommand parseList(String message, Command command)
            throws InvalidDateException {
        String dateText = getArguments(message, command);
        LocalDate filterDate = dateText.isEmpty()
                ? null : Task.parseListDate(dateText);
        return ParsedCommand.createWithFilterDate(command, filterDate);
    }

    /**
     * Parses the one-based task number used by mark, unmark, and delete.
     */
    private ParsedCommand parseTaskNumber(String message, Command command)
            throws InvalidTaskNumberException {
        String taskNumberText = getArguments(message, command);
        try {
            return ParsedCommand.createWithTaskNumber(command,
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
        return ParsedCommand.createWithTask(command, new Todo(description));
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

        String deadlineSeparator = details.contains(" /by ") ? " /by " : " by ";
        int deadlineSeparatorIndex = details.indexOf(deadlineSeparator);
        if (deadlineSeparatorIndex <= 0
                || deadlineSeparatorIndex + deadlineSeparator.length() >= details.length()) {
            throw new MissingDateTimeException(
                    "deadline <description> /by <date or time>.");
        }

        String description = details.substring(0, deadlineSeparatorIndex).trim();
        String deadlineDateTimeText = details.substring(
                deadlineSeparatorIndex + deadlineSeparator.length()).trim();
        return ParsedCommand.createWithTask(
                command, new Deadline(description, deadlineDateTimeText));
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

        String startSeparator = details.contains(" /from ") ? " /from " : " from ";
        String endSeparator = details.contains(" /to ") ? " /to " : " to ";
        int startSeparatorIndex = details.indexOf(startSeparator);
        int endSeparatorIndex = details.indexOf(endSeparator,
                startSeparatorIndex < 0 ? 0 : startSeparatorIndex + startSeparator.length());
        if (startSeparatorIndex <= 0
                || endSeparatorIndex <= startSeparatorIndex + startSeparator.length()
                || endSeparatorIndex + endSeparator.length() >= details.length()) {
            throw new MissingDateTimeException(
                    "event <description> /from <start> /to <end>.");
        }

        String description = details.substring(0, startSeparatorIndex).trim();
        String startDateTimeText = details.substring(
                startSeparatorIndex + startSeparator.length(), endSeparatorIndex).trim();
        String endDateTimeText = details.substring(
                endSeparatorIndex + endSeparator.length()).trim();
        return ParsedCommand.createWithTask(
                command, new Event(description, startDateTimeText, endDateTimeText));
    }

    /**
     * Returns the trimmed portion of a message after its command keyword.
     */
    private String getArguments(String message, Command command) {
        assert command.matches(message)
                : "Message must match the command before arguments are extracted.";
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
     * @param command the recognized command type.
     * @param task a parsed task for task-creation commands.
     * @param taskNumber a number for mark, unmark, or delete.
     * @param filterDate an optional date supplied to list.
     * @param searchTerm a keyword supplied to find.
     */
    public record ParsedCommand(Command command, Task task, Integer taskNumber,
            LocalDate filterDate, String searchTerm) {
        /**
         * Creates a parsed command without an argument.
         */
        private static ParsedCommand createWithoutArgument(Command command) {
            assert command == Command.BYE
                    : "Only the bye command may have no argument.";
            return new ParsedCommand(command, null, null, null, null);
        }

        /**
         * Creates a parsed task-creation command.
         */
        private static ParsedCommand createWithTask(Command command, Task task) {
            assert command == Command.TODO
                    || command == Command.DEADLINE
                    || command == Command.EVENT
                    : "Only task-creation commands may contain a task.";
            assert task != null
                    : "A task-creation command must contain a task.";
            return new ParsedCommand(command, task, null, null, null);
        }

        /**
         * Creates a parsed numbered command.
         */
        private static ParsedCommand createWithTaskNumber(Command command, int taskNumber) {
            assert command == Command.MARK
                    || command == Command.UNMARK
                    || command == Command.DELETE
                    : "Only numbered commands may contain a task number.";
            return new ParsedCommand(command, null, taskNumber, null, null);
        }

        /**
         * Creates a parsed list command with an optional date filter.
         */
        private static ParsedCommand createWithFilterDate(Command command, LocalDate filterDate) {
            assert command == Command.LIST
                    : "Only the list command may contain a date filter.";
            return new ParsedCommand(command, null, null, filterDate, null);
        }

        /**
         * Creates a parsed find command with its search term.
         */
        private static ParsedCommand withSearchTerm(Command command, String searchTerm) {
            assert command == Command.FIND
                    : "Only the find command may contain a search term.";
            assert searchTerm != null && !searchTerm.isBlank()
                    : "A find command must contain a search term.";
            return new ParsedCommand(command, null, null, null, searchTerm);
        }
    }
}
