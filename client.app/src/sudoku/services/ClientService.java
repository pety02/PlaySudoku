package sudoku.services;

import server.entities.Game;
import server.entities.Player;
import server.entities.SudokuLevel;

import java.rmi.RemoteException;

/**
 * Интерфейс, който наследява тоест разширява Remote интерфейса.
 */
public interface ClientService /*extends Remote*/ {
    /**
     * Инициализира дъската за игра.
     *
     * @param level    - ниво на трудност.
     * @param nickname - никнейм.
     * @return инициализирана дъска за игра.
     * @throws RemoteException
     */
    int[][] initGame(SudokuLevel level, String nickname) throws RemoteException;

    /**
     * Решава частично судокото чрез backtracking технология.
     *
     * @param board - дъска за игра.
     * @param N     - брой редове/колони.
     * @return Връща решено судоку.
     * @throws RemoteException
     */
    int[][] makeSolution(int[][] board, int N) throws RemoteException;

    /**
     * Ъпдейтва дъската за игра.
     *
     * @param updatedGridBoard - ъпдейтната дъска за игра.
     * @throws RemoteException
     */
    boolean updateGridBoard(int[][] updatedGridBoard, int value, int rowIndex, int colIndex) throws RemoteException;

    /**
     * Показва съобщение на потреббителя за различни неща (победа/загуба).
     *
     * @param title        - заглавие.
     * @param message      - съобщение.
     * @param player       - играч.
     * @param game         - игра.
     * @param totalMinutes - минути, за които е решено судокуто.
     * @throws RemoteException
     */
    void showMessage(String title, String message, Player player, Game game, String totalMinutes) throws RemoteException;

}