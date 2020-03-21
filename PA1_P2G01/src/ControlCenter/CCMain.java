package ControlCenter;

import Communication.ServerCom;
import ControlCenter.Thread.TFICom;
import FarmInfrastructure.FIMain;

import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class CCMain {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        Integer ccPort = 1200;
        Integer fiPort = 1300;
        CCController ccController = new CCController(host, fiPort);
        TFICom handler;
        /**
         * Communication Channel of the server.
         */
        ServerCom scon, sconi;
        scon = new ServerCom (ccPort);                            // criação do canal de escuta e sua associação
        scon.start (); 
        
        do {
            try {
                sconi = scon.accept();
                handler = new TFICom(sconi, ccController);              // lançamento do agente prestador do serviço
                handler.start ();                               // entrada em processo de escuta
            } catch (SocketTimeoutException ex) {}
        } while(ccController.continueSimulation());
        
        scon.end();
    }
}
