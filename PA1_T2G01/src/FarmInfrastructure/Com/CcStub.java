
package FarmInfrastructure.Com;

import Communication.ClientCom;
import Communication.HarvestState;
import Communication.Message;


/**
 * Class that encapsulates the communication to the Control Center from the 
 * Farm infrastructure.
 */
public class CcStub {
    
    /**
     * Message received as the answer to the request made.
     */
    private Message msgIn;
    
    /**
     * Message sent with the request we want.
     */
    private Message msgOut;
    
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
        
        String msgBody = "All Farmers are waiting in the Store House\n";
        msgOut = new Message(msgBody, HarvestState.WaitToStart);
        this.sendMessage(msgOut);
 
    }
    
    /**
     * Method called to send the WaitToWalk Signal.
     * Used when all farmers are awaiting in the Standing Area.
     */
    public void farmersPrepared(){
        
        String msgBody = "All Farmers are waiting in the Standing Area\n";
        msgOut = new Message(msgBody, HarvestState.WaitToWalk);
        this.sendMessage(msgOut);
        
    }
    
    /**
     * Method called to send the WaitToCollect Signal.
     * Used when all farmers are awaiting to collect cobs in the Granary.
     */
    public void farmersWCollect(){
        
        String msgBody = "All Farmers are waiting in the Granary\n";
        msgOut = new Message(msgBody, HarvestState.WaitToCollect);
        this.sendMessage(msgOut);
    
    }
    
    /**
     * Method called to send the WaitToReturn Signal.
     * Used when all farmers are awaiting in the Granary to come back.
     */
    public void farmersWProceed(){
        
        String msgBody = "All Farmers are waiting to Return\n";
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
        
        msgOut = new Message("Farmer: " + farmerId + " has terminated.\n",
                HarvestState.FarmerTerminated);
        this.sendMessage(msgOut);
        
    }
    /**
     * Method called to indicate that all farmers are terminated.
     * 
     */
    public void allFarmersTerminated(){
        msgOut = new Message("All Farmers Terminated.\n",
                HarvestState.Exit);
        this.sendMessage(msgOut);
    }
    
    /**
     * Private Method that handles the delivery of the message and its answer.
     * 
     * @param msgOut Message t send to the Control Center.
     */
    private void sendMessage(Message msgOut) {
        ClientCom ccon = new ClientCom(host, ccPort);
        
        while (!ccon.open()) {
            try {
                Thread.currentThread().sleep((long) 10);
            }
            catch (InterruptedException e) { }
        }
        
        ccon.writeObject(msgOut);
        
        msgIn = (Message) ccon.readObject();
        
        
        if(msgIn.getType() == HarvestState.Error){
            System.err.println(msgIn.getBody());
            System.exit(1);
        }
        ccon.close();
        
    }
}
