package sudoku.servicesImpls;

import server.entities.Game;
import server.entities.Player;
import server.entities.SudokuLevel;
import server.services.SudokuService;
import server.servicesImpls.SudokuServiceImpl;
import sudoku.services.ClientService;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Клас, който имплементира ClientService
 */
public class ClientServiceImpl implements ClientService {

    private final Logger logger = Logger.getLogger(ClientServiceImpl.class.getName());

    /**
     * Проверява дали дадена позиция е безопасна.
     *
     * @param board     - дъска.
     * @param rowI      - индекс на ред.
     * @param colI      - индекс на колона.
     * @param cellValue - стойност.
     * @return true - при безопасна,false - при небезопасна
     */
    private boolean isSafe(int[][] board,
                           int rowI, int colI,
                           int cellValue) {
        for (int colIndex = 0; colIndex < board.length; ++colIndex) {
            if (board[rowI][colIndex] == cellValue || board[rowI][colIndex] < 0 || board[rowI][colIndex] > 9) {
                return false;
            }
        }
        for (int[] boardRow : board) {
            if (boardRow[colI] == cellValue) {
                return false;
            }
        }

        int sqrt = (int) Math.sqrt(board.length);
        int boxRowStartIndex = rowI - rowI % sqrt;
        int boxColStartIndex = colI - colI % sqrt;

        for (int rowIndex = boxRowStartIndex;
             rowIndex < boxRowStartIndex + sqrt; rowIndex++) {
            for (int colIndex = boxColStartIndex;
                 colIndex < boxColStartIndex + sqrt; colIndex++) {
                if (board[rowIndex][colIndex] == cellValue) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Решава судоку пъзела.
     *
     * @param board            - дъска.
     * @param numberOfRowsCols - брой редове/колони
     * @return true - при решено судоку, false - при нерешено судоку
     */
    private boolean solveSudoku(
            int[][] board, int numberOfRowsCols) {
        int rowIndex = -1;
        int colIndex = -1;
        boolean isEmptyCell = true;
        for (int rowI = 0; rowI < numberOfRowsCols; rowI++) {
            for (int colI = 0; colI < numberOfRowsCols; colI++) {
                if (board[rowI][colI] == 0) {
                    rowIndex = rowI;
                    colIndex = colI;

                    isEmptyCell = false;
                    break;
                }
            }
            if (!isEmptyCell) {
                break;
            }
        }
        if (isEmptyCell) {
            return true;
        }
        for (int currentCellValue = 1; currentCellValue <= numberOfRowsCols; currentCellValue++) {
            if (isSafe(board, rowIndex, colIndex, currentCellValue)) {
                board[rowIndex][colIndex] = currentCellValue;
                if (solveSudoku(board, numberOfRowsCols)) {
                    return true;
                } else {
                    board[rowIndex][colIndex] = 0;
                }
            }
        }
        return false;
    }

    @Override
    public int[][] initGame(SudokuLevel level, String nickname) {

        Player player = new Player(nickname);
        Game game = new Game(level, level.getMaxEmptyCells(), new int[9][9], new int[9][9], 0, player);
        try {
            Registry r = LocateRegistry.getRegistry("localhost", 1099);
            try {
                SudokuService server = (SudokuService) r.lookup("SudokuGame");
                server.setGame(game);
                server.setPlayer(player);
                System.out.println(game);
                //TODO Не променяш game.board, а променяш server.game.board
                game.setBoard(server.fillValues());
            } catch (NotBoundException | AccessException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
            }
        } catch (RemoteException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }

        return game.getBoard();
    }

    @Override
    public int[][] makeSolution(int[][] board, int N) {
        int[][] solved = new int[N][N];
        if (solveSudoku(board, N)) {
            for (int r = 0; r < N; r++) {
                for (int d = 0; d < N; d++) {
                    solved[r][d] = board[r][d];
                }
            }
        }
        return solved;
    }

    @Override
    public boolean updateGridBoard(int[][] updatedGridBoard, int value, int rowIndex, int colIndex) {
        /* TODO: проверяваме дали дъската в текущото си състояние отговаря на всички
            изисквания за валиден ход и ако да връщаме дъската, ако ли не връщаме custom грешка,
            прихващаме я в контролера и индикираме грешното състояние с червен фон зад цифрата
            като даваме възможност за корекция, преповтаряйки целия процес, докато не въведем
            валидна стойност.
        */

        return isSafe(updatedGridBoard, rowIndex, colIndex, value);
    }

    @Override
    public void showMessage(String title, String message, Player player, Game game, int totalMinutes) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        logClientGameOutcome(player.getNickname(), game.getLevel(), game.getCurrentScore(), game.isWon(), totalMinutes);
    }

    /**
     * Логва резултата (победа/загуба) на играта.
     *
     * @param nickname     - никнейм.
     * @param level        - ниво на трудност.
     * @param totalScore   - краен резултат.
     * @param isWon        - дали е победа или не.
     * @param totalMinutes - брой на минути за решаване.
     */
    private void logClientGameOutcome(String nickname, SudokuLevel level, int totalScore, boolean isWon,
                                      int totalMinutes) {
        String fileName = nickname + "_sudoku_outcome.txt";
        LocalDate currentDate = LocalDate.now();

        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(String.format("""
                            \t%s
                            \tNickname: %s, SudokuLevel: %s, TotalScore: %d,
                            \tIsWon: %s, Minutes: %d
                            """, currentDate, nickname,
                    level, totalScore, isWon, totalMinutes));
            writer.close();
        } catch (IOException ioEx) {
            logger.log(Level.SEVERE, ioEx.getMessage());
        }
    }
}