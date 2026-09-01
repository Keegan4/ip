package panda.gui;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import panda.Panda;

/**
 * Starts Panda's JavaFX user interface.
 */
public class Main extends Application {
    private static final Path DEFAULT_DATA_FILE_PATH =
            Path.of("src", "main", "data", "info.txt");

    /**
     * Loads and displays Panda's main window.
     *
     * @param stage the primary JavaFX stage.
     * @throws IOException if the main-window resource cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        Panda panda = new Panda(DEFAULT_DATA_FILE_PATH.toString());
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();

        fxmlLoader.<MainWindow>getController().setPanda(panda);
        stage.setMinHeight(220);
        stage.setMinWidth(417);
        stage.setTitle("Panda");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
