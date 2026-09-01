package panda.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import panda.Panda;

/**
 * Exercises the main JavaFX window through its visible input and dialog controls.
 */
public class MainWindowTest {
    private static final int GUI_TIMEOUT_SECONDS = 5;

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void mainWindow_commandsAndBye_formatsResponsesAndClosesStage()
            throws InterruptedException {
        CountDownLatch toolkitStarted = new CountDownLatch(1);
        try {
            Platform.startup(toolkitStarted::countDown);
        } catch (IllegalStateException exception) {
            toolkitStarted.countDown();
        }
        assertTrue(toolkitStarted.await(GUI_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        CountDownLatch stageClosed = new CountDownLatch(1);
        AtomicReference<Throwable> testFailure = new AtomicReference<>();
        Platform.runLater(() -> runGuiScenario(stageClosed, testFailure));

        assertTrue(stageClosed.await(GUI_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "The Panda window did not close after the bye command.");
        assertNull(testFailure.get(), () -> "GUI scenario failed: " + testFailure.get());
    }

    /**
     * Loads the GUI, exercises representative commands, and records assertion failures.
     */
    private void runGuiScenario(CountDownLatch stageClosed,
            AtomicReference<Throwable> testFailure) {
        Stage stage = new Stage();
        stage.setOnHidden(event -> stageClosed.countDown());
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainWindow.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            Path dataFile = temporaryDirectory.resolve("tasks.txt");
            loader.<MainWindow>getController().setPanda(new Panda(dataFile.toString()));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            TextField userInput = (TextField) scene.lookup("#userInput");
            Button sendButton = (Button) scene.lookup("#sendButton");
            VBox dialogContainer = (VBox) scene.lookup("#dialogContainer");

            assertDialogText(dialogContainer, 0, """
                     ____    _    _   _ ____    _
                    |  _ \\  / \\  | \\ | |  _ \\  / \\
                    | |_) |/ _ \\ |  \\| | | | |/ _ \\
                    |  __// ___ \\| |\\  | |_| / ___ \\
                    |_|  /_/   \\_\\_| \\_|____/_/   \\_\\

                    Hello! I'm Panda.
                    What can I do for you?""");
            submitAndAssert(userInput, sendButton, dialogContainer, "todo read book", """
                    Got it. I've added this task:
                      [T][ ] read book
                    Now you have 1 task in the list.
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer, "mark 1", """
                    Nice! I've marked this task as done:
                      [X] read book
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer, "list", """
                    Here are the tasks in your list:
                    1.[T][X] read book
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer, "unknown", """
                    OOPS!!! I'm bamboo-zled; I don't know what that means :-(""");
            submitAndAssert(userInput, sendButton, dialogContainer, "delete 1", """
                    Noted. I've removed this task:
                      [T][X] read book
                    Now you have 0 tasks in the list.
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer, "bye",
                    "Bye. Hope to see you again soon!");
            assertTrue(userInput.isDisabled());
            assertTrue(sendButton.isDisabled());
        } catch (Throwable throwable) {
            testFailure.set(throwable);
            stage.hide();
            Platform.exit();
        }
    }

    /**
     * Submits one command and checks its newly appended Panda dialog.
     */
    private void submitAndAssert(TextField userInput, Button sendButton,
            VBox dialogContainer, String command, String expectedResponse) {
        int pandaDialogIndex = dialogContainer.getChildren().size() + 1;
        userInput.setText(command);
        sendButton.fire();

        assertEquals("", userInput.getText());
        assertDialogText(dialogContainer, pandaDialogIndex, expectedResponse);
    }

    /**
     * Checks dialog text while ignoring platform line-ending differences.
     */
    private void assertDialogText(VBox dialogContainer, int dialogIndex,
            String expectedText) {
        DialogBox dialogBox = (DialogBox) dialogContainer.getChildren().get(dialogIndex);
        assertEquals(normalizeLineEndings(expectedText),
                normalizeLineEndings(dialogBox.getDialogText()));
    }

    private String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }
}
