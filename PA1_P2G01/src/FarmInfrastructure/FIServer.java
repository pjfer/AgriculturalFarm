package FarmInfrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 
 * 
 * @author Pedro Ferreira
 */
public class FIServer {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        Integer port = 1234;
        System.out.println("Connecting to host " + host + "and port " + port);
        
        Socket serverSocket = null;
        BufferedReader inMessage = null;
        PrintWriter outMessage = null;
        
        try {
            serverSocket = new Socket(host, port);
            outMessage = new PrintWriter(serverSocket.getOutputStream(), true);
            inMessage = new BufferedReader(
                    new InputStreamReader(serverSocket.getInputStream()));
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to connect to server!");
            System.exit(1);
        }
        
        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        
        System.out.println("Message to send to the server: ");
        
        try {
            String message = stdIn.readLine();
            outMessage.println(message);
            System.out.println("Server response: " + inMessage.readLine());
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to send client's message!");
        }
        
        try {
            inMessage.close();
            outMessage.close();
            stdIn.close();
            serverSocket.close();
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to close server connection!");
        }
    }
}
