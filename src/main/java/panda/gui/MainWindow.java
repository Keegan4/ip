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

    private final Ui ui = new Ui();
    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image pandaImage = new Image(
            getClass().getResourceAsStream("/images/DaDuke.png"));

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
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getPandaDialog(ui.showWelcome(), pandaImage));
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
        String input = userInput.getText();
        boolean shouldExit = panda.isExitCommand(input);
        String response = panda.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPandaDialog(response, pandaImage));
        userInput.clear();

        if (shouldExit) {
            closeAfterFarewell();
        }
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
