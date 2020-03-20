
package FarmInfrastructure.Com;

import Communication.HarvestConfig;
import Communication.HarvestState;
import Communication.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class CcStub {
    
    private Socket clientSocket;
    private HarvestConfig hc;
    private Message msgIn;
    private Message msgOut;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final String host;
    private final Integer ccPort;
    
    public CcStub(String host, Integer ccPort){
        this.host = host;
        this.ccPort = ccPort;
        
    }
    
    public void farmersReady(){
        
        String msgBody = "All Farmers are waiting in the Store House";
        msgOut = new Message(msgBody, HarvestState.WaitToStart);
        
        try {
            out.writeObject(msgOut);
            out.flush();
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to send the message to the "
                    + "FI server");
            System.exit(1);
        }   
    }
    
    public void farmersPrepared(){
        String msgBody = "All Farmers are waiting in the Standing Area";
        msgOut = new Message(msgBody, HarvestState.WaitToWalk);
        
        try {
            out.writeObject(msgOut);
            out.flush();
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to send the message to the "
                    + "FI server");
            System.exit(1);
        }
    }
    
    public void farmersWCollect(){
        String msgBody = "All Farmers are waiting in the Granary";
        msgOut = new Message(msgBody, HarvestState.WaitToCollect);
        
        try {
            out.writeObject(msgOut);
            out.flush();
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to send the message to the "
                    + "FI server");
            System.exit(1);
        }
    
    }
    
    public void farmersWProceed(){
        String msgBody = "All Farmers are waiting to Return";
        msgOut = new Message(msgBody, HarvestState.WaitToReturn);
        
        try {
            out.writeObject(msgOut);
            out.flush();
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to send the message to the "
                    + "FI server");
            System.exit(1);
        }
    }

    public void update(String update){
        msgOut = new Message(update, HarvestState.Update);
        
        try {
            out.writeObject(msgOut);
            out.flush();
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to send the message to the "
                    + "FI server");
            System.exit(1);
        }
    }
    
    public void farmerTerminated(int farmerId){
        msgOut = new Message("Farmer: " + farmerId + "has terminated.",
                HarvestState.FarmerTerminated);
        
        try {
            out.writeObject(msgOut);
            out.flush();
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to send the message to the "
                    + "FI server");
            System.exit(1);
        }
    }
    
}
