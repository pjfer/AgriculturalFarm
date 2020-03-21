package FarmInfrastructure.Thread;

import Communication.HarvestState;
import Communication.Message;
import Communication.ServerCom;
import FarmInfrastructure.FIController;

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
     * Communication socket that received the request.
     */
    private final ServerCom sconi;
    
    /**
     * Farm Interface Controller that executes the request.
     */
    private final FIController fiController;
    
    /**
     * HarvestState received in the request message.
     */
    private HarvestState hvState;
    
    /**
     * Standard Constructor of the request handle thread.
     * 
     * @param sconi
     * @param fiController
     */
    public TCCCom(ServerCom sconi, FIController fiController) {
        this.sconi = sconi;
        this.fiController = fiController;
        
        /* Default Response Message*/
        this.msgOut = new Message("200 Good Request", HarvestState.Ok);
    }
    
    
    @Override
    /**
     * Standard Run method of a thread, executes the request handling.
     */
    public void run() {

        /* Obtain the request message. */
        msgIn = (Message) sconi.readObject();
        hvState = msgIn.getType();

        switch(hvState){

            case Prepare:
                System.out.println(msgIn.getNumFarmers() + " " + msgIn.getTimeoutPath() + " " + msgIn.getNumMaxSteps() + " " +  msgIn.getNumCornCobs());
                /* Verification fo the validity of the request fields. */
                if( msgIn.getNumFarmers() < 1 || msgIn.getNumFarmers() > 5
                        || msgIn.getTimeoutPath() <= 0 
                        || msgIn.getNumMaxSteps() < 1 
                        || msgIn.getNumMaxSteps() >  2
                        || msgIn.getNumCornCobs() < 1){

                    this.msgOut.setBody("400 Invalid Preparation Values.");
                    this.msgOut.setType(HarvestState.Error);

                }
                else{
                    fiController.prepareFarm(msgIn.getNumFarmers(), 
                            msgIn.getTimeoutPath(), msgIn.getNumMaxSteps(),
                            msgIn.getNumCornCobs());
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

        /* Send the response message. */
        sconi.writeObject(msgOut);
        
        /* Close the communitaction channel. */
        sconi.close();
        
    }
    
}
