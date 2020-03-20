package FarmInfrastructure;

import FarmInfrastructure.Com.CcStub;
import FarmInfrastructure.Com.FIServer;
import FarmInfrastructure.GUI.FarmInfGUI;
import FarmInfrastructure.Thread.TFarmer;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FIMain {
    
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
        Integer ccPort = 1234;
        
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
        
        /**
         *  Port for the Farm Interface Server.
         */
        Integer fiPort = 1235;
        
        /**
         * Farm Interface Server responsible to process the messages from the
         * Control Center.
         */
        FIServer fiServer = new FIServer(fiPort, fiController);
        
        /*
            Set the monitors so the controller can use them.
        */
        fiController.setGr(gr);
        fiController.setPath(path);
        fiController.setSa(sa);
        fiController.setSh(sh);
        fiController.setFiServer(fiServer);
        
        /* Set the Nimbus look and feel */
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                fiGUI.setVisible(true);
            }
        });
        
        /*
            Create farmer threads.
        */
        Thread threads[] = new Thread[5];
        for(int i = 1; i <= 5; i++){
            threads[i-1] = new TFarmer(i, gr, path, sh, sa);
            threads[i-1].start();
        }
        
        /*
            Start the server.
        */
        if (!fiServer.start())
            System.exit(1);
        
        /*
            Wait for messages until it receives the die signal.
        */
        do {
            fiServer.newConnection();
        } while(!fiServer.isClosed());
        
        /*
            Wait for all the farmers to die.
        */
        for(int i = 0; i < 5; i++){
            try {
                threads[i].join();
                /*
                    Send message that all farmer threads were terminated were killed.
                */
                fiController.farmerTerminated(i+1);
                
                System.out.println("Farmer "+(i+1)+ " has died");
            } catch (InterruptedException ex) {
                Logger.getLogger(FIMain.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        /*
            Delete the graphical interface.
        */
        fiGUI.dispose();
    
    }
    
    
}
