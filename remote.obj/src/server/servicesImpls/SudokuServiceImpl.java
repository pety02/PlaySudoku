package server.servicesImpls;

import server.entities.Game;
import server.entities.Player;
import server.services.SudokuService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SudokuServiceImpl extends UnicastRemoteObject implements SudokuService {

    private Game game;
    private Player player;

    public SudokuServiceImpl(Player player, Game game) throws RemoteException{
        this.player = player;
        this.game = game;
    }

    public SudokuServiceImpl() throws RemoteException {
        this.player = new Player("guest");
        this.game = new Game(this.player.getNickname());
    }

    @Override
    public void fillValues()
    {
        fillDiagonal();
        game.getValidator().fillRemaining(0, game.getSqrtOfN());
        removeKDigits();
    }

    @Override
    public void fillDiagonal()
    {

        for (int i = 0; i<game.getN(); i=i+game.getSqrtOfN())

            fillBox(i, i);
    }

    @Override
    public void fillBox(int row,int col)
    {
        int num;
        for (int i=0; i<game.getSqrtOfN(); i++)
        {
            for (int j=0; j<game.getSqrtOfN(); j++)
            {
                do
                {
                    num = randomGenerator(game.getN());
                }
                while (!game.getValidator().unUsedInBox(row, col, num));

                game.getBoard()[row+i][col+j] = num;
            }
        }
    }

    @Override
    public int randomGenerator(int num)
    {
        return (int) Math.floor((Math.random()*num+1));
    }

    @Override
    public void removeKDigits()
    {
        int count = game.getEmptyCells();
        while (count != 0)
        {
            int cellId = randomGenerator(game.getN()*game.getN())-1;

            int i = (cellId/game.getN());
            int j = cellId%9;
            if (j != 0)
                j = j - 1;

            if (game.getBoard()[i][j] != 0)
            {
                count--;
                game.getBoard()[i][j] = 0;
            }
        }
    }
}