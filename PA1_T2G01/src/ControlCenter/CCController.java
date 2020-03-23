package ControlCenter;

import Communication.ClientCom;
import Communication.HarvestState;
import Communication.Message;
import ControlCenter.GraphicalInterface.ControlCenterGUI;

/**
 * Class that handles the interaction between the control center GUI, 
 * the control center server and the farm infrastructure server.
 */
public class CCController {
    /**
     * Indicates if the simulation in the farm infrastructure is still running,
     * in other words, is set to false when the exit button on the control
     * center GUI is pressed.
     */
    private boolean continueSimulation;
    
    /**
     * Message received from the farm infrastructure server.
     */
    private Message msgIn;
    
    /**
     * Message sent to the farm infrastructure server.
     */
    private Message msgOut;
    
    /**
     * Server's address to where the messages will be sent.
     */
    private final String host;
    
    /**
     * Server's port to where the messages will be sent.
     */
    private final Integer port;
    
    /**
     * Control Center Graphical User Interface.
     */
    private final ControlCenterGUI ccGUI;
    
    /**
     * Instantiation and initialization of control center GUI.
     * 
     * @param host server's address to where the messages will be sent.
     * @param port server's port to where the messages will be sent.
     */
    public CCController(String host, Integer port) {
        continueSimulation = true;
        ccGUI = new ControlCenterGUI(this);
        ccGUI.startGUI(ccGUI);
        this.host = host;
        this.port = port;
    }
    
    /**
     * Check if the simulation on the farm infrastructure is still running.
     * 
     * @return true, if it's still running.
     *         false, otherwise.
     */
    public boolean continueSimulation() {
        return continueSimulation;
    }
    
    /**
     * After receiving a message from the farm infrastructure server, 
     * update the text area on the control center GUI with the most recent 
     * information about the farm infrastructure's state.
     * 
     * @param text information received from the farm infrastructure server.
     */
    public void updateGUITextArea(String text) {
        ccGUI.updateTextArea(text);
    }
    
    /**
     * After receiving a message from the farm infrastructure server, 
     * update the control center GUI to have only the prepare and exit buttons
     * available, as well as the harvest configuration fields.
     */
    public void readyToPrep() {
        ccGUI.readyToPrep();
    }
    
    /**
     * Send a message to the farm infrastructure server to prepare itself,
     * with the harvest configuration set in the control center GUI.
     * 
     * @param numCornCobs total number of corn cobs that each farmer has to get.
     * @param numFarmers total number of farmers that are going to harvest.
     * @param maxSteps maximum number of steps that each farmer can do.
     * @param timeout time needed for each farmer, when moving in the path.
     */
    public void prepareHarvest(Integer numCornCobs, Integer numFarmers, 
            Integer maxSteps, Integer timeout) {
        continueSimulation = true;
        msgOut = new Message(HarvestState.Prepare, numCornCobs, 
                numFarmers, maxSteps, timeout);
        sendMessage();
    }
    
    /**
     * After receiving a message from the farm infrastructure server, 
     * update the control center GUI to have only the start and exit buttons
     * available.
     */
    public void prepComplete() {
        ccGUI.prepComplete();
    }

    /**
     * Send a message to the farm infrastructure server to start the simulation.
     */
    public void startHarvest() {
        String msgBody = "Start the harvest";
        msgOut = new Message(msgBody, HarvestState.Start);
        sendMessage();
    }
    
    /**
     * After receiving a message from the farm infrastructure server, 
     * update the control center GUI to have only the collect, stop and exit 
     * buttons available.
     */
    public void readyToCollect() {
        ccGUI.readyToCollect();
    }

    /**
     * Send a message to the farm infrastructure server to make the farmers 
     * start collecting the corn cobs.
     */
    public void startCollecting() {
        String msgBody = "Collect the corn cobs";
        msgOut = new Message(msgBody, HarvestState.Collect);
        sendMessage();
    }
    
    /**
     * After receiving a message from the farm infrastructure server, 
     * update the control center GUI to have only the return, stop and exit 
     * buttons available.
     */
    public void readyToReturn() {
        ccGUI.readyToReturn();
    }

    /**
     * Send a message to the farm infrastructure server to make the farmers 
     * return to the storehouse with the collected corn cobs.
     */
    public void returnHarvest() {
        String msgBody = "Return with the corn cobs";
        msgOut = new Message(msgBody, HarvestState.Return);
        sendMessage();
    }

    /**
     * Send a message to the farm infrastructure server to stop the ongoing 
     * simulation and return to the initial state of the simulation.
     */
    public void stop() {
        String msgBody = "Stop the harvest";
        msgOut = new Message(msgBody, HarvestState.Stop);
        sendMessage();
    }
    
    /**
     * After receiving a message from the farm infrastructure server, 
     * update the control center GUI to have only the exit button available.
     */
    public void fiStopped() {
        ccGUI.fiStopped();
    }

    /**
     * Send a message to the farm infrastructure server to shutdown the 
     * simulation.
     */
    public void exit() {
        String msgBody = "End simulation";
        msgOut = new Message(msgBody, HarvestState.Exit);
        sendMessage();
    }
    
    /**
     * After receiving a message from the farm infrastructure server, 
     * terminates the control center GUI and signalizes the simulation's 
     * shutdown.
     */
    public void fiExited() {
        ccGUI.dispose();
        continueSimulation = false;
    }
    
    /**
     * Construction of the necessary means to send and receive messages between
     * the farm infrastructure server, through the initialization of a 
     * communication channel, as well as input and output streams.
     */
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
        
        if(msgIn.getType() == HarvestState.Error) {
            System.err.println(msgIn.getBody());
            System.exit(1);
        }
        
        ccon.close();
    }
}
