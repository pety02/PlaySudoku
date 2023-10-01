package sudoku.controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import server.entities.Game;
import server.entities.Player;
import server.entities.SudokuBoard;
import server.entities.SudokuLevel;
import sudoku.services.ClientService;
import sudoku.servicesImpls.ClientServiceImpl;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeViewSudokuController {
    private final ClientService clientServiceImpl = new ClientServiceImpl();
    private final Logger logger = Logger.getLogger(HomeViewSudokuController.class.getName());
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button easyGameBtn;
    @FXML
    private Button mediumGameBtn;
    @FXML
    private Button hardGameBtn;
    @FXML
    private TextField nicknameTxtField;

    @FXML
    public void onEasyGameBtnClicked(ActionEvent actionEvent) {
        try {
            SudokuBoard board = makeBoard(SudokuLevel.EASY, 9);
            Player player = new Player(nicknameTxtField.getText());
            SudokuBoard solution = clientServiceImpl.makeSolution(board, board.getBoardSize());
            Game game = new Game(SudokuLevel.EASY, SudokuLevel.EASY.getMaxEmptyCells(),
                    board, solution, 0, player);

            Pair<Game, Player> data = new Pair<>(game, player);
            sendSudokuScene(actionEvent, data);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }

    @FXML
    void onMediumGameBtnClicked(ActionEvent actionEvent) {
        try {
            SudokuBoard board = makeBoard(SudokuLevel.MEDIUM, 9);
            Player player = new Player(nicknameTxtField.getText());
            SudokuBoard solution = clientServiceImpl.makeSolution(board, board.getBoardSize());
            Game game = new Game(SudokuLevel.MEDIUM, SudokuLevel.MEDIUM.getMaxEmptyCells(),
                    board, solution, 0, player);

            Pair<Game, Player> data = new Pair<>(game, player);
            sendSudokuScene(actionEvent, data);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }

    @FXML
    void onHardGameBtnClicked(ActionEvent actionEvent) {
        try {
            SudokuBoard board = makeBoard(SudokuLevel.HARD, 9);
            Player player = new Player(nicknameTxtField.getText());
            SudokuBoard solution = clientServiceImpl.makeSolution(board, board.getBoardSize());
            Game game = new Game(SudokuLevel.HARD, SudokuLevel.HARD.getMaxEmptyCells(),
                    board, solution, 0, player);

            Pair<Game, Player> data = new Pair<>(game, player);
            sendSudokuScene(actionEvent, data);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }

    private SudokuBoard makeBoard(SudokuLevel level, int N) throws RemoteException {
        return clientServiceImpl.initGame(level, N, nicknameTxtField.getText());
    }

    private void sendSudokuScene(Event actionEvent, Pair<Game, Player> data) {
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudoku/fxmls/PlaySudoku.fxml"));
            Parent root = loader.load();
            Scene content = new Scene(root);
            window.setUserData(data);
            if (loader.getController().getClass() == PlaySudokuController.class) {
                ((PlaySudokuController) loader.getController()).receive(window);
            }

            window.setScene(content);
        } catch (IOException e) {
            logger.log(Level.SEVERE, LocalDate.now() + e.getMessage());
        }
    }

    @FXML
    void initialize() {
        assert easyGameBtn != null : "fx:id=\"easyGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert hardGameBtn != null : "fx:id=\"hardGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert mediumGameBtn != null : "fx:id=\"mediumGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert nicknameTxtField != null : "fx:id=\"nicknameTxtField\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
    }
}