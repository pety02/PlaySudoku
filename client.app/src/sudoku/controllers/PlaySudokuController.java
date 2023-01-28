package sudoku.controllers;

import javafx.scene.control.Button;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import server.entities.SudokuLevel;
import sudoku.services.ClientService;
import sudoku.servicesImpls.ClientServiceImpl;
import javax.swing.*;

public class PlaySudokuController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private GridPane firstRowFirstColGrid;

    @FXML
    private GridPane firstRowSecondColGrid;

    @FXML
    private GridPane firstRowZeroColGrid;

    @FXML
    private Button helpButton;
    
    @FXML
    private Button newGameButton;

    @FXML
    private Text nicknameLabel;

    @FXML
    private Button redoButton;

    @FXML
    private Text scoreLabel;

    @FXML
    private Text sudokuLevelLbl;

    @FXML
    private GridPane secondRowFirstColGrid;

    @FXML
    private GridPane secondRowSecondColGrid;

    @FXML
    private GridPane secondRowZeroColGrid;

    @FXML
    private Text timeLabel;

    @FXML
    private Button undoButton;

    @FXML
    private GridPane zeroRowFirstColGrid;

    @FXML
    private GridPane zeroRowSecondColGrid;

    @FXML
    private GridPane zeroRowZeroColGrid;

    @FXML
    void onHelpBtnClicked(MouseEvent event) {
        String title = "Помощ";
        String message = """
                Правила на играта:
                \t1. Судокуто е матрица от 3 реда с по 3 квадратчета на ред.
                \t2. Всеки ред трябва да съдържа числата от 1 до 9 без повторения.
                \t3. Всеки стълб трябва да съдържа числата от 1 до 9 без повторения.
                \t4. Всяка кутийка от 3 реда на 3 стълба трябва да съдържа числата от 1 до 9 без повторения.
                \t4. Играта се брои за победна едва, когато горните условия са изпълнени.

                \tПриятна игра!""";

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    @FXML
    void onNewGameBtnClicked(MouseEvent event) throws RemoteException {
        /* TODO: да се измисли как да се взема сегашното ниво на трудност и никнейма на играча
            и да се генерира нова игра от същото ниво на трудност.
        */

        ClientService cl = new ClientServiceImpl();
        int[][] board = cl.initGame(SudokuLevel.EASY, nicknameLabel.getText());
        System.out.println(String.format("Nickname: %s, SudokuLevel: %s\n", nicknameLabel.getText(), SudokuLevel.EASY));
        for (int[] ints : board) {
            for (int anInt : ints) {
                System.out.print(String.format("%d ", anInt));
            }
            System.out.println();
        }
    }

    @FXML
    void onRedoBtnClicked(MouseEvent event) {
        // TODO: да се имплементира
    }

    @FXML
    void onUndoBtnClicked(MouseEvent event) {
        // TODO: да се имплементира
    }

    @FXML
    void initialize() {
        assert firstRowFirstColGrid != null : "fx:id=\"firstRowFirstColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert firstRowSecondColGrid != null : "fx:id=\"firstRowSecondColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert firstRowZeroColGrid != null : "fx:id=\"firstRowZeroColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert helpButton != null : "fx:id=\"helpBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert newGameButton != null : "fx:id=\"newGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert nicknameLabel != null : "fx:id=\"nicknameLbl\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert redoButton != null : "fx:id=\"redoBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert scoreLabel != null : "fx:id=\"scoreLbl\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert secondRowFirstColGrid != null : "fx:id=\"secondRowFirstColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert secondRowSecondColGrid != null : "fx:id=\"secondRowSecondColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert secondRowZeroColGrid != null : "fx:id=\"secondRowZeroColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert timeLabel != null : "fx:id=\"timeLbl\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert undoButton != null : "fx:id=\"undoBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert zeroRowFirstColGrid != null : "fx:id=\"zeroRowFirstColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert zeroRowSecondColGrid != null : "fx:id=\"zeroRowSecondColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert zeroRowZeroColGrid != null : "fx:id=\"zeroRowZeroColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
    }

    public void setNickname(String nickname) {
        nicknameLabel = new Text();
        nicknameLabel.setText(nickname);
    }
}