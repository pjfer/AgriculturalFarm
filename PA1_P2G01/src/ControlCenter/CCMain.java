package ControlCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class responsible for initializing the server and the communication between 
 * the servers, which also works as the main class for the control centre.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class CCServer {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        Integer ccPort = 1234;
        Integer fiPort = 1235;
        Scanner sc = new Scanner(System.in);
        Boolean stayConnected = true;
        Socket ccTofiSocket = null;
        ServerSocket fiToccSocket = null;
        
        System.out.println("Introduza o número de agricultores:");
        Integer numFarmers = sc.nextInt();
        
        System.out.println("Introduza o número máximo de passos:");
        Integer numMaxSteps = sc.nextInt();
        
        System.out.println("Introduza o timeout dentro do Path:");
        Integer timeoutPath = sc.nextInt();
        
        HarvestConfig hc = new HarvestConfig(numFarmers, numMaxSteps, 
                timeoutPath);
        
        try {
            fiToccSocket = new ServerSocket(ccPort);
            System.out.println("CC server listening to port " + ccPort);
        }
        catch(IOException e) {
            System.err.println("ERROR: Server Socket " + ccPort + 
                    " is already in use!");
            System.exit(1);
        }
        
        while(stayConnected) {
            try {
                ccTofiSocket = new Socket(host, fiPort);
                ClientThread clientThread = new ClientThread(ccTofiSocket, hc);
                clientThread.start();
            } catch(IOException e) {
                System.err.println("ERROR: Unable to connect to FI server!");
            }
            
            try {
                Socket clientSocket = fiToccSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket, hc);
                clientThread.start();
            }
            catch(IOException e) {
                System.err.println("ERROR: Unable to accept the client's " + 
                        "request!");
            }
        }
        
        try {
            fiToccSocket.close();
            ccTofiSocket.close();
            System.out.println("Server closed on socket " + ccPort);
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to close the Server Socket " + 
                    ccPort);
            System.exit(1);
        }
    }
}
