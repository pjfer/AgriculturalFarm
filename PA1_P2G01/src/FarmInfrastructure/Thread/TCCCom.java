package FarmInfrastructure.Thread;

import Communication.HarvestState;
import Communication.Message;
import FarmInfrastructure.FIController;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * TCCCom is a thread built to handle 
 * the Farm Interface server requests received.
 *
 * @author Rafael Teixeira & Pedro Ferreira
 */
public class TCCCom extends Thread {
    /**
     * Message thats received as a request.
     */
    private Message msgIn;
    
    /**
     * Message that will be sent as a response to the request.
     */
    private final Message msgOut;
    
    /**
     * Body of the response message.
     */
    private String msgBody;
    
    /**
     * Communication socket that received the request.
     */
    private final Socket clientSocket;
    
    /**
     * Farm Interface Controller that executes the request.
     */
    private final FIController fiController;
    
    /**
     * HarvestState received in the request message.
     */
    private HarvestState hvState;
    
    /**
     * Communication stream from were the message request is received.
     */
    private ObjectInputStream in;
    
    /**
     * Communication stream used to send the response message.
     */
    private ObjectOutputStream out;
    
    /**
     * Standard Constructor of the request handle thread.
     * 
     * @param clientSocket Communication socket that received the request.
     * @param fiController Farm Interface Controller that executes the request.
     * @param in
     * @param out
     */
    public TCCCom(Socket clientSocket, FIController fiController, 
            ObjectInputStream in, ObjectOutputStream out) {
        this.clientSocket = clientSocket;
        this.fiController = fiController;
        
        System.out.println("BANANAMORE");
        
        //Default Response Message
        this.msgOut = new Message("200 Good Request", HarvestState.Ok);
        
        this.in = in;
        
        System.out.println("BANANAMORE");
        
        this.out = out;
        
        System.out.println("BANANAMORE");
    }
    
    
    @Override
    /**
     * Standard Run method of a thread, executes the request handling.
     */
    public void run() {
        try {
            // Obtain the request message.
            msgIn = (Message) in.readObject();
            hvState = msgIn.getType();
            
            switch(hvState){
                
                case Prepare:
                    //Verification fo the validity of the request fields
                    if( msgIn.getNumFarmers() < 1 || msgIn.getNumFarmers() > 5
                            || msgIn.getTimeoutPath() <= 0 
                            || msgIn.getNumMaxSteps() < 1 
                            || msgIn.getNumMaxSteps() >  2){
                        
                        this.msgOut.setBody("400 Invalid Preparation Values.");
                        this.msgOut.setType(HarvestState.Error);
                        
                    }
                    else{
                        fiController.prepareFarm(msgIn.getNumFarmers(), 
                                msgIn.getTimeoutPath(), msgIn.getNumMaxSteps());
                    }
                    break;
                case Start:
                    fiController.startMove();
                    break;
                case Collect:
                    fiController.startCollection();
                    break;
                case Return:
                    fiController.returnWCorn();
                    break;
                case Stop:
                    fiController.stopHarvest();
                    break;
                case Exit:
                    fiController.exitSimulation();
                    break;
                default:
                    this.msgOut.setBody("400 Bad Request.");
                    this.msgOut.setType(HarvestState.Error);
            }
            
            //Send the response message.
            out.writeObject(msgOut);
            out.flush();
        }
        catch(IOException | ClassNotFoundException e) {
            System.err.println("ERROR: Unable to read the msgOut from client "
                    + "socket on port " + clientSocket.getPort());
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
