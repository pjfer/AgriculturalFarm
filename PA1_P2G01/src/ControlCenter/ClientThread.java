package ControlCenter;

import Communication.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class to handle the requests from clients in a thread based system.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class ClientThread extends Thread {
    private Socket clientSocket;
    private HarvestState hvState;
    private HarvestConfig hc;
    
    public ClientThread() {
        super();
    }
    
    public ClientThread(Socket clientSocket, HarvestConfig hc) {
        this.clientSocket = clientSocket;
        this.hvState = HarvestState.Initial;
        this.hc = hc;
    }
    
    @Override
    public void run() {
        ObjectInputStream messageIn = null; 
        ObjectOutputStream messageOut = null;
        Message message;
        String body;
        
        try {
            messageIn = new ObjectInputStream(clientSocket.getInputStream());
            messageOut = new ObjectOutputStream(clientSocket.getOutputStream());
            
            switch (hvState) {
                case Initial:
                    body = "{ numFarmers: " + hc.getNumFarmers() + 
                            ", numMaxSteps: " + hc.getNumMaxSteps() +
                            ", timeoutPath: " + hc.getTimeoutPath() + " }";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.Prepare;
                    break;
                case Prepare:
                    body = "Start the harvest";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.Walk;
                    break;
                case Walk:
                    body = "Waiting for all farmers to reach the Granary";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.WaitToCollect;
                    break;
                case WaitToCollect:
                    body = "Waiting for all farmers to reach the Granary";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.Collect;
                    break;
                case Collect:
                    body = "Collect the corn cobs";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.WaitToReturn;
                    break;
                case WaitToReturn:
                    body = "Waiting for all farmers to collect";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.Return;
                    break;
                case Return:
                    body = "Return with the corn cobs";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.Store;
                    break;
                case Store:
                    body = "Waiting for all farmers to deliver the cobs";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.Initial;
                    break;
                case Stop:
                    body = "Stop the harvest";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.Initial;
                    break;
                case Exit:
                    body = "Exit simulation";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.Initial;
                    break;
                default:
                    body = "Nothing to do";
                    message = new Message(body, hvState, this.getId());
                    hvState = HarvestState.Initial;
                    break;
            }
            
            messageOut.writeObject(message);
            messageOut.flush();
            String clientMessage = messageIn.readUTF();
            System.out.println("Client's message: " + clientMessage);
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to read the message from client "
                    + "on socket" + clientSocket);
        }
        
        try {
            messageIn.close();
            messageOut.close();
            clientSocket.close();
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to close the connection of " + 
                    clientSocket);
        }
    }
}
