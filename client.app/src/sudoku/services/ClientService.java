package sudoku.services;

import server.entities.Game;
import server.entities.Player;
import server.entities.SudokuBoard;
import server.entities.SudokuLevel;

import java.rmi.RemoteException;

public interface ClientService {

    boolean isSafe(SudokuBoard board,
                   int rowI, int colI,
                   int cellValue);

    SudokuBoard initGame(SudokuLevel level, int boardSize, String nickname) throws RemoteException;

    SudokuBoard makeSolution(SudokuBoard board, int boardSize) throws RemoteException;

    void updateGridBoard(SudokuBoard updatedGridBoard, int value, int rowIndex, int colIndex) throws RemoteException;

    void showMessage(String title, String message, Player player, Game game, String totalMinutes) throws RemoteException;

    boolean canSolve(SudokuBoard board, int boardSize);
}