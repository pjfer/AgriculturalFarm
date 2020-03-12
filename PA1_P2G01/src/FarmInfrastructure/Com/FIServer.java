package FarmInfrastructure;

import FarmInfrastructure.GUI.FarmInfGUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class FIServer {
    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        Integer ccPort = 1234;
        Integer fiPort = 1235;
        System.out.println("Connecting to host " + host + " and port " + ccPort);
        
        Socket fiToccSocket = null;
        ServerSocket ccTofiSocket = null;
        BufferedReader inMessage = null;
        PrintWriter outMessage = null;
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FarmInfGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FarmInfGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FarmInfGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FarmInfGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        FarmInfGUI fiGUI = new FarmInfGUI();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                fiGUI.setVisible(true);
            }
        });
        
        try {
            ccTofiSocket = new ServerSocket(fiPort);
            System.out.println("FI server listening to port " + fiPort);
        }
        catch(IOException e) {
            System.err.println("ERROR: Server Socket " + fiPort + 
                    " is already in use!");
            System.exit(1);
        }
        
        try {
            Socket clientSocket = ccTofiSocket.accept();
            outMessage = new PrintWriter(clientSocket.getOutputStream(), true);
            inMessage = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Server response: " + inMessage.readLine());
            String message = "How you doing?";
            outMessage.println(message);
            outMessage.flush();
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to accept the client's " + 
                        "request!");
        }
        
        try {
            fiToccSocket = new Socket(host, ccPort);
            outMessage = new PrintWriter(fiToccSocket.getOutputStream(), true);
            inMessage = new BufferedReader(
                    new InputStreamReader(fiToccSocket.getInputStream()));
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to connect to server!");
            System.exit(1);
        }
        
        try {
            String message = "When is the dinner?";
            outMessage.println(message);
            outMessage.flush();
            System.out.println("Server response: " + inMessage.readLine());
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to send client's message!");
        }
        
        try {
            inMessage.close();
            outMessage.close();
            fiToccSocket.close();
            ccTofiSocket.close();
        }
        catch(IOException e) {
            System.err.println("ERROR: Unable to close server connection!");
        }
        
        
        
        
    }
    public boolean allCorbsCollected;
    public boolean allFarmersInGranary;

    public void farmerAwaiting(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void farmerEnterSH(int id, int position) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void farmerStanding(int id, int position) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void movePath(Integer id, Integer[] position) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void moveGranary(Integer id, Integer position) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
