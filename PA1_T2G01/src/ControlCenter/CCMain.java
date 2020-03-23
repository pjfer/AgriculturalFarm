package ControlCenter;

import Communication.ServerCom;
import ControlCenter.Thread.TFICom;
import java.net.SocketTimeoutException;

/**
 * Main class responsible for the instantiation and initialization of the 
 * control center server and the threads created to respond to client's messages,
 * as well as the control center controller.
 */
public class CCMain {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        Integer ccPort = 1200;
        Integer fiPort = 1300;
        CCController ccController = new CCController(host, fiPort);
        TFICom handler;
        ServerCom scon, sconi;
        scon = new ServerCom(ccPort);
        
        scon.start();
        
        do {
            try {
                sconi = scon.accept();
                handler = new TFICom(sconi, ccController);
                handler.start();
            } catch (SocketTimeoutException ex) {}
        } while(ccController.continueSimulation());
        
        scon.end();
        
        System.out.println("End of Simulation");
    }
}
