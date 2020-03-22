package ControlCenter.Thread;

import Communication.Message;
import Communication.HarvestState;
import Communication.ServerCom;
import ControlCenter.CCController;


/**
 * Class to handle the requests from clients in a thread based system.
 */
public class TFICom extends Thread {
    /**
     * Message received from the farm infrastructure server.
     */
    private Message msgIn;
    
    /**
     * Message sent to the farm infrastructure server.
     */
    private final Message msgOut;
    
    /**
     * Communication socket that received the request.
     */
    private final ServerCom sconi;
    
    /**
     * Communication manager with the control center GUI.
     */
    private final CCController ccController;
    
    /**
     * Represents the state of farm infrastructure simulation.
     */
    private HarvestState hvState;
    
    /**
     * Instantiation of communication channel with the farm infrastructure 
     * server and of control center controller.
     * 
     * @param sconi server's communication socket.
     * @param ccController communication manager with the control center GUI.
     */
    public TFICom(ServerCom sconi, CCController ccController) {
        this.sconi = sconi;
        this.ccController = ccController;
        this.msgOut = new Message("200 OK", HarvestState.Ok);
    }
    
    /**
     * Verifies the farm insfrastructure's state received and executes the 
     * associated task to be done in the control center GUI. In the end, 
     * responds to the client with an OK message if the state was recognized, 
     * or with an ERROR message otherwise, and closes the socket.
     */
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
                msgOut.setBody("400 Bad Request");
                msgOut.setType(HarvestState.Error);
        }
        
        sconi.writeObject(msgOut);
        sconi.close();
    }
}
