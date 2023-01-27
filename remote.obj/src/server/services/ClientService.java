package server.services;
import server.entities.SudokuLevel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    int[][] initGame(SudokuLevel level) throws RemoteException;

    int[][] makeSolution(SudokuLevel level, int[][] board) throws RemoteException;

    void updateGridBoard(int[][] updatedGridBoard) throws RemoteException;

    void showErrorMessage(String message)throws RemoteException;

    void showFinishMessage(String message)throws RemoteException;
}