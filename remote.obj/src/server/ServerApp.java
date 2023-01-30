package server;

import server.services.SudokuService;
import server.servicesImpls.SudokuServiceImpl;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Стартова точка за сървъра.
 */
public class ServerApp {
    /**
     * Стартира сървърното приложение.
     * @param args
     */
    public static void main(String[] args) {
        String remoteObjName = "SudokuGame";
        Logger logger = Logger.getLogger(ServerApp.class.getName());

        SudokuService sudokuService;
        try {
            sudokuService = new SudokuServiceImpl();
            Registry registry = LocateRegistry.createRegistry( 1099);

            registry.rebind(remoteObjName, sudokuService);
            System.out.printf("Remote object named: %s is registered. Registry is running ...%n", remoteObjName);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }
}