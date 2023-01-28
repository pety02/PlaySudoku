package server.servicesImpls;

import server.entities.Game;
import server.entities.Player;
import server.services.SudokuService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Клас, който наследява тоест разширява UnicastRemoteObject класа и имплементира SudokuService интерфейса.
 */
public class SudokuServiceImpl extends UnicastRemoteObject implements SudokuService {
    private Game game;
    private Player player;

    /**
     * Конструктор с параметри.
     * @param player - играч.
     * @param game - игра.
     * @throws RemoteException
     */
    public SudokuServiceImpl(Player player, Game game) throws RemoteException {
        setPlayer(player);
        setGame(game);
    }

    /**
     * Конструктор по подразбиране
     * @throws RemoteException
     */
    public SudokuServiceImpl() throws RemoteException {
        setPlayer(new Player());
        setGame(new Game());
    }

    @Override
    public void fillValues() {
        fillDiagonal();
        game.getValidator().fillRemaining(0, game.getValidator().getSqrtOfNumberOfRowsCols());
        removeKDigits();
    }

    @Override
    public void fillDiagonal() {
        for (int index = 0; index < game.getValidator().getNumberOfRowsCols();
             index = index + game.getValidator().getSqrtOfNumberOfRowsCols()) {
            fillBox(index, index);
        }
    }

    @Override
    public void fillBox(int rowI,int colI) {
        int number;
        for (int rowIndex = 0; rowIndex < game.getValidator().getSqrtOfNumberOfRowsCols(); ++rowIndex) {
            for (int colIndex = 0; colIndex < game.getValidator().getSqrtOfNumberOfRowsCols(); ++colIndex) {
                do {
                    number = randomGenerator(game.getValidator().getNumberOfRowsCols());
                }
                while (!game.getValidator().unUsedInBox(rowI, colI, number));
                game.getBoard()[rowI + rowIndex][colI + colIndex] = number;
            }
        }
    }

    @Override
    public int randomGenerator(int number)
    {
        return (int) Math.floor((Math.random() * number + 1));
    }

    @Override
    public void removeKDigits() {
        int counter = game.getEmptyCells();
        while (counter != 0) {
            int cellId = randomGenerator(game.getValidator().getNumberOfRowsCols()
                    * game.getValidator().getNumberOfRowsCols()) - 1;

            int rowIndex = cellId / game.getValidator().getNumberOfRowsCols();
            int colIndex = cellId % 9;
            if (colIndex != 0)
                colIndex = colIndex - 1;

            if (game.getBoard()[rowIndex][colIndex] != 0) {
                counter--;
                game.getBoard()[rowIndex][colIndex] = 0;
            }
        }
    }

    /**
     * Сетър за полето игра (game).
     * @param game - игра.
     */
    public void setGame(Game game) {
        this.game = (game != null) ? game : new Game();
    }

    /**
     * Сетър за полето играч (player).
     * @param player - играч.
     */
    public void setPlayer(Player player) {
        this.player = (player != null) ? player : new Player();
    }

    /**
     * Гетър за полето игра (game).
     * @return стойността на game.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Гетър за полето играч (player).
     * @return стойността на player.
     */
    public Player getPlayer() {
        return player;
    }
}