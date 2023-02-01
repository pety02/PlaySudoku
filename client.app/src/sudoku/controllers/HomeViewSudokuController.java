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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import server.entities.Game;
import server.entities.Player;
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

/**
 * Контролер на заглавния прозорец.
 */
public class HomeViewSudokuController {

    private Player player;
    private Game game;
    private final ClientService clientServiceImpl = new ClientServiceImpl();
    private final Logger logger = Logger.getLogger(HomeViewSudokuController.class.getName());

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

    /**
     * Създава дъска и инициализира нейните стойности според нивото на трудност на предстоящата игра.
     *
     * @param level - ниво на трудност.
     * @throws RemoteException
     */
    private int[][] makeBoard(SudokuLevel level) throws RemoteException {

        return clientServiceImpl.initGame(level, nicknameTxtField.getText());
    }

    /**
     * Инициализира сцената със судоку пъзела.
     *
     * @param actionEvent - събитие.
     */
    private void sendSudokuScene(Event actionEvent, Pair<Game, Player> data) {
        Stage s = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudoku/fxmls/PlaySudoku.fxml"));
            Parent root = loader.load();
            Scene sc = new Scene(root);
            s.setUserData(data);
            if (loader.getController().getClass() == PlaySudokuController.class) {
                ((PlaySudokuController) loader.getController()).receive(s);
            }

            s.setScene(sc);
        } catch (IOException e) {
            logger.log(Level.SEVERE, LocalDate.now() + e.getMessage());
        }
    }

    /**
     * Инициализира EASY Game.
     *
     * @param actionEvent - събитие.
     */
    @FXML
    public void onEasyGameBtnClicked(ActionEvent actionEvent) {
        try {
            int[][] b = makeBoard(SudokuLevel.EASY);
            //ClientService clientServiceImpl = new ClientServiceImpl();

            int[][] sol = new int[9][9];
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < b[i].length; j++) {
                    sol[i][j] = b[i][j];
                }
            }

            Player player = new Player(nicknameTxtField.getText());
            Game game = new Game(SudokuLevel.EASY, SudokuLevel.EASY.getMaxEmptyCells(),
                    b, clientServiceImpl.makeSolution(sol, sol.length), 0, player);

            Pair<Game, Player> data = new Pair<>(game, player);
            sendSudokuScene(actionEvent, data);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }

    /**
     * Инициализира MEDIUM Game.
     *
     * @param event - събитие.
     */
    @FXML
    void onMediumGameBtnClicked(MouseEvent event) {
        try {
            int[][] b = makeBoard(SudokuLevel.MEDIUM);
            ClientService clientServiceImpl = new ClientServiceImpl();

            int[][] sol = new int[9][9];
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < b[i].length; j++) {
                    sol[i][j] = b[i][j];
                }
            }

            Player player = new Player(nicknameTxtField.getText());
            Game game = new Game(SudokuLevel.MEDIUM, SudokuLevel.MEDIUM.getMaxEmptyCells(),
                    b, clientServiceImpl.makeSolution(sol, sol.length), 0, player);

            Pair<Game, Player> data = new Pair<>(game, player);
            sendSudokuScene(event, data);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }

    /**
     * Инициализира HARD Game.
     *
     * @param event - събитие.
     */
    @FXML
    void onHardGameBtnClicked(MouseEvent event) {
        try {
            int[][] b = makeBoard(SudokuLevel.HARD);
            ClientService clientServiceImpl = new ClientServiceImpl();

            int[][] sol = new int[9][9];
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < b[i].length; j++) {
                    sol[i][j] = b[i][j];
                }
            }

            Player player = new Player(nicknameTxtField.getText());
            Game game = new Game(SudokuLevel.HARD, SudokuLevel.HARD.getMaxEmptyCells(),
                    b, clientServiceImpl.makeSolution(sol, sol.length), 0, player);

            Pair<Game, Player> data = new Pair<>(game, player);
            sendSudokuScene(event, data);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }

    /**
     * Инициализира контролера.
     */
    @FXML
    void initialize() {
        assert easyGameBtn != null : "fx:id=\"easyGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert hardGameBtn != null : "fx:id=\"hardGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert mediumGameBtn != null : "fx:id=\"mediumGameBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert nicknameTxtField != null : "fx:id=\"nicknameTxtField\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
    }
}
