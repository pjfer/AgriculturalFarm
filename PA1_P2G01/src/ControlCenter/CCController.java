package ControlCenter;

import Communication.ClientCom;
import Communication.HarvestState;
import Communication.Message;
import ControlCenter.GraphicalInterface.ControlCenterGUI;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class 
 *
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class CCController {
    private boolean continueSimulation;
    private Message msgIn;
    private Message msgOut;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final String host;
    private final Integer port;
    private final ControlCenterGUI ccGUI;
    
    public CCController(String host, Integer port) {
        continueSimulation = true;
        ccGUI = new ControlCenterGUI(this);
        ccGUI.startGUI(ccGUI);
        this.host = host;
        this.port = port;
    }
    
    public boolean continueSimulation() {
        return continueSimulation;
    }
    
    public void updateGUITextArea(String text) {
        ccGUI.updateTextArea(text);
    }
    
    public void prepareHarvest(Integer numCornCobs, Integer numFarmers, 
            Integer maxSteps, Integer timeout) {
        continueSimulation = true;
        msgOut = new Message(HarvestState.Prepare, numCornCobs, 
                numFarmers, maxSteps, timeout);
        sendMessage();
    }
    
    public void readyToPrep() {
        ccGUI.readyToPrep();
    }
    
    public void prepComplete() {
        ccGUI.prepComplete();
    }

    public void startHarvest() {
        String msgBody = "Start the harvest";
        msgOut = new Message(msgBody, HarvestState.Start);
        sendMessage();
    }
    
    public void readyToCollect() {
        ccGUI.readyToCollect();
    }

    public void startCollecting() {
        String msgBody = "Collect the corn cobs";
        msgOut = new Message(msgBody, HarvestState.Collect);
        sendMessage();
    }
    
    public void readyToReturn() {
        ccGUI.readyToReturn();
    }

    public void returnHarvest() {
        String msgBody = "Return with the corn cobs";
        msgOut = new Message(msgBody, HarvestState.Return);
        sendMessage();
    }

    public void stop() {
        String msgBody = "Stop the harvest";
        msgOut = new Message(msgBody, HarvestState.Stop);
        sendMessage();
    }
    
    public void fiStopped() {
        ccGUI.fiStopped();
    }

    public void exit() {
        String msgBody = "End simulation";
        msgOut = new Message(msgBody, HarvestState.Exit);
        sendMessage();
    }
    
    public void fiExited() {
        ccGUI.fiExited();
        continueSimulation = false;
    }
    
    private void sendMessage() {
        ClientCom ccon = new ClientCom(host, port);
        
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
