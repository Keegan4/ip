package panda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import panda.exception.InvalidDateException;
import panda.exception.InvalidTaskNumberException;

/**
 * Tests task-list operations that depend on one-based numbering and dates.
 */
class TaskListTest {

    @Test
    void getTasksOn_mixedTasks_returnsMatchingDatedTasksWithOriginalNumbers()
            throws InvalidDateException {
        Todo todo = new Todo("read book");
        Deadline matchingDeadline = new Deadline("submit report", "2026-09-11 18:00");
        Event spanningEvent = new Event("orientation", "2026-09-10 09:00",
                "2026-09-12 17:00");
        Deadline otherDeadline = new Deadline("return book", "2026-09-13 10:00");
        TaskList tasks = new TaskList(List.of(
                todo, matchingDeadline, spanningEvent, otherDeadline));

        List<TaskList.NumberedTask> result = tasks.getTasksOn(LocalDate.of(2026, 9, 11));

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).number());
        assertSame(matchingDeadline, result.get(0).task());
        assertEquals(3, result.get(1).number());
        assertSame(spanningEvent, result.get(1).task());
    }

    @Test
    void getTasksOn_eventBoundaryDates_includesBothEndpoints()
            throws InvalidDateException {
        Event event = new Event("camp", "2026-09-10 09:00", "2026-09-12 17:00");
        TaskList tasks = new TaskList(List.of(event));

        assertEquals(1, tasks.getTasksOn(LocalDate.of(2026, 9, 10)).size());
        assertEquals(1, tasks.getTasksOn(LocalDate.of(2026, 9, 12)).size());
        assertEquals(0, tasks.getTasksOn(LocalDate.of(2026, 9, 9)).size());
        assertEquals(0, tasks.getTasksOn(LocalDate.of(2026, 9, 13)).size());
    }

    @Test
    void delete_validTaskNumber_removesAndReturnsTheSelectedTask()
            throws InvalidTaskNumberException {
        Todo first = new Todo("read book");
        Todo second = new Todo("return book");
        TaskList tasks = new TaskList(List.of(first, second));

        Task deleted = tasks.delete(1);

        assertSame(first, deleted);
        assertEquals(List.of(second), tasks.getTaskSnapshot());
    }

    @Test
    void delete_outOfRangeTaskNumbers_throwsInvalidTaskNumberException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(InvalidTaskNumberException.class, () -> tasks.delete(0));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.delete(2));
    }
}
