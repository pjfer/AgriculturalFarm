package FarmInfrastructure.Com;

import FarmInfrastructure.FIController;
import FarmInfrastructure.Thread.TCCCom;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class FIServer {
    
    private final Integer port;
    private ServerSocket socket;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final FIController fiController;
    private TCCCom ccCom;
    private boolean closed;
    
    public FIServer(Integer port, FIController fiController){
        this.port = port;
        this.fiController = fiController;
    }
    
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
    
    public boolean close() {
        try {
            in.close();
            out.close();
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
    
    public void newConnection(){
        try {
            clientSocket = socket.accept();
            ccCom = new TCCCom(clientSocket, fiController);
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
