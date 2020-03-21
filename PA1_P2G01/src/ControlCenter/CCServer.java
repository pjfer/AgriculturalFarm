package ControlCenter;

import ControlCenter.Thread.TFICom;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class responsible for initializing the Control Center server.
 * This server communicates with the Farm Infrastructure client, with the goal
 * of reading all its messages.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class CCServer {
    private final Integer port;
    private ServerSocket socket;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private TFICom fiCom;
    
    public CCServer(Integer port) {
        this.port = port;
    }
    
    public boolean start() {
        try {
            socket = new ServerSocket(port);
            System.out.println("CC server listening to port " + port);
            
            return true;
        }
        catch(IOException e) {
            System.err.println("ERROR: Port " + port + " is already in use!");
            
            return false;
        }
    }
    
    public boolean close() {
        try {
            in.close();
            out.close();
            socket.close();
            System.out.println("Server closed on port " + port);
            
            return true;
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to close the server on port " + 
                    port);
            
            return false;
        }
    }
    
    public void newConnection(CCController ccController) {
        try {
            clientSocket = socket.accept();
            //Create the Stream to retrieve messages.
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            //Create the Stream to send messages.
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            fiCom = new TFICom(clientSocket, ccController, in, out);
            fiCom.start();
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to read the client's " + 
                    "message!");
            System.exit(1);
        }
    }
}
