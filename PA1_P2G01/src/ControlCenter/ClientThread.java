package ControlCenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class to handle the requests from clients in a thread based system.
 * 
 * @author Pedro Ferreira
 */
public class ClientThread extends Thread {
    private Socket clientSocket;
    private HarvestState hvState;
    private HarvestConfig hc;
    
    public ClientThread() {
        super();
    }
    
    public ClientThread(Socket clientSocket, HarvestState hvState,
            HarvestConfig hc) {
        this.clientSocket = clientSocket;
        this.hvState = hvState;
        this.hc = hc;
    }
    
    @Override
    public void run() {
        BufferedReader messageIn = null; 
        PrintWriter messageOut = null;
        String message;
        
        try {
            messageIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            messageOut = new PrintWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream()));
            
            switch (hvState) {
                case Initial:
                    message = "numFarmers = " + hc.getNumFarmers() + 
                    ", numMaxSteps = " + hc.getNumMaxSteps() +
                    ", timeoutPath = " + hc.getTimeoutPath();
                    break;
                case Prepare:
                    message = "All farmers can procede to the Path!";
                    break;
                case Walk:
                    message = "Waiting for all farmers to reach the Granary!";
                    break;
                case WaitToCollect:
                    message = "Waiting for all farmers to reach the Granary!";
                    break;
                case Collect:
                    message = "All farmers can start to collect!";
                    break;
                case WaitToReturn:
                    message = "Waiting for all farmers to collect!";
                    break;
                case Return:
                    message = "All farmers can procede to the Path!";
                    break;
                case Store:
                    message = "Waiting for all farmers to deliver the cobs!";
                    break;
                default:
                    message = "Nothing to do!";
                    break;
            }
            
            messageOut.println(message);
            messageOut.flush();
            String clientMessage = messageIn.readLine();
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
