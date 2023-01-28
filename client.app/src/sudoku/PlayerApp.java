package sudoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class PlayerApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root
                = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxmls/HomeViewSudoku.fxml")));

        Scene scene = new Scene(root);

        stage.setTitle("Play Sudoku");
        stage.sizeToScene();
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {

        Application.launch(args);
    }
}