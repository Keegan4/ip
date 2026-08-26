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
