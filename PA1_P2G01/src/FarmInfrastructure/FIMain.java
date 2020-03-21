package FarmInfrastructure;

import Communication.ServerCom;
import FarmInfrastructure.Com.CcStub;
import FarmInfrastructure.GUI.FarmInfGUI;
import FarmInfrastructure.Thread.TCCCom;
import FarmInfrastructure.Thread.TFarmer;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;
import java.net.SocketTimeoutException;

public class FIMain {
    
    public static boolean waitconnection;
    
    public static void main(String[] args){
        
        /**
         * Farm interface, responsible for showing graphically the state
         * of the farm.
         */
        
        FarmInfGUI fiGUI = new FarmInfGUI();
        
        /**
         * IP of the host server for the Control Center.
         */
        String host = "127.0.0.1";
        
        /**
         * Port of the host server for the Control Center.
         */
        Integer ccPort = 1200;
        
        /**
         *  Port for the Farm Interface Server.
         */
        Integer fiPort = 1300;
        
        /**
         * Communication Channel of the server.
         */
        ServerCom scon, sconi;
        
        /**
         * Thread to handle the process.
         */
        TCCCom handler;
        
        /**
         * Interface to communicate with the Control Center.
         */
        CcStub cc = new CcStub(host, ccPort);
        
        /**
         * Interface that controls the actual farm.
         */
        FIController fiController = new FIController(fiGUI, cc);
        
        /**
         * Monitor of the Granary Area.
         */
        MGranary gr = new MGranary(fiController);
        
        /**
         * Monitor of the Path Area.
         */
        MPath path = new MPath(fiController);
        
        /**
         * Monitor of the Store House Area.
         */
        MStoreHouse sh = new MStoreHouse(fiController);
        
        /**
         * Monitor of the Standing Area.
         */
        MStandingArea sa = new MStandingArea(fiController);
        
        
        /*  Set the monitors so the controller can use them.*/
        fiController.setGr(gr);
        fiController.setPath(path);
        fiController.setSa(sa);
        fiController.setSh(sh);
        
        /* Set the Nimbus look and feel. */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code ">
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FarmInfGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form. */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                fiGUI.setVisible(true);
            }
        });
        
        /* Create farmer threads.*/
        Thread threads[] = new Thread[5];
        for(int i = 1; i <= 5; i++){
            threads[i-1] = new TFarmer(i, gr, path, sh, sa);
            threads[i-1].start();
        }
        
       
        scon = new ServerCom (fiPort);
        scon.start (); 
        
        /* Wait for messages until it receives the die signal.*/
        waitconnection = true;
        do {
            try {
                /* Waits for a request from the server.*/
                sconi = scon.accept();
                
                /* Starts a thread to handle the request. */
                handler = new TCCCom(sconi, fiController);              
                handler.start ();
            } catch (SocketTimeoutException ex) {}
        } while(waitconnection);
        
        
        /* Wait for all the farmers to die. */
        for(int i = 0; i < 5; i++){
            try {
                threads[i].join();
                
                /*Send message that the farmer thread has terminated.*/
                fiController.farmerTerminated(i+1);
                
                System.out.println("Farmer "+(i+1)+ " has died");
            } catch (InterruptedException ex) {
                System.err.println("ERROR: Unable to terminate farmer " + i);
            } 
        }
        
        /*Delete the graphical interface.*/
        fiGUI.dispose();
        
        /*Stop the farm interface server.*/
        scon.end();
    }
    
    
}
