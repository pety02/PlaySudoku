package sudoku.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import server.entities.Game;
import server.entities.GameTurn;
import server.entities.Player;
import server.entities.SudokuLevel;
import sudoku.services.ClientService;
import sudoku.servicesImpls.ClientServiceImpl;

import javax.swing.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Контролер на игралния прозорец.
 */
public class PlaySudokuController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane pane;

    @FXML
    private GridPane sudokuGrid;

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

    private Player currentPlayer;

    private Game currentGame;

    private int[][] board;

    private void fillBox(int[][] board, GridPane grid, int rowStInd, int rowEndInd, int colStInd, int colEndInd) {
        int index = 0;
        for (int i = rowStInd; i <= rowEndInd; i++) {
            for (int j = colStInd; j <= colEndInd; j++) {
                ((TextField) grid.getChildren().get(index))
                        .setText(Integer.toString(board[i][j]));
                ((TextField) grid.getChildren().get(index)).setEditable(false);
                ((TextField) grid.getChildren().get(index)).setStyle("-fx-font-weight: bold;");
                if(board[i][j] == 0) {
                    ((TextField) grid.getChildren().get(index))
                            .setText("");
                    ((TextField) grid.getChildren().get(index)).setEditable(true);
                    ((TextField) grid.getChildren().get(index)).setStyle("-fx-font-weight: none;");
                }
                index++;
            }
        }
    }

    void receive(Stage s) {
        Pair<Game, Player> data = (Pair<Game, Player>) s.getUserData();

        currentPlayer = data.getValue();
        currentGame = data.getKey();

        board = currentGame.getBoard();

        final int boardSize = 9;
        int i = 0;
        for (int j = 0; j < boardSize; j+=3) {
            for (int k = 0; k < boardSize; k+=3) {
                GridPane innerGrid = (GridPane) sudokuGrid.getChildren().get(i);
                fillBox(board, innerGrid, j, j+2, k, k+2);
                i++;
            }
        }

        Timer myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            int minutes = 0;
            int seconds = 0;
            @Override
            public void run() {
                if(seconds == 60) {
                    minutes += 1;
                    seconds=0;
                }
                if(minutes >= 10 && seconds >= 10) {
                    timeLabel.setText(String.format("Time: %d:%d", minutes, seconds));
                } else {
                    timeLabel.setText(String.format("Time: 0%d:0%d", minutes, seconds));
                }
                seconds++;

                if (minutes>0 && minutes % 5 == 0 && seconds == 0) {
                    int answer = JOptionPane.showOptionDialog(null, "Do you still playing?", "Playing status",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            new Object[] { "Yes", "No" }, JOptionPane.YES_OPTION);
                    if(answer == JOptionPane.NO_OPTION) {
                        this.cancel();
                    }
                }
            }
        }, 0, 1000);
        nicknameLabel.setText(data.getValue().getNickname());
        scoreLabel.setText(String.valueOf(data.getKey().getCurrentScore()));
        sudokuLevelLbl.setText(String.valueOf(data.getKey().getLevel()));
    }

    /**
     * Отваря инструкциите на играта.
     *
     * @param event - събитие.
     */
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

    /**
     * Създава нова игра от същата трудност.
     *
     * @param event - събитие.
     * @throws RemoteException
     */
    @FXML
    void onNewGameBtnClicked(MouseEvent event) throws RemoteException {
        ClientService cl = new ClientServiceImpl();
        int[][] board;
        switch (sudokuLevelLbl.getText()) {
            case "MEDIUM" -> board = cl.initGame(SudokuLevel.MEDIUM, nicknameLabel.getText());
            case "HARD" -> board = cl.initGame(SudokuLevel.HARD, nicknameLabel.getText());

            default -> board = cl.initGame(SudokuLevel.EASY, nicknameLabel.getText());
        }

        final int boardSize = 9;
        int i = 0;
        for (int j = 0; j < boardSize; j+=3) {
            for (int k = 0; k < boardSize; k+=3) {
                GridPane innerGrid = (GridPane) sudokuGrid.getChildren().get(i);
                fillBox(board, innerGrid, j, j+2, k, k+2);
                i++;
            }
        }

        Timer myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            int minutes = 0;
            int seconds = 0;
            @Override
            public void run() {
                if(seconds == 60) {
                    minutes += 1;
                    seconds=0;
                }
                if(minutes >= 10 && seconds >= 10) {
                    timeLabel.setText(String.format("Time: %d:%d", minutes, seconds));
                } else {
                    timeLabel.setText(String.format("Time: 0%d:0%d", minutes, seconds));
                }
                seconds++;

                if (minutes>0 && minutes % 5 == 0) {
                    int answer = JOptionPane.showOptionDialog(null, "Do you still playing?", "Playing status",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            new Object[] { "Yes", "No" }, JOptionPane.YES_OPTION);
                    if(answer == JOptionPane.NO_OPTION) {
                        this.cancel();
                    }
                }
            }
        }, 0, 1000);
    }

    @FXML
    void onInputTextChanged(KeyEvent event) {
        // TODO: да дебъгна за NullPointerException и/или други бъгове
        TextField node = (TextField)event.getSource();
        int value = (node.getText().matches("[1-9](1,)")) ? Integer.parseInt(node.getText()) : 0;
        int rowIndex = GridPane.getRowIndex(node);
        int colIndex = GridPane.getColumnIndex(node);
        currentGame.setLastTurn(new GameTurn(value,rowIndex, colIndex));
        ClientServiceImpl cl = new ClientServiceImpl();
        if(cl.isSafe(board, rowIndex, colIndex, value)) {
            currentGame.setBoard(board);
            node.setEditable(false);
            node.setStyle("-fx-font-weight: bold; -fx-background-color: white;");
        } else {
            node.setEditable(true);
            node.setStyle("-fx-font-weight: none; -fx-background-color: red;");
        }

        int[][] boardToBeSolved = new int[9][9];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boardToBeSolved[i][j] = board[i][j];
            }
        }

        boolean canBeSolved = cl.canSolve(boardToBeSolved, boardToBeSolved.length);
        if(canBeSolved && currentGame.getBoard() == currentGame.getSolution()) {
            currentGame.setWon();
            double score = currentGame.getCurrentScore();
            int minutes = Integer.parseInt(timeLabel.getText());
            String title = "Congratulations, You won!";
            String message = String.format("Congratulations, You won!\n\tNickname: %s\n\tTotal score: %.2f\n\tTime: %d minutes",
                    currentPlayer.getNickname(),
                    score,
                    minutes);
            cl.showMessage(title, message, currentPlayer, currentGame, minutes);
        } else if(!canBeSolved) {
            double score = currentGame.getCurrentScore();
            int minutes = Integer.parseInt(timeLabel.getText());
            String title = "Sorry, You fail!";
            String message = String.format("Sorry, You fail!\n\tNickname: %s\n\tTotal score: %.2f\n\tTime: %d minutes",
                    currentPlayer.getNickname(),
                    score,
                    minutes);
            cl.showMessage(title, message, currentPlayer, currentGame, minutes);
        }
    }

    /**
     * Премества един ход напред.
     *
     * @param event - събитие.
     */
    @FXML
    void onRedoBtnClicked(MouseEvent event) {
        // TODO: да проверя сетването на стойностите
        if(!currentGame.getUndoStack().isEmpty()) {
            int value = currentGame.getUndoStack().peek().getValue();
            int rowIndex = currentGame.getUndoStack().peek().getValue();
            int colIndex = currentGame.getUndoStack().peek().getValue();

            if(currentGame.getLastTurn() != null) {
                GameTurn temp = currentGame.getUndoStack().peek();
                currentGame.getUndoStack().pop();
                currentGame.getUndoStack().push(currentGame.getLastTurn());
                currentGame.setLastTurn(temp);
            }
            // сетва value на позиция [rowIndex,colIndex] в grid-а на дъската
            if(0 <= rowIndex && rowIndex <= 2 && 0 <= colIndex && colIndex <= 2) {
                ((TextField)zeroRowZeroColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (0 <= rowIndex && rowIndex <= 2 && 3 <= colIndex && colIndex <= 5) {
                ((TextField)zeroRowFirstColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            }  else if (0 <= rowIndex && rowIndex <= 2 && 6 <= colIndex && colIndex <= 8) {
                ((TextField)zeroRowSecondColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (3 <= rowIndex && rowIndex <= 5 && 0 <= colIndex && colIndex <= 2) {
                ((TextField)firstRowZeroColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (3 <= rowIndex && rowIndex <= 5 && 3 <= colIndex && colIndex <= 5) {
                ((TextField)firstRowFirstColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (3 <= rowIndex && rowIndex <= 5 && 6 <= colIndex && colIndex <= 8) {
                ((TextField)firstRowSecondColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (6 <= rowIndex && rowIndex <= 8 && 0 <= colIndex && colIndex <= 2) {
                ((TextField)secondRowZeroColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (6 <= rowIndex && rowIndex <= 8 && 3 <= colIndex && colIndex <= 5) {
                ((TextField)secondRowFirstColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else {
                ((TextField)secondRowSecondColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            }
        }
    }

    /**
     * Връща един ход назад.
     *
     * @param event - събитие.
     */
    @FXML
    void onUndoBtnClicked(MouseEvent event) {
        // TODO: да проверя сетването на стойностите
        if(currentGame.getLastTurn() != null) {
            int value = currentGame.getLastTurn().getValue();
            int rowIndex = currentGame.getLastTurn().getValue();
            int colIndex = currentGame.getLastTurn().getValue();

            if(!currentGame.getUndoStack().isEmpty()) {
                GameTurn temp = currentGame.getLastTurn();
                currentGame.setLastTurn(currentGame.getUndoStack().peek());
                currentGame.getUndoStack().pop();
                currentGame.getUndoStack().push(temp);
            }
            // сетва value на позиция [rowIndex,colIndex] в grid-а на дъската
            if(0 <= rowIndex && rowIndex <= 2 && 0 <= colIndex && colIndex <= 2) {
                ((TextField)zeroRowZeroColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (0 <= rowIndex && rowIndex <= 2 && 3 <= colIndex && colIndex <= 5) {
                ((TextField)zeroRowFirstColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            }  else if (0 <= rowIndex && rowIndex <= 2 && 6 <= colIndex && colIndex <= 8) {
                ((TextField)zeroRowSecondColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (3 <= rowIndex && rowIndex <= 5 && 0 <= colIndex && colIndex <= 2) {
                ((TextField)firstRowZeroColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (3 <= rowIndex && rowIndex <= 5 && 3 <= colIndex && colIndex <= 5) {
                ((TextField)firstRowFirstColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (3 <= rowIndex && rowIndex <= 5 && 6 <= colIndex && colIndex <= 8) {
                ((TextField)firstRowSecondColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (6 <= rowIndex && rowIndex <= 8 && 0 <= colIndex && colIndex <= 2) {
                ((TextField)secondRowZeroColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else if (6 <= rowIndex && rowIndex <= 8 && 3 <= colIndex && colIndex <= 5) {
                ((TextField)secondRowFirstColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            } else {
                ((TextField)secondRowSecondColGrid.getChildren().get(colIndex)).setText(String.valueOf(value));
            }
        }
    }

    /**
     * Инициализира контролера.
     */
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
}