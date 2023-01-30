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
    private int[][] makeBoard(SudokuLevel level) throws RemoteException {
        ClientService cl = new ClientServiceImpl();
        int[][] board = cl.initGame(level, nicknameTxtField.getText());
        //System.out.println(String.format("Nickname: %s, SudokuLevel: %s\n", nicknameTxtField.getText(), level));
        /*for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(String.format("%d ", board[i][j]));
            }
            System.out.println();
        }*/

        return board;
    }

    /**
     * Инициализира сцената със судоку пъзела.
     * @param actionEvent - събитие.
     */
    //@FXML
    private void sendSudokuScene(Event actionEvent, int[][] board) {
        Stage s = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        s.close();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/PlaySudoku.fxml"));
            Parent root = loader.load();
            s.setUserData(board);
            Scene sc = new Scene(root);
            PlaySudokuController ctrl = new PlaySudokuController();
//            ctrl.initialize();
            loader.setController(ctrl);
//            if(loader.getController().getClass()== PlaySudokuController.class){
//                ((PlaySudokuController)loader.getController()).receive(actionEvent,s,sc);
//            }

            ctrl.receive(actionEvent, s, sc);
            s.setScene(sc);
            s.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Инициализира EASY Game.
     * @param actionEvent - събитие.
     */
    @FXML
    public void onEasyGameBtnClicked(ActionEvent actionEvent) {
        try {
            int[][] b = makeBoard(SudokuLevel.EASY);
            sendSudokuScene(actionEvent, b);
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
            int[][] b = makeBoard(SudokuLevel.MEDIUM);
            sendSudokuScene(event, b);
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
            int[][] b = makeBoard(SudokuLevel.HARD);
            sendSudokuScene(event, b);
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