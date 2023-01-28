package sudoku.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import server.entities.SudokuLevel;
import sudoku.services.ClientService;
import sudoku.servicesImpls.ClientServiceImpl;

/**
 * Контролер на заглавния прозорец.
 */
public class HomeViewSudokuController {

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
     * @param level - ниво на трудност.
     * @throws RemoteException
     */
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

    /**
     * Инициализира сцената със судоку пъзела.
     * @param actionEvent - събитие.
     */
    private void initializeSudokuScene(Event actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../fxmls/PlaySudoku.fxml"));
            Stage s = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene sc = new Scene(root);
            // тук ще се попълват никнейма на играча, времето, за което решава едни судоку пъзел, самият пъзел,
            // който е генериран от сървъра, нивото на трудност и прочие
            s.setScene(sc);
            s.show();
        } catch(IOException ioEx) {
            logger.log(Level.SEVERE, LocalDate.now() + ioEx.getMessage());
        }
    }

    /**
     * Инициализира EASY Game.
     * @param actionEvent - събитие.
     */
    @FXML
    public void onEasyGameBtnClicked(ActionEvent actionEvent) {
        try {
            makeBoard(SudokuLevel.EASY);
            initializeSudokuScene(actionEvent);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }

    /**
     * Инициализира MEDIUM Game.
     * @param event - събитие.
     * @throws RemoteException
     */
    @FXML
    void onMediumGameBtnClicked(MouseEvent event) throws RemoteException {
        try {
            makeBoard(SudokuLevel.MEDIUM);
            initializeSudokuScene(event);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }

    /**
     * Инициализира HARD Game.
     * @param event - събитие.
     * @throws RemoteException
     */
    @FXML
    void onHardGameBtnClicked(MouseEvent event) throws RemoteException {
        try {
            makeBoard(SudokuLevel.HARD);
            initializeSudokuScene(event);
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