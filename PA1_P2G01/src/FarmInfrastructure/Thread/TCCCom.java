package FarmInfrastructure.Thread;

import Communication.HarvestState;
import Communication.Message;
import FarmInfrastructure.FIController;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class TCCCom extends Thread {
    private Message msgIn;
    private Message msgOut;
    private String msgBody;
    private Socket clientSocket;
    private FIController fiController;
    private HarvestState hvState;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public TCCCom() {
        super();
    }
    
    public TCCCom(Socket clientSocket, FIController fiController) {
        this.clientSocket = clientSocket;
        this.fiController = fiController;
        this.msgBody = "200 OK";
        this.msgOut = new Message(msgBody);
    }
    
    @Override
    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            msgIn = (Message) in.readObject();
            hvState = msgIn.getType();
            
            switch(hvState){
                case Prepare:
                    fiController.prepareFarm(5, 500, 1);
                    break;
                case Start:
                    fiController.startMove();
                    break;
                case Collect:
                    fiController.startCollection();
                    break;
                case Return:
                    fiController.returnWCorn();
                    break;
                case Stop:
                    fiController.stopHarvest();
                    break;
                case Exit:
                    fiController.exitSimulation();
                    break;
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
