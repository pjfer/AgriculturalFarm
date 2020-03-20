package ControlCenter;

import Communication.HarvestState;
import Communication.Message;

/**
 * 
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class CCMain {
    public static void main(String[] args) {
        HarvestState hs;
        Message msgReceived;
        String host = "127.0.0.1";
        Integer ccPort = 1234;
        Integer fiPort = 1235;
        CCController ccController = new CCController();
        ccController.setupFICom(host, fiPort);
        CCServer ccServer = new CCServer(ccPort);
        
        if (!ccServer.start())
            System.exit(1);
        
        do {
            msgReceived = ccServer.readMessage();
            hs = msgReceived.getType();
            
            switch (hs) {
                case Prepare:
                    ccController.prepComplete();
                    break;
                case Collect:
                    ccController.readyToCollect();
                    break;
                case Return:
                    ccController.readyToReturn();
                    break;
                case Stop:
                    ccController.fiStopped();
                    break;
                case Exit:
                    ccController.fiExited();
                    break;
            }
        } while(hs != HarvestState.Exit && hs != null);
        
        if (!ccServer.close())
            System.exit(1);
    }
}
