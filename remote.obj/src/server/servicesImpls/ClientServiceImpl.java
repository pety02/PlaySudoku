package server.servicesImpls;
import server.services.ClientService;
import server.entities.SudokuLevel;

import java.rmi.RemoteException;

public class ClientServiceImpl implements ClientService {

    @Override
    public int[][] initGame(SudokuLevel level) throws RemoteException {

        return new int[0][];
    }

    @Override
    public int[][] makeSolution(SudokuLevel level, int[][] board) throws RemoteException {
        return new int[0][];
    }

    @Override
    public void updateGridBoard(int[][] updatedGridBoard) throws RemoteException {

    }

    @Override
    public void showErrorMessage(String message) throws RemoteException {

    }

    @Override
    public void showFinishMessage(String message) throws RemoteException {

    }
}