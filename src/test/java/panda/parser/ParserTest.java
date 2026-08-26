package panda.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import panda.exception.EmptyDescriptionException;
import panda.exception.InvalidCommandException;
import panda.exception.InvalidDateException;
import panda.exception.InvalidTaskNumberException;
import panda.exception.MissingDateTimeException;
import panda.exception.PandaException;
import panda.task.Deadline;
import panda.task.Event;
import panda.task.Todo;

/**
 * Tests how {@link Parser} converts user input into structured commands.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_todoCommand_returnsTodoWithCompleteDescription() throws PandaException {
        Parser.ParsedCommand result = parser.parse("todo read a Java book");

        assertEquals(Command.TODO, result.command());
        Todo todo = assertInstanceOf(Todo.class, result.task());
        assertEquals("read a Java book", todo.getName());
        assertNull(result.taskNumber());
        assertNull(result.filterDate());
    }

    @Test
    void parse_numberedCommands_returnsCommandAndTaskNumber() throws PandaException {
        Parser.ParsedCommand mark = parser.parse("mark 2");
        Parser.ParsedCommand unmark = parser.parse("unmark 15");
        Parser.ParsedCommand delete = parser.parse("delete 1");

        assertEquals(Command.MARK, mark.command());
        assertEquals(2, mark.taskNumber());
        assertEquals(Command.UNMARK, unmark.command());
        assertEquals(15, unmark.taskNumber());
        assertEquals(Command.DELETE, delete.command());
        assertEquals(1, delete.taskNumber());
    }

    @Test
    void parse_listCommands_returnsOptionalDateFilter() throws PandaException {
        Parser.ParsedCommand unfiltered = parser.parse("list");
        Parser.ParsedCommand filtered = parser.parse("list 2026-08-26");

        assertEquals(Command.LIST, unfiltered.command());
        assertNull(unfiltered.filterDate());
        assertEquals(Command.LIST, filtered.command());
        assertEquals(LocalDate.of(2026, 8, 26), filtered.filterDate());
    }

    @Test
    void parse_datedTaskCommands_returnsCorrectTaskTypesAndDetails()
            throws PandaException {
        Parser.ParsedCommand deadlineResult = parser.parse(
                "deadline submit report /by 2026-09-01 18:00");
        Parser.ParsedCommand eventResult = parser.parse(
                "event project meeting /from 2026-09-02 14:00 /to 2026-09-02 16:00");

        assertEquals(Command.DEADLINE, deadlineResult.command());
        Deadline deadline = assertInstanceOf(Deadline.class, deadlineResult.task());
        assertEquals("submit report", deadline.getName());
        assertEquals("submit report (by: Sep 01 2026 18:00)", deadline.getDisplayText());

        assertEquals(Command.EVENT, eventResult.command());
        Event event = assertInstanceOf(Event.class, eventResult.task());
        assertEquals("project meeting", event.getName());
        assertEquals("project meeting (from: Sep 02 2026 14:00 to: Sep 02 2026 16:00)",
                event.getDisplayText());
    }

    @Test
    void parse_missingOrMalformedArguments_throwsFocusedExceptions() {
        assertThrows(EmptyDescriptionException.class, () -> parser.parse("todo"));
        assertThrows(InvalidTaskNumberException.class, () -> parser.parse("mark bamboo"));
        assertThrows(InvalidDateException.class, () -> parser.parse("list 2025-02-29"));
        assertThrows(MissingDateTimeException.class,
                () -> parser.parse("deadline submit report"));
        assertThrows(MissingDateTimeException.class,
                () -> parser.parse("event meeting /from 2026-09-02 14:00"));
    }

    @Test
    void parse_unknownCommand_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> parser.parse("feed panda"));
    }
}
