package ControlCenter;

import java.io.IOException;
import java.net.Socket;

/**
 * 
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class CCMain {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        Integer ccPort = 1234;
        Integer fiPort = 1235;
        CCController ccController = null;
        CCServer ccServer = new CCServer(ccPort);
        
        try {
            ccController = new CCController(new Socket(host, fiPort));
        } catch (IOException e) {
            System.err.println("ERROR: Unable to connect to the FI server!");
            System.exit(1);
        }
        
        if (!ccServer.start())
            System.exit(1);
        
        do {
            ccServer.newConnection(ccController);
        } while(ccController.continueSimulation());
        
        if (!ccServer.close())
            System.exit(1);
    }
}
