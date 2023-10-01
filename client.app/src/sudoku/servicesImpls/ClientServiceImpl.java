package sudoku.servicesImpls;

import javafx.application.Platform;
import server.entities.Game;
import server.entities.Player;
import server.entities.SudokuBoard;
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

public class ClientServiceImpl implements ClientService {
    private final Logger logger = Logger.getLogger(ClientServiceImpl.class.getName());

    @Override
    public boolean isSafe(SudokuBoard board,
                          int rowI, int colI,
                          int cellValue) {
        for (int colIndex = 0; colIndex < board.getBoardSize(); ++colIndex) {
            if (board.getCellValue(rowI, colI) == cellValue || board.getCellValue(rowI, colI) < 0
                    || board.getCellValue(rowI, colI) > 9) {
                return false;
            }
        }
        for (int rowIndex = 0; rowIndex < board.getBoardSize(); rowIndex++) {
            for (int colIndex = 0; colIndex < board.getBoardSize(); colIndex++) {
                if (board.getCellValue(rowIndex, colIndex) == cellValue) {
                    return false;
                }
            }
        }

        int sqrt = (int) Math.sqrt(board.getBoardSize());
        int boxRowStartIndex = rowI - rowI % sqrt;
        int boxColStartIndex = colI - colI % sqrt;

        for (int rowIndex = boxRowStartIndex;
             rowIndex < boxRowStartIndex + sqrt; rowIndex++) {
            for (int colIndex = boxColStartIndex;
                 colIndex < boxColStartIndex + sqrt; colIndex++) {
                if (board.getCellValue(rowI, colI) == cellValue) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public SudokuBoard initGame(SudokuLevel level, int boardSize, String nickname) {

        int sqrtOfRowsCols = (int) Math.sqrt(boardSize);
        SudokuBoard gameBoard = new SudokuBoard(boardSize);
        for (int rowIndex = 0; rowIndex < gameBoard.getBoardSize(); rowIndex++) {
            for (int colIndex = 0; colIndex < gameBoard.getBoardSize(); colIndex++) {
                try {
                    int cellValue;
                    SudokuServiceImpl sudokuServiceImpl;
                    do {
                        sudokuServiceImpl = new SudokuServiceImpl();
                        cellValue = sudokuServiceImpl.generateRandomly(boardSize);
                        gameBoard.setCellValue(rowIndex, colIndex, cellValue);
                    } while (!isSafe(gameBoard, rowIndex, colIndex, cellValue) && (cellValue < 0 || 8 < cellValue));
                    System.out.print(cellValue + " ");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("");
        }
        SudokuBoard solution = makeSolution(gameBoard, boardSize);
        Player player = new Player(nickname);
        Game game;
        try {
            game = new Game(level, level.getMaxEmptyCells(), gameBoard, new SudokuServiceImpl(), solution, 0, player);
            try {
                Registry r = LocateRegistry.getRegistry("localhost", 1099);
                System.out.println("registry created");
                try {
                    SudokuService server = (SudokuService) r.lookup("SudokuGame");
                    System.out.println("server inited");
                    game.setGameBoard(gameBoard);
                    System.out.println("gameBoard set");
                    game.setSolution(solution);
                    System.out.println("solution set");
                    /*server.setGame(game);
                    System.out.println("game set");
                    server.setPlayer(player);
                    System.out.println("player set");*/
                    server.fillValues();
                } catch (NotBoundException | AccessException ex) {
                    logger.log(Level.SEVERE, ex.getMessage());
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(null, "Error in connecting with the server");
                Platform.exit();
            }

            return game.getGameBoard();
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null, "Error with initializing a new game");
            Platform.exit();
            return null;
        }
    }

    @Override
    public SudokuBoard makeSolution(SudokuBoard board, int boardSize) {
        SudokuBoard solved = new SudokuBoard(boardSize);
        if (canSolve(board, boardSize)) {
            for (int rowIndex = 0; rowIndex < boardSize; rowIndex++) {
                for (int colIndex = 0; colIndex < boardSize; colIndex++) {
                    solved.setCellValue(rowIndex, colIndex, board.getCellValue(rowIndex, colIndex));
                }
            }
        }
        return solved;
    }

    @Override
    public void updateGridBoard(SudokuBoard updatedGridBoard, int value, int rowIndex, int colIndex) {
        if (isSafe(updatedGridBoard, rowIndex, colIndex, value)) {
            updatedGridBoard.setCellValue(rowIndex, colIndex, value);
        } else {
            updatedGridBoard.setCellValue(rowIndex, colIndex, 0);
        }
    }

    @Override
    public void showMessage(String title, String message, Player player, Game game, String totalMinutes) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        logClientGameOutcome(player.getNickname(), game.getLevel(), game.getCurrentScore(), game.isWon(), totalMinutes);
    }

    @Override
    public boolean canSolve(SudokuBoard board, int boardSize) {
        int rowIndex = -1;
        int colIndex = -1;
        boolean isEmptyCell = true;
        for (int rowI = 0; rowI < boardSize; rowI++) {
            for (int colI = 0; colI < boardSize; colI++) {
                if (board.getCellValue(rowI, colI) == 0) {
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
        for (int currentCellValue = 1; currentCellValue <= boardSize; currentCellValue++) {
            if (isSafe(board, rowIndex, colIndex, currentCellValue)) {
                board.setCellValue(rowIndex, colIndex, currentCellValue);
                if (canSolve(board, boardSize)) {
                    return true;
                } else {
                    board.setCellValue(rowIndex, colIndex, 0);
                }
            }
        }
        return false;
    }

    private void logClientGameOutcome(String nickname, SudokuLevel level, int totalScore, boolean isWon,
                                      String totalMinutes) {
        String fileName = nickname + "_sudoku_outcome.txt";
        LocalDate currentDate = LocalDate.now();

        try {
            FileWriter writer = new FileWriter(fileName);
            writer.append(String.format("""
                            \t%s
                            \tNickname: %s, SudokuLevel: %s, TotalScore: %d,
                            \tIsWon: %s, Minutes: %s
                            """, currentDate, nickname,
                    level, totalScore, isWon, totalMinutes));
            writer.close();
        } catch (IOException ioEx) {
            logger.log(Level.SEVERE, ioEx.getMessage());
        }
    }
}