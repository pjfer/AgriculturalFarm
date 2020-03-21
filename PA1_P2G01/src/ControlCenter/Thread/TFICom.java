package ControlCenter.Thread;

import Communication.Message;
import Communication.HarvestState;
import ControlCenter.CCController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class to handle the requests from clients in a thread based system.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class TFICom extends Thread {
    private Message msgIn;
    private Message msgOut;
    private String msgBody;
    private Socket clientSocket;
    private CCController ccController;
    private HarvestState hvState;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public TFICom() {
        super();
    }
    
    public TFICom(Socket clientSocket, CCController ccController) {
        this.clientSocket = clientSocket;
        this.ccController = ccController;
        this.msgBody = "200 OK";
    }
    
    @Override
    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            msgIn = (Message) in.readObject();
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
            out.writeObject(msgOut);
            out.flush();
        }
        catch(IOException | ClassNotFoundException e) {
            System.err.println("ERROR: Unable to read the msgOut from client "
                    + "socket on port " + clientSocket.getPort());
            System.exit(1);
        }
        
        try {
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
