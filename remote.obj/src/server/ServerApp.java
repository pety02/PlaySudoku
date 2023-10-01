package server;

import server.services.SudokuService;
import server.servicesImpls.SudokuServiceImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerApp {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(ServerApp.class.getName());

        try {
            String remoteObjName = "SudokuGame";
            SudokuService sudokuService = new SudokuServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind(remoteObjName, sudokuService);
            System.out.printf("Remote object named: %s is registered. Registry is running ...%n", remoteObjName);
        } catch (RemoteException remoteEx) {
            logger.log(Level.SEVERE, LocalDate.now() + remoteEx.getMessage());
        }
    }
}