package sudoku.controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import server.entities.SudokuLevel;
import sudoku.services.ClientService;
import sudoku.servicesImpls.ClientServiceImpl;

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

    private void makeBoard(SudokuLevel level) throws RemoteException {
        ClientService cl = new ClientServiceImpl();
        int[][] board = cl.initGame(level, nicknameTxtField.getText());
        System.out.println(String.format("Nickname: %s, SudokuLevel: %s\n", nicknameTxtField.getText(), level));
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(String.format("%d ", board[i][j]));
            }
            System.out.println();
        }
    }

    @FXML
    public void onEasyGameBtnClicked(javafx.event.ActionEvent actionEvent) throws RemoteException {
        makeBoard(SudokuLevel.EASY);
    }

    @FXML
    void onHardGameBtnClicked(MouseEvent event) throws RemoteException {
        makeBoard(SudokuLevel.HARD);
    }

    @FXML
    void onMediumGameBtnClicked(MouseEvent event) throws RemoteException {
        makeBoard(SudokuLevel.MEDIUM);
    }

    @FXML
    void initialize() {
        assert easyGameBtn != null : "fx:id=\"easyGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert hardGameBtn != null : "fx:id=\"hardGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert mediumGameBtn != null : "fx:id=\"mediumGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert nicknameTxtField != null : "fx:id=\"nicknameTxtField\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
    }
}