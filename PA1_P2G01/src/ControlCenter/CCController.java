package ControlCenter;

import Communication.HarvestState;
import Communication.Message;
import ControlCenter.GraphicalInterface.ControlCenterGUI;
import java.io.IOException;
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
    private final ControlCenterGUI ccGUI;
    
    public CCController(Socket socket) {
        continueSimulation = true;
        ccGUI = new ControlCenterGUI(this);
        ccGUI.startGUI(ccGUI);
        
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to create the input/output of "
                    + "FI server connection!");
            System.exit(1);
        }
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
        String msgBody = "Prepare the harvest";
        msgOut = new Message(msgBody, HarvestState.Prepare, numCornCobs, 
                numFarmers, maxSteps, timeout);
        
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
    
    public void readyToPrep() {
        ccGUI.readyToPrep();
    }
    
    public void prepComplete() {
        ccGUI.prepComplete();
    }

    public void startHarvest() {
        String msgBody = "Start the harvest";
        msgOut = new Message(msgBody, HarvestState.Start);
        
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
    
    public void readyToCollect() {
        ccGUI.readyToCollect();
    }

    public void startCollecting() {
        String msgBody = "Collect the corn cobs";
        msgOut = new Message(msgBody, HarvestState.Collect);
        
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
    
    public void readyToReturn() {
        ccGUI.readyToReturn();
    }

    public void returnHarvest() {
        String msgBody = "Return with the corn cobs";
        msgOut = new Message(msgBody, HarvestState.Return);
        
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

    public void stop() {
        String msgBody = "Stop the harvest";
        msgOut = new Message(msgBody, HarvestState.Stop);
        
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
    
    public void fiStopped() {
        ccGUI.fiStopped();
    }

    public void exit() {
        String msgBody = "End simulation";
        msgOut = new Message(msgBody, HarvestState.Exit);
        
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
    
    public void fiExited() {
        ccGUI.fiExited();
        continueSimulation = false;
    }
}
