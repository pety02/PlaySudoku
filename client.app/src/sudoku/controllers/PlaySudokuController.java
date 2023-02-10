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
import server.entities.Game;
import server.entities.GameTurn;
import server.entities.Player;
import server.entities.SudokuLevel;
import sudoku.services.ClientService;
import sudoku.servicesImpls.ClientServiceImpl;

import javax.swing.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Timer;

/**
 * Контролер на игралния прозорец.
 */
public class PlaySudokuController {

    /**
     * Метод при натискане на бутон 8.
     */
    @FXML
    void onEightsBtnClicked() {
        onNumberBtnClicked(8);
    }

    /**
     * Метод при натискане на бутон 5.
     */
    @FXML
    void onFivesBtnClicked() {
        onNumberBtnClicked(5);
    }

    /**
     * Метод при натискане на бутон 4.
     */
    @FXML
    void onFoursBtnClicked() {
        onNumberBtnClicked(4);
    }

    /**
     * Метод при натискане на бутон 9.
     */
    @FXML
    void onNinesBtnClicked() {
        onNumberBtnClicked(9);
    }

    /**
     * Метод при натискане на бутон 1.
     */
    @FXML
    void onOnesBtnClicked() {
        onNumberBtnClicked(1);
    }

    /**
     * Метод при натискане на бутон 7.
     */
    @FXML
    void onSevensBtnClicked() {
        onNumberBtnClicked(7);
    }

    /**
     * Метод при натискане на бутон 6.
     */
    @FXML
    void onSixsBtnClicked() {
        onNumberBtnClicked(6);
    }

    /**
     * Метод при натискане на бутон 3.
     */
    @FXML
    void onThreesBtnClicked() {
        onNumberBtnClicked(3);
    }

    /**
     * Метод при натискане на бутон 2.
     */
    @FXML
    void onTwoesBtnClicked() {
        onNumberBtnClicked(2);
    }

    /**
     * Отваря инструкциите на играта.
     */
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

    /**
     * Създава нова игра от същата трудност.
     *
     * @throws RemoteException
     */
    @FXML
    void onNewGameBtnClicked() throws RemoteException {
        ClientService cl = new ClientServiceImpl();
        int[][] board;

        currentGame = new Game();
        currentGame.setPlayer(currentPlayer);

        switch (sudokuLevelLbl.getText()) {
            case "MEDIUM" -> {
                board = cl.initGame(SudokuLevel.MEDIUM, nicknameLabel.getText());
                currentGame.setLevel(SudokuLevel.MEDIUM);
                currentGame.setEmptyCells(SudokuLevel.MEDIUM.getMaxEmptyCells());
            }
            case "HARD" -> {
                board = cl.initGame(SudokuLevel.HARD, nicknameLabel.getText());
                currentGame.setLevel(SudokuLevel.HARD);
                currentGame.setEmptyCells(SudokuLevel.HARD.getMaxEmptyCells());
            }

            default -> {
                board = cl.initGame(SudokuLevel.EASY, nicknameLabel.getText());
                currentGame.setLevel(SudokuLevel.EASY);
                currentGame.setEmptyCells(SudokuLevel.EASY.getMaxEmptyCells());
            }
        }
        scoreLabel.setText(String.valueOf(String.format("Score: %d", currentGame.getCurrentScore())));

        fillSudokuGrid(board);

        myTimer.cancel();
        timerTick();
    }

    /**
     * Прави всички текстови кутии в един грид нередактирируеми.
     *
     * @param grid - текущ грид.
     */
    private void makeNotEditable(GridPane grid) {
        grid.getChildren().forEach(tf -> ((TextField) tf).setEditable(Boolean.FALSE));
    }

