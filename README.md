# Panda project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/panda/Panda.java` file, right-click it, and choose `Run Panda.main()` (if the code editor is showing compile errors, try restarting the IDE).
**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Updating tasks

Change the name of any existing task without changing its type, completion status, timing, or position:

```text
update <task number> /name <new name>
```

For example, `update 2 /name submit final report` changes the name of task 2. The task number refers to its position in the complete task list.

Change a Deadline's date and time using the strict `yyyy-MM-dd HH:mm` format:

```text
update <task number> /by <new date and time>
```

Change an Event's complete time interval using the same format:

```text
update <task number> /from <new start> /to <new end>
```

Timing updates do not change the task's name, type, completion status, or position. An Event update must include both `/from` and `/to` values.

## AI assistance

OpenAI Codex was used as a coding assistant during the development of this project. Its contributions
included:

- explaining Java date-time parsing, formatting, and validation;
- helping implement dated deadlines and events, including the `list <date>` filter;
- suggesting and implementing the `Ui`, `Storage`, `Parser`, and `TaskList` refactoring;
- reorganizing classes into packages and configuring Gradle;
- reviewing error handling, Javadocs, and SE-EDU Java coding-standard compliance; and
- expanding the JUnit and command-line UI test coverage.

AI-assisted changes were checked by compiling with Java 25, running the Gradle JUnit suite, generating
Javadocs, and running the project's repeatable command-line UI tests. The project author remains
responsible for reviewing the resulting code and for the final design and implementation decisions.
