package panda.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Displays compact user commands and full-width Panda response cards.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Loads a message row and constrains its text to the available width.
     */
    private DialogBox(String text, Image image) {
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box layout.", exception);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(20, 20, 20));
        setMinWidth(0);
    }

    /**
     * Creates a right-aligned command chip without an avatar.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox row = new DialogBox(text, null);
        row.getChildren().remove(row.displayPicture);
        row.setAlignment(Pos.TOP_RIGHT);
        row.dialog.getStyleClass().add("user-command");
        row.dialog.maxWidthProperty().bind(row.widthProperty().multiply(0.85));
        return row;
    }

    /**
     * Creates a wide response card with a small circular Panda avatar.
     */
    public static DialogBox getPandaDialog(String text, Image image) {
        DialogBox row = new DialogBox(text, image);
        row.dialog.getStyleClass().add("panda-response");
        row.dialog.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    /**
     * Creates an error card whose accent and text distinguish it from success.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox row = getPandaDialog(text, image);
        row.dialog.getStyleClass().add("error-response");
        return row;
    }

    String getDialogText() {
        return dialog.getText();
    }
}
