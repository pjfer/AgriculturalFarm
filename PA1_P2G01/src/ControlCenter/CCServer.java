package ControlCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class responsible for initializing the server and the communication between 
 * the servers, which also works as the main class for the control centre.
 * 
 * @author Pedro Ferreira
 */
public class CCServer {
    public static void main(String[] args) {
        Integer port = 1234;
        Boolean stayConnected = true;
        ServerSocket serverSocket = null;
        
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening to socket " + port);
        }
        catch (IOException e) {
            System.err.println("ERROR: Server Socket " + port + 
                    " is already in use!");
            System.exit(1);
        }
        
        while (stayConnected) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket);
                clientThread.start();
            }
            catch (IOException e) {
                System.err.println("ERROR: Unable to accept the client's " + 
                        "request!");
            }
        }
        
        try {
            serverSocket.close();
            System.out.println("Server closed on socket " + port);
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to close the Server Socket " + 
                    port);
            System.exit(1);
        }
    }
}
