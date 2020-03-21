package ControlCenter.Thread;

import Communication.Message;
import Communication.HarvestState;
import Communication.ServerCom;
import ControlCenter.CCController;
import java.io.IOException;


/**
 * Class to handle the requests from clients in a thread based system.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class TFICom extends Thread {
    private Message msgIn;
    private Message msgOut;
    private String msgBody;
    
    /**
     * Communication socket that received the request.
     */
    private final ServerCom sconi;
    private CCController ccController;
    private HarvestState hvState;
    
    
    public TFICom(ServerCom sconi, CCController ccController) {
        this.sconi = sconi;
        this.ccController = ccController;
        this.msgBody = "200 OK";
    }
    
    @Override
    public void run() {
        msgIn = (Message) sconi.readObject();
        hvState = msgIn.getType();

        switch (hvState) {
            case WaitToStart:
                ccController.readyToPrep();
                break;
            case WaitToWalk:
                ccController.updateGUITextArea(msgIn.getBody());
                ccController.prepComplete();
                break;
            case Update:
                ccController.updateGUITextArea(msgIn.getBody());
                break;
            case WaitToCollect:
                ccController.updateGUITextArea(msgIn.getBody());
                ccController.readyToCollect();
                break;
            case WaitToReturn:
                ccController.updateGUITextArea(msgIn.getBody());
                ccController.readyToReturn();
                break;
            case FarmerTerminated:
                ccController.updateGUITextArea(msgIn.getBody());
                break;
            case Stop:
                ccController.updateGUITextArea(msgIn.getBody());
                ccController.fiStopped();
                break;
            case Exit:
                ccController.updateGUITextArea(msgIn.getBody());
                ccController.fiExited();
                break;
            default:
                System.err.println("ERROR: Unable to recognize the given"
                        + "harvest state!");
                System.exit(1);
        }

        msgOut = new Message(msgBody, hvState);
        sconi.writeObject(msgOut);
        
        sconi.close();
        
    }
}
