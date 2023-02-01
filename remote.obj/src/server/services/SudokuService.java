package server.services;

import server.entities.Game;
import server.entities.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Интерфейсен клас, който наследява тоест разширява интерфейса Remote.
 */
public interface SudokuService extends Remote {
    /**
     * Попълва клетките на судоку пъзела, които не са по
     * главния диагонал със случайни валидни стойности.
     * @throws RemoteException
     */
    void fillValues() throws RemoteException;

    /**
     * Попълва главния диагонал на судоко пъзела със стойности.
     * @throws RemoteException
     */
    void fillDiagonal() throws RemoteException;

    /**
     * Попълва определна кутия (част) от судоку пъзела с валидни стойности.
     * @param rowI - индекс на ред в дадена кутия (част) от судоку пъзела.
     * @param colI - индекс на колона в дадена кутия (част) от судоку пъзела.
     * @throws RemoteException
     */
    void fillBox(int rowI,int colI) throws RemoteException;

    /**
     * Генерира случайни валидни стойности.
     * @param number - число, по което ще генерираме случайна стойност за дадена клетка от судоку пъзела.
     * @return Случайно генерирано число.
     * @throws RemoteException
     */
    int randomGenerator(int number) throws RemoteException;

    /**
     * Премахва тоест скрива част от цифрите, за да се получи нерешен судоку
     * пъзел, който да бъде предоставен за решение.
     * @throws RemoteException
     */
    void removeKDigits() throws RemoteException;

    void setGame(Game game) throws RemoteException;

    void setPlayer(Player player) throws RemoteException;

    Game getGame()throws RemoteException;

    Player getPlayer()throws RemoteException;
}