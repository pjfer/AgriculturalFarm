
package FarmInfrastructure.Com;

import Communication.HarvestState;
import Communication.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * Class that encapsulates the communication to the Control Center from the 
 * Farm infrastructure.
 * 
 * @author Rafael Teixeira
 */
public class CcStub {
    
    /**
     * Represents the Communication Socket to the Control Center.
     */
    private Socket clientSocket;
    
    /**
     * Message received as the answer to the request made.
     */
    private Message msgIn;
    
    /**
     * Message sent with the request we want.
     */
    private Message msgOut;
    
    /**
     * Stream of the received answer.
     */
    private ObjectInputStream in;
    
    /**
     * Stream of the sent request.
     */
    private ObjectOutputStream out;
    
    /**
     * Control Center Server IP.
     */
    private final String host;
    
    /**
     * Control Center Server port.
     */
    private final Integer ccPort;
    
    /**
     * Default Constructor
     * 
     * @param host Control Center Server IP.
     * @param ccPort Control Center Server port.
     */
    public CcStub(String host, Integer ccPort){
        this.host = host;
        this.ccPort = ccPort;
        
        
    }
    
    /**
     * Method called to send the WaitToStart Signal.
     * Used when all farmers are awaiting in the StoreHouse.
     */
    public void farmersReady(){
        
        String msgBody = "All Farmers are waiting in the Store House";
        msgOut = new Message(msgBody, HarvestState.WaitToStart);
        this.sendMessage(msgOut);
 
    }
    
    /**
     * Method called to send the WaitToWalk Signal.
     * Used when all farmers are awaiting in the Standing Area.
     */
    public void farmersPrepared(){
        
        String msgBody = "All Farmers are waiting in the Standing Area";
        msgOut = new Message(msgBody, HarvestState.WaitToWalk);
        this.sendMessage(msgOut);
        
    }
    
    /**
     * Method called to send the WaitToCollect Signal.
     * Used when all farmers are awaiting to collect cobs in the Granary.
     */
    public void farmersWCollect(){
        
        String msgBody = "All Farmers are waiting in the Granary";
        msgOut = new Message(msgBody, HarvestState.WaitToCollect);
        this.sendMessage(msgOut);
    
    }
    
    /**
     * Method called to send the WaitToReturn Signal.
     * Used when all farmers are awaiting in the Granary to come back.
     */
    public void farmersWProceed(){
        
        String msgBody = "All Farmers are waiting to Return";
        msgOut = new Message(msgBody, HarvestState.WaitToReturn);
        this.sendMessage(msgOut);

    }

    /**
     * Method called to send the Update Signal.
     * Used when a farmer does some action.
     * 
     * @param update Action done by the farmer.
     */
    public void update(String update){
        
        msgOut = new Message(update, HarvestState.Update);
        this.sendMessage(msgOut);
    }
    
    /**
     * Method called to send the FarmerTerminated Signal.
     * Used when a farmer terminates his execution.
     * 
     * @param  farmerId Farmer ID.
     */
    public void farmerTerminated(int farmerId){
        
        msgOut = new Message("Farmer: " + farmerId + "has terminated.",
                HarvestState.FarmerTerminated);
        this.sendMessage(msgOut);
        
    }
    
    /**
     * Private Method that handles the delivery of the message and its answer.
     */
    private void sendMessage(Message msgOut){
        try {
            clientSocket = new Socket(host, ccPort);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(msgOut);
            out.flush();
            
            in = new ObjectInputStream(clientSocket.getInputStream());
            msgIn = (Message) in.readObject();
            
            if(msgIn.getType() == HarvestState.Error){
                System.err.println(msgIn.getBody());
                System.exit(1);
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("ERROR: Unable to send the message to the "
                    + "CC server");
            System.exit(1);
        }
        
        try {
            //Close the communication channels.
            in.close();
            out.close();
            clientSocket.close();
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to close the connection of " +
                    clientSocket);
            System.exit(1);
        }
    }
}
