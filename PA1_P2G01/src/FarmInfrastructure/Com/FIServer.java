package FarmInfrastructure.Com;

import Communication.Message;
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
    
    public FIServer(Integer port){
        this.port = port;
    }
    
    public boolean start() {
        try {
            socket = new ServerSocket(port);
            System.out.println("FI server listening to port " + port);
            
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
    
    public Message readMessage() {
        try {
            clientSocket = socket.accept();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            return (Message) in.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("ERROR: Unable to read the client's " + 
                    "message!");
            
            return null;
        }
    }
    
    public boolean sendResponse(String response) {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(response);
            out.flush();
            
            return true;
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to send a response! ");
            
            return false;
        }
    }
    

    
}
