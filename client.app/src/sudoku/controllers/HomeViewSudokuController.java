package sudoku.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class HomeViewSudokuController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button easyGameBtn;

    @FXML
    private Button hardGameBtn;

    @FXML
    private Button mediumGameBtn;

    @FXML
    private TextField nicknameTxtField;

    @FXML
    void onEasyGameBtnCllicked(MouseEvent event) {

    }

    @FXML
    void onHardGameBtnClicked(MouseEvent event) {

    }

    @FXML
    void onMediumGameBtnClicked(MouseEvent event) {

    }

    @FXML
    void initialize() {
        assert easyGameBtn != null : "fx:id=\"easyGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert hardGameBtn != null : "fx:id=\"hardGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert mediumGameBtn != null : "fx:id=\"mediumGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert nicknameTxtField != null : "fx:id=\"nicknameTxtField\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
    }
}