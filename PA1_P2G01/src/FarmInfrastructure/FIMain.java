package FarmInfrastructure;

import Communication.HarvestState;
import Communication.Message;
import FarmInfrastructure.Com.FIServer;
import FarmInfrastructure.GUI.FarmInfGUI;
import FarmInfrastructure.Thread.TFarmer;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;
import java.util.Scanner; 
import java.util.logging.Level;
import java.util.logging.Logger;

public class FIMain {
    
    public static void main(String[] args){
        
        FarmInfGUI fiGUI = new FarmInfGUI();
        
        
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
        
        FIController fiController = new FIController(fiGUI);
        MGranary gr = new MGranary(fiController);
        MPath path = new MPath(fiController);
        MStoreHouse sh = new MStoreHouse(fiController);
        MStandingArea sa = new MStandingArea(fiController);
        
        fiController.setGr(gr);
        fiController.setPath(path);
        fiController.setSa(sa);
        fiController.setSh(sh);
        
        
        Thread threads[] = new Thread[5];
        for(int i = 1; i <= 5; i++){
            threads[i-1] = new TFarmer(i, gr, path, sh, sa);
            threads[i-1].start();
        }
        Scanner scan = new Scanner(System.in);
        
        HarvestState hs;
        Message msgReceived;
        String host = "127.0.0.1";
        Integer ccPort = 1234;
        Integer fiPort = 1235;
        
        FIServer fiServer = new FIServer(fiPort);
        
        if (!fiServer.start())
            System.exit(1);
        
        do {
            msgReceived = fiServer.readMessage();
            hs = msgReceived.getType();
            switch(hs){
                case Prepare:
                    System.out.println("Preparing Farm");
                    fiController.prepareFarm(5, 500, 1);
                    break;
                case Start:
                    fiController.startCollection();
                    break;
                case Collect:
                    fiController.collectCorn();
                    break;
                case Return:
                    fiController.returnWCorn();
                    break;
                case Stop:
                    fiController.stopHarvest();
                    break;
                case Exit:
                    fiController.exitSimulation();
                    break;
            }
        } while(hs != HarvestState.Exit && hs != null);
        
        
        for(int i = 0; i < 5; i++){
            try {
                threads[i].join();
                System.out.println("Farmer "+(i+1)+ " has died");
            } catch (InterruptedException ex) {
                Logger.getLogger(FIMain.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        fiGUI.dispose();
        if (!fiServer.close())
            System.exit(1);
    
    }
    
    
}
