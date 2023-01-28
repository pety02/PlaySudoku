package sudoku.servicesImpls;
import server.entities.Game;
import server.entities.Player;
import server.entities.SudokuLevel;
import server.services.SudokuService;
import server.servicesImpls.SudokuServiceImpl;
import sudoku.PlayerApp;
import sudoku.services.ClientService;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientServiceImpl implements ClientService {

    @Override
    public int[][] initGame(SudokuLevel level, String nickname) throws RemoteException {

        Player player = new Player(nickname);
        Game game = new Game();
        try {
            Registry r = LocateRegistry.getRegistry ("localhost",1099);
            SudokuService server = null;
            try {
                // TODO: да проверя дали правилно се вика server-а и ако не защо
                server = (SudokuService) r.lookup("SudokuGame");
                SudokuService obj = new SudokuServiceImpl(player, game);
                obj.fillValues();
            } catch (NotBoundException | AccessException ex) {
                Logger.getLogger(PlayerApp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

        return game.getBoard();
    }

    @Override
    public int[][] makeSolution(SudokuLevel level, int[][] board) throws RemoteException {
        // TODO: алгоритъм за решване на судоку на базат на back-tracking
        return new int[0][];
    }

    @Override
    public void updateGridBoard(int[][] updatedGridBoard) throws RemoteException {
        /* TODO: проверяваме дали дъската в текущото си състояние отговаря на всички
            изисквания за валиден ход и ако да връщаме дъската, ако ли не връщаме custom грешка,
            прихващаме я в контролера и индикираме грешното състояние с червен фон зад цифрата
            като даваме възможност за корекция, преповтаряйки целия процес, докато не въведем
            валидна стойност.
        */
    }

    @Override
    public void showMessage(String title, String message, Player player, Game game, int toatalMinutes) throws RemoteException {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        try {
            logClientGameOutcome(player.getNickname(), game.getLevel(), game.getCurrentScore(), game.isWon(), toatalMinutes);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void logClientGameOutcome(String nickname, SudokuLevel level, int totalScore, boolean isWon,
                                     int totalMinutes) throws IOException {
        String fileName = nickname + "_sudoku_outcome.txt";
        LocalDate currentDate = LocalDate.now();

        FileWriter logger = new FileWriter(fileName);
        logger.write(String.format("""
                        \t%s
                        \tNickname: %s, SudokuLevel: %s, TotalScore: %d,
                        \tIsWon: %s, Minutes: %d
                        """, currentDate, nickname,
                level,totalScore, isWon, totalMinutes));
        logger.close();
    }
}