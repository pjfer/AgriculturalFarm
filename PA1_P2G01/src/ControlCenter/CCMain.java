package ControlCenter;

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
        CCController ccController = new CCController(host, fiPort);
        CCServer ccServer = new CCServer(ccPort);
        if (!ccServer.start())
            System.exit(1);
        
        do {
            ccServer.newConnection(ccController);
        } while(ccController.continueSimulation());
        
        if (!ccServer.close())
            System.exit(1);
    }
}
