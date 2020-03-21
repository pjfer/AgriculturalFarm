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
    private Socket socket;
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
        setupSocket();
        msgOut = new Message(HarvestState.Prepare, numCornCobs, 
                numFarmers, maxSteps, timeout);
        sendMessage();
        closeSocket();
    }
    
    public void readyToPrep() {
        ccGUI.readyToPrep();
    }
    
    public void prepComplete() {
        ccGUI.prepComplete();
    }

    public void startHarvest() {
        setupSocket();
        String msgBody = "Start the harvest";
        msgOut = new Message(msgBody, HarvestState.Start);
        sendMessage();
        closeSocket();
    }
    
    public void readyToCollect() {
        ccGUI.readyToCollect();
    }

    public void startCollecting() {
        setupSocket();
        String msgBody = "Collect the corn cobs";
        msgOut = new Message(msgBody, HarvestState.Collect);
        sendMessage();
        closeSocket();
    }
    
    public void readyToReturn() {
        ccGUI.readyToReturn();
    }

    public void returnHarvest() {
        setupSocket();
        String msgBody = "Return with the corn cobs";
        msgOut = new Message(msgBody, HarvestState.Return);
        sendMessage();
        closeSocket();
    }

    public void stop() {
        setupSocket();
        String msgBody = "Stop the harvest";
        msgOut = new Message(msgBody, HarvestState.Stop);
        sendMessage();
        closeSocket();
    }
    
    public void fiStopped() {
        ccGUI.fiStopped();
    }

    public void exit() {
        setupSocket();
        String msgBody = "End simulation";
        msgOut = new Message(msgBody, HarvestState.Exit);
        sendMessage();
        closeSocket();
    }
    
    public void fiExited() {
        ccGUI.fiExited();
        continueSimulation = false;
    }
    
    private void setupSocket() {
        try {
            socket = new Socket(host, port);
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to create the socket to the "
                    + "FI server!");
            System.exit(1);
        }
    }
    
    private void closeSocket() {
        try {
            //Close the communication channels.
            out.close();
            socket.close();
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to close the connection of " +
                    socket);
            System.exit(1);
        }
    }
    
    private void sendMessage() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
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