    /**
     * Задава логиката на играта. Позволява на играч да играе.
     *
     * @param event - събитие.
     */
    @FXML
    void onInputTextChanged(KeyEvent event) {
        TextField node = (TextField) event.getSource();

        char[] text = node.getText().toCharArray();
        if (text.length != 1 || (text[0] < '1' || text[0] > '9')) {
            node.setText(node.getText().replaceAll("0", ""));
            return;
        }

        int value = Integer.parseInt(node.getText());

        int sqrt = (int) Math.sqrt(board.length);
        GridPane innerGrid = (GridPane) node.getParent();
        int rowGridIndex = (GridPane.getRowIndex(innerGrid) != null) ? GridPane.getRowIndex(innerGrid) : 0;
        int colGridIndex = (GridPane.getColumnIndex(innerGrid) != null) ? GridPane.getColumnIndex(innerGrid) : 0;

        int rowIndex = 0, colIndex = 0;
        for (int i = 0; i < sqrt; i++) {
            for (int j = 0; j < sqrt; j++) {
                if (rowGridIndex == i && colGridIndex == j) {
                    int txtFieldRowIndex = (GridPane.getRowIndex(node) != null) ? GridPane.getRowIndex(node) : 0;
                    int txtFieldColIndex = (GridPane.getColumnIndex(node) != null) ? GridPane.getColumnIndex(node) : 0;
                    rowIndex = rowGridIndex * sqrt + txtFieldRowIndex;
                    colIndex = colGridIndex * sqrt + txtFieldColIndex;
                    System.out.printf("Row - Col: %d %d%n", rowIndex, colIndex);
                }
            }
        }

        currentGame.addLastTurn(new GameTurn(value, rowIndex, colIndex, Integer.parseInt(scoreLabel.getText().substring(7))));
        ClientServiceImpl cl = new ClientServiceImpl();
        if (cl.isSafe(board, rowIndex, colIndex, value)) {
            correctValues++;
            currentGame.setCurrentScore(5);
            scoreLabel.setText(String.valueOf(String.format("Score: %d", currentGame.getCurrentScore())));
            currentGame.setBoard(board);
            node.setEditable(false);
            currentGame.getUndoStack().pop();
            node.setStyle("-fx-background-color: white;");
        } else {
            currentGame.setCurrentScore(-5);
            scoreLabel.setText(String.valueOf(String.format("Score: %d", currentGame.getCurrentScore())));
            node.setEditable(true);
            node.setStyle("-fx-background-color: red;");
        }

        int[][] boardToBeSolved = new int[9][9];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boardToBeSolved[i][j] = board[i][j];
            }
        }

        boolean canBeSolved = cl.canSolve(boardToBeSolved, boardToBeSolved.length);
        final int boardSize = currentGame.getBoard().length;

        if (!canBeSolved || currentGame.getCurrentScore() <= -50) {
            myTimer.cancel();
            int index = 0;
            for (int i = 0; i < boardSize; i += 3) {
                for (int j = 0; j < boardSize; j += 3) {
                    GridPane innerSudokuGrid = (GridPane) sudokuGrid.getChildren().get(index);
                    fillBoxSolution(currentGame.getSolution(), innerSudokuGrid, i, i + 2, j, j + 2);
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
            cl.showMessage(title, message, currentPlayer, currentGame, time);
        }

        if (correctValues == currentGame.getLevel().getMaxEmptyCells()) {
            myTimer.cancel();
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
            cl.showMessage(title, message, currentPlayer, currentGame, time);
        }
    }

    /**
     * Премества един ход напред.
     */
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

    /**
     * Връща един ход назад.
     */
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

    /**
     * Запълва един грид със стойности от генерираното вече судоку.
     *
     * @param board     - дъска.
     * @param grid      - грид.
     * @param rowStInd  - начален индекс за редовете.
     * @param rowEndInd - краен индекс за редовете.
     * @param colStInd  - начален индекс за колоните.
     * @param colEndInd - краен индекс за колоните.
     */
    private void fillBox(int[][] board, GridPane grid, int rowStInd, int rowEndInd, int colStInd, int colEndInd) {
        int index = 0;
        for (int i = rowStInd; i <= rowEndInd; i++) {
            for (int j = colStInd; j <= colEndInd; j++) {
                TextField cell = ((TextField) grid.getChildren().get(index));
                cell.setText(Integer.toString(board[i][j]));
                cell.setEditable(false);
                cell.setStyle("-fx-font-weight: bold;");
                if (board[i][j] == 0) {
                    cell.setText("");
                    cell.setEditable(true);
                    cell.setStyle("-fx-font-weight: none;");
                }
                index++;
            }
        }
    }

    /**
     * Запълва судоку грида с примерно решение на пъзела в случая на загуба.
     *
     * @param board     - дъска.
     * @param grid      - грид.
     * @param rowStInd  - начален индекз за реодвете.
     * @param rowEndInd - краен индекс за редовете.
     * @param colStInd  - начален индекс за колоните.
     * @param colEndInd - краен индекс за колоните.
     */
    private void fillBoxSolution(int[][] board, GridPane grid, int rowStInd, int rowEndInd, int colStInd, int colEndInd) {
        int index = 0;
        for (int i = rowStInd; i <= rowEndInd; i++) {
            for (int j = colStInd; j <= colEndInd; j++) {
                TextField cell = ((TextField) grid.getChildren().get(index));
                cell.setText(Integer.toString(board[i][j]));
                cell.setEditable(false);
                cell.setStyle("-fx-font-weight: bold;");
                index++;
            }
        }
    }

    /**
     * Създава и стартира теймер, който на всеки 5 минути прави запитване към играча дали още играе.
     */
    private void timerTick() {
        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
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

    /**
     * Приема информация от HomeViewSudokuController-а при инициализация на нова игра.
     *
     * @param s - сцена.
     */
    void receive(Stage s) {
        Pair<Game, Player> data = (Pair<Game, Player>) s.getUserData();

        currentPlayer = data.getValue();
        currentGame = data.getKey();

        board = currentGame.getBoard();

        fillSudokuGrid(board);

        timerTick();

        nicknameLabel.setText(data.getValue().getNickname());
        scoreLabel.setText(String.valueOf(String.format("Score: %d", data.getKey().getCurrentScore())));
        sudokuLevelLbl.setText(String.valueOf(data.getKey().getLevel()));
    }

    /**
     * Запълва даден судоку грид със стойности.
     *
     * @param board - дъска.
     */
    private void fillSudokuGrid(int[][] board) {
        final int boardSize = board.length;
        int i = 0;
        for (int j = 0; j < boardSize; j += 3) {
            for (int k = 0; k < boardSize; k += 3) {
                GridPane innerGrid = (GridPane) sudokuGrid.getChildren().get(i);
                fillBox(board, innerGrid, j, j + 2, k, k + 2);
                i++;
            }
        }
    }

    /**
     * Променя фона на клетките, в които се намира дадена цифра.
     *
     * @param number - число (цифра).
     */
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

    /**
     * При undo или redo функционалностите поставя дадена стойност в определна от нейните индекси клетка.
     *
     * @param value    - стойност.
     * @param rowIndex - индекс на ред.
     * @param colIndex - индекс на колона.
     */
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

    /**
     * Инициализира контролера.
     */
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
        assert sudokuLevelLbl != null : "fx:id=\"sudokuLevelLbl\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert threesBtn != null : "fx:id=\"threesBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert timeLabel != null : "fx:id=\"timeLabel\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert twoesBtn != null : "fx:id=\"twoesBtn\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert undoButton != null : "fx:id=\"undoButton\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert zeroRowFirstColGrid != null : "fx:id=\"zeroRowFirstColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert zeroRowSecondColGrid != null : "fx:id=\"zeroRowSecondColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
        assert zeroRowZeroColGrid != null : "fx:id=\"zeroRowZeroColGrid\" was not injected: check your FXML file 'PlaySudoku.fxml'.";
    }

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

    private int[][] board;

    private int correctValues = 0;
    private Timer myTimer = new Timer();
}