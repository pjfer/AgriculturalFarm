package ControlCenter.Thread;

import Communication.Message;
import Communication.HarvestConfig;
import Communication.HarvestState;
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
    private Socket clientSocket;
    private HarvestConfig hc;
    private HarvestState hvState;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public TFICom() {
        super();
    }
    
    public TFICom(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    public void setHarvestConfig(HarvestConfig hc) {
        this.hc = hc;
    }
    
    public void setHarvestState(HarvestState hvState) {
        this.hvState = hvState;
    }
    
    @Override
    public void run() {
        Message message;
        String body;
        
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            
            switch (hvState) {
                case Initial:
                    body = hc.toString();
                    message = new Message(body, hvState, getId());
                    break;
                case Prepare:
                    body = "Start the harvest";
                    message = new Message(body, hvState, getId());
                    break;
                case Walk:
                    body = "Waiting for all farmers to reach the Granary";
                    message = new Message(body, hvState, getId());
                    break;
                case WaitToCollect:
                    body = "Waiting for all farmers to reach the Granary";
                    message = new Message(body, hvState, getId());
                    break;
                case Collect:
                    body = "Collect the corn cobs";
                    message = new Message(body, hvState, getId());
                    break;
                case WaitToReturn:
                    body = "Waiting for all farmers to collect";
                    message = new Message(body, hvState, getId());
                    break;
                case Return:
                    body = "Return with the corn cobs";
                    message = new Message(body, hvState, getId());
                    break;
                case Store:
                    body = "Waiting for all farmers to deliver the cobs";
                    message = new Message(body, hvState, getId());
                    break;
                case Stop:
                    body = "Stop the harvest";
                    message = new Message(body, hvState, getId());
                    break;
                case Exit:
                    body = "Exit simulation";
                    message = new Message(body, hvState, getId());
                    break;
                default:
                    body = "Nothing to do";
                    message = new Message(body, hvState, getId());
                    break;
            }
            
            out.writeObject(message);
            out.flush();
            Message response = (Message) in.readObject();
            System.out.println("Server's message: " + response.getBody());
        }
        catch(IOException | ClassNotFoundException e) {
            System.err.println("ERROR: Unable to read the message from client "
                    + "socket on port " + clientSocket.getPort());
        }
        
        try {
            in.close();
            out.close();
            clientSocket.close();
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to close the connection of " + 
                    clientSocket);
        }
    }
}
