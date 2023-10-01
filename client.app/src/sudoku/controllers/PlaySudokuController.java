package sudoku.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import server.entities.*;
import sudoku.services.ClientService;
import sudoku.servicesImpls.ClientServiceImpl;

import javax.swing.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Timer;

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
    private Text sudokuLevelLabel;

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
    private Button eightsBtn;

    @FXML
    private Button fivesBtn;

    @FXML
    private Button foursBtn;

    @FXML
    private Button ninesBtn;

    @FXML
    private Button onesBtn;

    @FXML
    private Button sevensBtn;

    @FXML
    private Button sixsBtn;

    @FXML
    private Button threesBtn;

    @FXML
    private Button twoesBtn;


    private Player currentPlayer;

    private Game currentGame;

    private SudokuBoard board;

    private int correctValues = 0;
    private Timer gameTimer = new Timer();

    private void fillBox(SudokuBoard board, GridPane grid, int rowStInd, int rowEndInd, int colStInd, int colEndInd) {
        int index = 0;
        for (int i = rowStInd; i <= rowEndInd; i++) {
            for (int j = colStInd; j <= colEndInd; j++) {
                TextField cell = ((TextField) grid.getChildren().get(index));
                cell.setText(Integer.toString(board.getCellValue(i, j)));
                cell.setEditable(false);
                cell.setStyle("-fx-font-weight: bold;");
                if (board.getCellValue(i, j) == 0) {
                    cell.setText("");
                    cell.setEditable(true);
                    cell.setStyle("-fx-font-weight: none;");
                }
                index++;
            }
        }
    }

    private void fillBoxSolution(SudokuBoard board, GridPane grid, int rowStInd, int rowEndInd, int colStInd, int colEndInd) {
        int index = 0;
        for (int i = rowStInd; i <= rowEndInd; i++) {
            for (int j = colStInd; j <= colEndInd; j++) {
                TextField cell = ((TextField) grid.getChildren().get(index));
                cell.setText(Integer.toString(board.getCellValue(i, j)));
                cell.setEditable(false);
                cell.setStyle("-fx-font-weight: bold;");
                index++;
            }
        }
    }

    private void timerTick() {
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            int minutes = 0;
            int seconds = 0;

            boolean hasBeenOpened = false;

            @Override
            public void run() {
                if (seconds == 60) {
                    minutes += 1;
                    seconds = 0;

                    hasBeenOpened = false;
                }
                if (minutes >= 10 && seconds >= 10) {
                    timeLabel.setText(String.format("Time: %d:%d", minutes, seconds));
                } else {
                    timeLabel.setText(String.format("Time: 0%d:0%d", minutes, seconds));
                }
                seconds++;

                if (minutes > 0 && minutes % 5 == 0 && !hasBeenOpened) {
                    int answer = JOptionPane.showOptionDialog(null, "Do you still playing?", "Playing status",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);
                    if (answer == JOptionPane.NO_OPTION) {
                        this.cancel();
                        Platform.exit();
                    }
                    hasBeenOpened = true;
                }
            }
        }, 0, 1000);
    }

    private void fillSudokuGrid(SudokuBoard board) {
        final int boardSize = board.getBoardSize();
        int i = 0;
        for (int j = 0; j < boardSize; j += 3) {
            for (int k = 0; k < boardSize; k += 3) {
                GridPane innerGrid = (GridPane) sudokuGrid.getChildren().get(i);
                fillBox(board, innerGrid, j, j + 2, k, k + 2);
                i++;
            }
        }
    }

    private void onNumberBtnClicked(int number) {
        int boardSize = 9;
        for (int i = 0; i < boardSize; ++i) {
            GridPane innerGrid = (GridPane) sudokuGrid.getChildren().get(i);
            for (int j = 0; j < boardSize; ++j) {
                TextField txtField = (TextField) innerGrid.getChildren().get(j);
                if (txtField.getText().equals(String.valueOf(number))) {
                    txtField.setStyle("-fx-background-color: yellow;");
                } else {
                    txtField.setStyle("-fx-background-color: white;");
                }
            }
        }
    }

    private void setCells(int value, int rowIndex, int colIndex) {
        String valueToChange = value != 0 ? Integer.toString(value) : "";
        int innerGridIndex = (colIndex / 3) + (rowIndex / 3 * 3);
        GridPane innerGrid = (GridPane) sudokuGrid.getChildren().get(innerGridIndex);

        int cellIndex = 8 - ((8 - rowIndex) % 3 * 3 + (8 - colIndex) % 3);
        TextField cell = (TextField) innerGrid.getChildren().get(cellIndex);
        cell.setText(valueToChange);
        ClientServiceImpl csi = new ClientServiceImpl();
        if (value == 0 || csi.isSafe(board, rowIndex, colIndex, value)) {
            cell.setBackground(Background.fill(Paint.valueOf("white")));
        } else {
            cell.setBackground(Background.fill(Paint.valueOf("red")));
        }
    }

    private SudokuBoard initGame(int sudokuBoardEmptyCells,
                                 SudokuLevel sudokuLevel, String playerNickname, ClientService client)
            throws RemoteException {
        SudokuBoard sudokuBoard = client.initGame(sudokuLevel, 9, playerNickname);
        currentGame.setLevel(sudokuLevel);
        currentGame.setEmptyCells(sudokuBoardEmptyCells);

        return sudokuBoard;
    }

    void receive(Stage s) {
        Pair<Game, Player> data = (Pair<Game, Player>) s.getUserData();

        currentPlayer = data.getValue();
        currentGame = data.getKey();

        board = currentGame.getGameBoard();

        fillSudokuGrid(board);

        timerTick();

        nicknameLabel.setText(data.getValue().getNickname());
        scoreLabel.setText(String.valueOf(String.format("Score: %d", data.getKey().getCurrentScore())));
        sudokuLevelLabel.setText(String.valueOf(data.getKey().getLevel()));
    }

    @FXML
    void onOnesBtnClicked() {
        onNumberBtnClicked(1);
    }

    @FXML
    void onTwoesBtnClicked() {
        onNumberBtnClicked(2);
    }

    @FXML
    void onThreesBtnClicked() {
        onNumberBtnClicked(3);
    }

    @FXML
    void onFoursBtnClicked() {
        onNumberBtnClicked(4);
    }

    @FXML
    void onFivesBtnClicked() {
        onNumberBtnClicked(5);
    }

    @FXML
    void onSixsBtnClicked() {
        onNumberBtnClicked(6);
    }

    @FXML
    void onSevensBtnClicked() {
        onNumberBtnClicked(7);
    }

    @FXML
    void onEightsBtnClicked() {
        onNumberBtnClicked(8);
    }

    @FXML
    void onNinesBtnClicked() {
        onNumberBtnClicked(9);
    }


    @FXML
    void onHelpBtnClicked() {
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
    void onNewGameBtnClicked() {
        ClientService client = new ClientServiceImpl();
        SudokuBoard board;

        currentGame = new Game();
        currentGame.setPlayer(currentPlayer);

        try {
            switch (sudokuLevelLabel.getText()) {
                case "MEDIUM" -> board = initGame(SudokuLevel.EASY.getMaxEmptyCells(),
                        SudokuLevel.EASY, nicknameLabel.getText(), client);
                case "HARD" -> board = initGame(SudokuLevel.MEDIUM.getMaxEmptyCells(),
                        SudokuLevel.MEDIUM, nicknameLabel.getText(), client);

                default -> board = initGame(SudokuLevel.HARD.getMaxEmptyCells(),
                        SudokuLevel.HARD, nicknameLabel.getText(), client);
            }
            scoreLabel.setText(String.valueOf(String.format("Score: %d", currentGame.getCurrentScore())));
            fillSudokuGrid(board);

            gameTimer.cancel();
            timerTick();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, "Sudoku board cannot be initialized", "Server error", JOptionPane.ERROR);
        }
    }

    private void makeNotEditable(GridPane grid) {
        grid.getChildren().forEach(tf -> ((TextField) tf).setEditable(Boolean.FALSE));
    }

    @FXML
    void onInputTextChanged(KeyEvent event) {
        TextField node = (TextField) event.getSource();

        char[] text = node.getText().toCharArray();
        if (text.length != 1 || (text[0] < '1' || text[0] > '9')) {
            node.setText(node.getText().replaceAll("0", ""));
            return;
        }

        int value = Integer.parseInt(node.getText());

        int sqrt = (int) Math.sqrt(board.getBoardSize());
        GridPane innerGrid = (GridPane) node.getParent();
        int rowGridIndex = (GridPane.getRowIndex(innerGrid) != null) ? GridPane.getRowIndex(innerGrid) : 0;
        int colGridIndex = (GridPane.getColumnIndex(innerGrid) != null) ? GridPane.getColumnIndex(innerGrid) : 0;

        int rowIndex = 0, colIndex = 0;
        for (int currentRowIndex = 0; currentRowIndex < sqrt; currentRowIndex++) {
            for (int urrentColIndex = 0; urrentColIndex < sqrt; urrentColIndex++) {
                if (rowGridIndex == currentRowIndex && colGridIndex == urrentColIndex) {
                    int txtFieldRowIndex = (GridPane.getRowIndex(node) != null) ? GridPane.getRowIndex(node) : 0;
                    int txtFieldColIndex = (GridPane.getColumnIndex(node) != null) ? GridPane.getColumnIndex(node) : 0;
                    rowIndex = rowGridIndex * sqrt + txtFieldRowIndex;
                    colIndex = colGridIndex * sqrt + txtFieldColIndex;
                }
            }
        }

        currentGame.addLastTurn(new GameTurn(value, rowIndex, colIndex, Integer.parseInt(scoreLabel.getText().substring(7))));
        ClientServiceImpl client = new ClientServiceImpl();
        if (client.isSafe(board, rowIndex, colIndex, value)) {
            correctValues++;
            currentGame.setCurrentScore(5);
            scoreLabel.setText(String.valueOf(String.format("Score: %d", currentGame.getCurrentScore())));
            currentGame.setGameBoard(board);
            node.setEditable(false);
            currentGame.getUndoStack().pop();
            node.setStyle("-fx-background-color: white;");
        } else {
            currentGame.setCurrentScore(-5);
            scoreLabel.setText(String.valueOf(String.format("Score: %d", currentGame.getCurrentScore())));
            node.setEditable(true);
            node.setStyle("-fx-background-color: red;");
        }

        SudokuBoard boardToBeSolved = new SudokuBoard(9);
        for (int currentRowIndex = 0; currentRowIndex < board.getBoardSize(); currentRowIndex++) {
            for (int currentColIndex = 0; currentColIndex < board.getBoardSize(); currentColIndex++) {
                boardToBeSolved.setCellValue(currentRowIndex, currentColIndex,
                        board.getCellValue(currentRowIndex, currentColIndex));
            }
        }

        boolean canBeSolved = client.canSolve(boardToBeSolved, boardToBeSolved.getBoardSize());
        final int boardSize = currentGame.getGameBoard().getBoardSize();

        if (!canBeSolved || currentGame.getCurrentScore() <= -50) {
            gameTimer.cancel();
            int index = 0;
            for (int currentRowIndex = 0; currentRowIndex < boardSize; currentRowIndex += 3) {
                for (int currentColIndex = 0; currentColIndex < boardSize; currentColIndex += 3) {
                    GridPane innerSudokuGrid = (GridPane) sudokuGrid.getChildren().get(index);
                    fillBoxSolution(currentGame.getSolution(), innerSudokuGrid, currentRowIndex, currentRowIndex + 2, currentColIndex, currentColIndex + 2);
                    index++;
                }
            }
            double score = currentGame.getCurrentScore();
            String time = timeLabel.getText();
            String title = "Sorry, You fail!";
            String message = String.format("Sorry, You fail!\n\tNickname: %s\n\tTotal score: %.2f\n\tTime: %s",
                    currentPlayer.getNickname(),
                    score,
                    time);
            client.showMessage(title, message, currentPlayer, currentGame, time);
        }

        if (correctValues == currentGame.getLevel().getMaxEmptyCells()) {
            gameTimer.cancel();
            int index = 0;
            for (int i = 0; i < boardSize; i += 3) {
                for (int j = 0; j < boardSize; j += 3) {
                    GridPane innerSudokuGrid = (GridPane) sudokuGrid.getChildren().get(index);
                    makeNotEditable(innerSudokuGrid);
                    index++;
                }
            }
            currentGame.setWon();
            double score = currentGame.getCurrentScore();
            String time = timeLabel.getText();
            String title = "Congratulations, You won!";
            String message = String.format("Congratulations, You won!\n\tNickname: %s\n\tTotal score: %.2f\n\tTime: %s",
                    currentPlayer.getNickname(),
                    score,
                    time);
            client.showMessage(title, message, currentPlayer, currentGame, time);
        }
    }

    @FXML
    void onRedoBtnClicked() {
        GameTurn firstRedo = currentGame.getFirstRedo();

        int value = firstRedo.getValue();
        int rowIndex = firstRedo.getRowIndex();
        int colIndex = firstRedo.getColIndex();
        currentGame.clearRedoStack();
        scoreLabel.setText(String.valueOf(String.format("Score: %d", firstRedo.getScore())));

        setCells(value, rowIndex, colIndex);
        setCells(0, firstRedo.getRowIndex(), firstRedo.getColIndex());
    }

    @FXML
    void onUndoBtnClicked() {
        GameTurn[] lastUndo = currentGame.getLastUndo();
        if (Arrays.stream(lastUndo)
                .allMatch(Objects::nonNull)) {
            GameTurn currentTurn = lastUndo[0];
            GameTurn prevTurn = lastUndo[1];
            int value = prevTurn.getValue();
            int rowIndex = prevTurn.getRowIndex();
            int colIndex = prevTurn.getColIndex();
            scoreLabel.setText(String.valueOf(String.format("Score: %d", prevTurn.getScore())));

            setCells(value, rowIndex, colIndex);
            setCells(0, currentTurn.getRowIndex(), currentTurn.getColIndex());
        }
    }

    @FXML
    void initialize() {
        assert eightsBtn != null : "fx:id=\"eightsBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert firstRowFirstColGrid != null : "fx:id=\"firstRowFirstColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert firstRowSecondColGrid != null : "fx:id=\"firstRowSecondColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert firstRowZeroColGrid != null : "fx:id=\"firstRowZeroColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert fivesBtn != null : "fx:id=\"fivesBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert foursBtn != null : "fx:id=\"foursBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert helpButton != null : "fx:id=\"helpButton\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert newGameButton != null : "fx:id=\"newGameButton\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert nicknameLabel != null : "fx:id=\"nicknameLabel\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert ninesBtn != null : "fx:id=\"ninesBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert onesBtn != null : "fx:id=\"onesBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert redoButton != null : "fx:id=\"redoButton\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert scoreLabel != null : "fx:id=\"scoreLabel\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert secondRowFirstColGrid != null : "fx:id=\"secondRowFirstColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert secondRowSecondColGrid != null : "fx:id=\"secondRowSecondColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert secondRowZeroColGrid != null : "fx:id=\"secondRowZeroColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert sevensBtn != null : "fx:id=\"sevensBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert sixsBtn != null : "fx:id=\"sixsBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert sudokuGrid != null : "fx:id=\"sudokuGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert sudokuLevelLabel != null : "fx:id=\"sudokuLevelLbl\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert threesBtn != null : "fx:id=\"threesBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert timeLabel != null : "fx:id=\"timeLabel\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert twoesBtn != null : "fx:id=\"twoesBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert undoButton != null : "fx:id=\"undoButton\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert zeroRowFirstColGrid != null : "fx:id=\"zeroRowFirstColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert zeroRowSecondColGrid != null : "fx:id=\"zeroRowSecondColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert zeroRowZeroColGrid != null : "fx:id=\"zeroRowZeroColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
    }
}