package server.servicesImpls;

import server.entities.Game;
import server.entities.Player;
import server.entities.SudokuBoard;
import server.services.SudokuService;

import java.net.StandardSocketOptions;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.Random;

public class SudokuServiceImpl extends UnicastRemoteObject implements SudokuService {
    private Game game;
    private Player player;

    public SudokuServiceImpl() throws RemoteException {
        setPlayer(new Player());
        setGame(new Game());
    }

    @Override
    public void fillValues() throws RemoteException {
        fillDiagonal();
        for (int rowIndex = 0; rowIndex < game.getRowsCols(); rowIndex++) {
            for (int colIndex = 0; colIndex < game.getRowsCols(); colIndex++) {
                fillRemaining(rowIndex, colIndex, game);
            }
        }
        removeKDigits();
    }

    @Override
    public void fillDiagonal() throws RemoteException {
        // TODO: to be implemented...
        for (int index = 0; index < game.getSqrtOfRowsCols();
             index = index + game.getSqrtOfRowsCols()) {
            fillBox(index, index);
        }
    }

    @Override
    public boolean fillRemaining(int rowI, int colI, Game game) throws RemoteException {
        int numberOfRowsCols = game.getSqrtOfRowsCols(), sqrtOfNumberOfRowsCols = game.getSqrtOfRowsCols();
        if (colI >= numberOfRowsCols && rowI < numberOfRowsCols - 1) {
            rowI += 1;
            colI = 0;
        }
        if (rowI >= numberOfRowsCols && colI >= numberOfRowsCols) {
            return true;
        }
        if (rowI < sqrtOfNumberOfRowsCols) {
            if (colI < sqrtOfNumberOfRowsCols) {
                colI = sqrtOfNumberOfRowsCols;
            }
        } else if (rowI < numberOfRowsCols - sqrtOfNumberOfRowsCols) {
            if (colI == (rowI / sqrtOfNumberOfRowsCols) * sqrtOfNumberOfRowsCols) {
                colI += sqrtOfNumberOfRowsCols;
            }
        } else {
            if (colI == numberOfRowsCols - sqrtOfNumberOfRowsCols) {
                rowI += 1;
                colI = 0;
                if (rowI >= numberOfRowsCols) {
                    return true;
                }
            }
        }
        for (int cellValue = 1; cellValue <= numberOfRowsCols; ++cellValue) {
            SudokuBoard.SudokuGrid[][] currentGrid = game.getGameBoard().getBoard();
            if (game.isSafePosition(currentGrid, rowI, colI, cellValue)) {
                currentGrid[rowI][colI].getGrid()[rowI][colI] = cellValue;
                if (fillRemaining(rowI, colI + 1, game)) {
                    return true;
                }
                currentGrid[rowI][colI].getGrid()[rowI][colI] = 0;
            }
        }
        return false;
    }

    @Override
    public void fillBox(int rowI, int colI) throws RemoteException {
        int number;
        for (int rowIndex = 0; rowIndex < game.getGameBoard().getBoardSize(); ++rowIndex) {
            for (int colIndex = 0; colIndex < game.getGameBoard().getBoardSize(); ++colIndex) {
                do {
                    number = generateRandomly(game.getSqrtOfRowsCols());
                } while (!game.isUnusedInRow(rowIndex, number)
                        || !game.isUnusedInCol(colIndex, number)
                        || !game.isUnusedInBox(game.getGameBoard().getBoard(), number));
                game.getGameBoard().setCellValue(rowIndex, colIndex, number);
            }
        }
    }

    @Override
    public int generateRandomly(int number) throws RemoteException {
        Random rand = new Random();
        return rand.nextInt(number);
    }

    @Override
    public void removeKDigits() throws RemoteException {
        int counter = game.getEmptyCells();
        while (counter != 0) {
            int cellId = generateRandomly(game.getRowsCols()
                    * game.getRowsCols()) - 1;

            int rowIndex = cellId / game.getRowsCols();
            int colIndex = cellId % game.getRowsCols();
            if (colIndex != 0)
                colIndex = colIndex - 1;

            if (game.getGameBoard().getBoard()[rowIndex][colIndex].getGrid()[rowIndex][colIndex] != 0) {
                counter--;
                game.getGameBoard().getBoard()[rowIndex][colIndex].getGrid()[rowIndex][colIndex] = 0;
            }
        }
    }

    @Override
    public void setGame(Game game) throws RemoteException {
        this.game = Objects.requireNonNullElseGet(game, Game::new);
        //System.out.println("here in game setter");
    }

    @Override
    public void setPlayer(Player player) throws RemoteException {
        this.player = Objects.requireNonNullElseGet(player, Player::new);
    }

    @Override
    public Game getGame() throws RemoteException {
        return new Game(game);
    }

    @Override
    public Player getPlayer() throws RemoteException {
        return new Player(player);
    }
}