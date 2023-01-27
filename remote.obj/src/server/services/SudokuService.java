package server.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SudokuService extends Remote {
    void fillValues() throws RemoteException;
    void fillDiagonal() throws RemoteException;
    void fillBox(int row,int col) throws RemoteException;
    int randomGenerator(int num) throws RemoteException;
    void removeKDigits() throws RemoteException;
}