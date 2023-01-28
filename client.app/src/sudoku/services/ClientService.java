package sudoku.services;
import server.entities.Game;
import server.entities.Player;
import server.entities.SudokuLevel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    int[][] initGame(SudokuLevel level, String nickname) throws RemoteException;

    int[][] makeSolution(SudokuLevel level, int[][] board) throws RemoteException;

    void updateGridBoard(int[][] updatedGridBoard) throws RemoteException;

    void showMessage(String title, String message, Player player, Game game, int toatalMinutes)throws RemoteException;
}