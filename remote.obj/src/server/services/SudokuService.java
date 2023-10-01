package server.services;

import server.entities.Game;
import server.entities.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SudokuService extends Remote {

    void fillValues() throws RemoteException;

    void fillDiagonal() throws RemoteException;

    boolean fillRemaining(int rowI, int colI, Game game) throws RemoteException;

    void fillBox(int rowI, int colI) throws RemoteException;

    int generateRandomly(int number) throws RemoteException;

    void removeKDigits() throws RemoteException;

    void setGame(Game game) throws RemoteException;

    void setPlayer(Player player) throws RemoteException;

    Game getGame() throws RemoteException;

    Player getPlayer() throws RemoteException;
}