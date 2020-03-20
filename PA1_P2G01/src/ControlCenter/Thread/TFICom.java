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
                case Prepare:
                    ccController.updateGUITextArea(msgIn.getBody());
                    ccController.prepComplete();
                    msgOut = new Message(msgBody, hvState);
                    break;
                case Walk:
                    ccController.updateGUITextArea(msgIn.getBody());
                    msgOut = new Message(msgBody, hvState);
                    break;
                case WaitToCollect:
                    ccController.updateGUITextArea(msgIn.getBody());
                    ccController.readyToCollect();
                    msgOut = new Message(msgBody, hvState);
                    break;
                case WaitToReturn:
                    ccController.updateGUITextArea(msgIn.getBody());
                    ccController.readyToReturn();
                    msgOut = new Message(msgBody, hvState);
                    break;
                case Store:
                    ccController.updateGUITextArea(msgIn.getBody());
                    msgOut = new Message(msgBody, hvState);
                    break;
                case Stop:
                    ccController.updateGUITextArea(msgIn.getBody());
                    msgOut = new Message(msgBody, hvState);
                    ccController.fiStopped();
                    break;
                case Exit:
                    ccController.updateGUITextArea(msgIn.getBody());
                    ccController.fiExited();
                    msgOut = new Message(msgBody, hvState);
                    break;
                default:
                    System.err.println("ERROR: Unable to recognize the given"
                            + "harvest state!");
                    System.exit(1);
            }
            
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
