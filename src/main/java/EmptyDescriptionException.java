/**
 * Signals that a task-creation command has no description.
 *
 * Written by Codex: Keep missing-description errors consistent for every task type.
 */
public class EmptyDescriptionException extends PandaException {
    /**
     * Creates a panda-themed missing-description error.
     *
     * @param taskType the kind of task being created
     */
    public EmptyDescriptionException(String taskType) {
        super("OOPS!!! This panda needs " + articleFor(taskType) + " " + taskType
                + " description before it can get to work.");
    }

    /**
     * Chooses the correct indefinite article for the task type.
     *
     * Written by Codex: Keep generated error messages grammatically readable.
     *
     * @param taskType the task type used in the message
     * @return `an` for vowel-starting types, otherwise `a`
     */
    private static String articleFor(String taskType) {
        return "aeiou".indexOf(Character.toLowerCase(taskType.charAt(0))) >= 0 ? "an" : "a";
    }
}
