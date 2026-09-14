package panda.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import panda.Panda;
import panda.ui.Ui;

/**
 * Controls Panda's main JavaFX window.
 */
public class MainWindow extends AnchorPane {
    /** Gives JavaFX time to render Panda's farewell before closing the window. */
    private static final Duration EXIT_DELAY = Duration.millis(750);
    private static final String WELCOME_REACTION =
            "Panda is awake, wiggling, and ready for tasks! (^_^)";
    private static final String SUCCESS_REACTION =
            "Bamboo power! Another tiny victory for the pile. (^_^)";
    private static final String ERROR_REACTION =
            "Oops, my paws got tangled! Let's try that again. (>_<)";
    private static final String EXIT_REACTION =
            "Panda waddles off in search of a crunchy snack... (^_^)/";

    private final Ui ui = new Ui();
    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/explorer-avatar.png"));
    private final Image pandaImage = new Image(
            getClass().getResourceAsStream("/images/panda-avatar.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Panda panda;

    /**
     * Configures automatic scrolling and displays Panda's welcome message.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected.";
        assert dialogContainer != null : "fx:id=\"dialogContainer\" was not injected.";
        assert userInput != null : "fx:id=\"userInput\" was not injected.";
        assert sendButton != null : "fx:id=\"sendButton\" was not injected.";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getPandaDialog(
                        appendReaction(ui.showWelcome(), WELCOME_REACTION), pandaImage));
    }

    /**
     * Supplies the Panda instance that processes entered commands.
     *
     * @param panda the application coordinator.
     */
    public void setPanda(Panda panda) {
        this.panda = panda;
    }

    /**
     * Displays the entered command and Panda's response, then handles exit requests.
     */
    @FXML
    private void handleUserInput() {
        assert panda != null : "Panda must be supplied before user input is handled.";

        String input = userInput.getText();
        boolean shouldExit = panda.isExitCommand(input);
        String response = panda.getResponse(input);
        String responseReaction = getResponseReaction(response, shouldExit);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPandaDialog(
                        appendReaction(response, responseReaction), pandaImage));
        userInput.clear();

        if (shouldExit) {
            closeAfterFarewell();
        }
    }

    /**
     * Chooses a playful reaction that fits the result of a command.
     *
     * @param response the functional response returned by Panda.
     * @param shouldExit whether the command ends the session.
     * @return the matching personality line.
     */
    private String getResponseReaction(String response, boolean shouldExit) {
        if (shouldExit) {
            return EXIT_REACTION;
        }
        if (response.startsWith("OOPS!!!")) {
            return ERROR_REACTION;
        }
        return SUCCESS_REACTION;
    }

    /**
     * Places a personality line after Panda's functional response.
     *
     * @param response the functional response.
     * @param reaction the playful personality line.
     * @return the combined GUI response.
     */
    private String appendReaction(String response, String reaction) {
        String lineSeparator = System.lineSeparator();
        String trailingLineSeparator = response.endsWith(lineSeparator) ? lineSeparator : "";
        return response.stripTrailing() + lineSeparator + reaction + trailingLineSeparator;
    }

    /**
     * Prevents further input and closes JavaFX after the farewell is rendered.
     */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }
}
