package sudoku.controllers;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PlaySudokuController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private GridPane board;

    @FXML
    private Label lblNickname;

    @FXML
    void initialize() {
        assert board != null : "fx:id=\"board\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
    }
}