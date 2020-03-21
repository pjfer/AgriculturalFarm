package FarmInfrastructure.Com;

import FarmInfrastructure.FIController;
import FarmInfrastructure.Thread.TCCCom;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class that represents the server for the Farm Infrastructure.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class FIServer {
    
    /**
     * Farm infrastructure server port.
     */
    private final Integer port;
    
    /**
     * Farm Infrastructure server socket.
     */
    private ServerSocket socket;
    
    /**
     * Socket that handles the requests from the Control Center.
     */
    private Socket clientSocket;
    
    /**
     * Farm Infrastructure Controller that executes the requests received.
     */
    private final FIController fiController;
    
    /**
     * Thread that handles the incoming requests.
     */
    private TCCCom ccCom;
    
    /**
     * Flag that signals if the server socket was closed or not
     */
    private boolean closed;
    
    /**
     * Default Constructor.
     * 
     * @param port Farm infrastructure server port.
     * @param fiController Farm Infrastructure Controller that 
     * executes the requests received.
     */
    public FIServer(Integer port, FIController fiController){
        this.port = port;
        this.fiController = fiController;
    }
    
    /**
     * Method used to start the server socket.
     * 
     * @return Boolean that indicates if the server was successfully started.
     */
    public boolean start() {
        try {
            socket = new ServerSocket(port);
            System.out.println("FI server listening to port " + port);
            closed = false;
            return true;
        }
        catch(IOException e) {
            System.err.println("ERROR: Port " + port + " is already in use!");
            
            return false;
        }
    }
    
    /**
     * Method used to stop the server socket.
     * 
     * @return Boolean that indicates if the server was successfully stopped.
     */
    public boolean close() {
        try {
            socket.close();
            System.out.println("Server closed on port " + port);
            closed = true;
            return true;
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to close the server on port " + 
                    port);
            
            return false;
        }
        
    }
    
    /**
     * Method called while the server is listening.
     * Creates a thread for each new request received.
     * 
     */
    public void newConnection(){
        try {
            clientSocket = socket.accept();
            
            System.out.println("BANANA");
            
            //Create the Stream to retrieve messages.
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            System.out.println("BANANA");

            //Create the Stream to send messages.
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            
            System.out.println("BANANA");
            
            ccCom = new TCCCom(clientSocket, fiController, in, out);
            ccCom.start();
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to read the client's " + 
                    "message!");
            System.exit(1);
        }
    }  

    public boolean isClosed() {
        return closed;
    }
    
    
}
