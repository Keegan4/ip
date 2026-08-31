package gui;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import panda.Panda;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {
    private static final Path DEFAULT_DATA_FILE_PATH =
            Path.of("src", "main", "data", "info.txt");

    private Panda panda;

    @Override
    public void start(Stage stage) {
        try {
            String filePath =  DEFAULT_DATA_FILE_PATH.toString();
            panda = new Panda(filePath);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setPanda(panda);  // inject the Duke instance
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

