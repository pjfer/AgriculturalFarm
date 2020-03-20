
package FarmInfrastructure.Com;

import Communication.HarvestConfig;
import Communication.HarvestState;
import Communication.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CcStub {
    
    private Socket clientSocket;
    private HarvestConfig hc;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final String host;
    private final Integer ccPort;
    
    public CcStub(String host, Integer ccPort){
        this.host = host;
        this.ccPort = ccPort;
        
    }
    
    public void farmersReady(){
        Message message;
        String body;
        
        try {
            clientSocket = new Socket(host, ccPort);
            in = new ObjectInputStream(clientSocket.getInputStream()); 
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            
            message = new Message(HarvestState.WaitToWalk);
            
            out.writeObject(message);
            out.flush();
            Message response = (Message) in.readObject();
            System.out.println("Server's message: " + response.getBody());
            
            in.close();
            out.close();
            clientSocket.close();
            
        } catch (IOException ex) {
            Logger.getLogger(CcStub.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CcStub.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public void farmersWCollect(){
    
    }
    
    public void farmersWProceed(){
    
    }





    
}
