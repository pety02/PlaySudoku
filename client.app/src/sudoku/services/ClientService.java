package sudoku.services;

import server.entities.Game;
import server.entities.Player;
import server.entities.SudokuLevel;

import java.rmi.RemoteException;

/**
 * Интерфейс, който наследява тоест разширява Remote интерфейса.
 */
public interface ClientService {

    /**
     * Проверява дали дадена позиция е безопасна.
     *
     * @param board     - дъска.
     * @param rowI      - индекс на ред.
     * @param colI      - индекс на колона.
     * @param cellValue - стойност.
     * @return true - при безопасна,false - при небезопасна
     */
    boolean isSafe(int[][] board,
                   int rowI, int colI,
                   int cellValue);

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

    /**
     * Проверява дали може да се реши судокуто.
     *
     * @param board     - дъската на судокуто.
     * @param boarsSize - размера на дъската.
     * @return True - при възможност за решване,False - при невъзможност.
     */
    boolean canSolve(int[][] board, int boarsSize);
}