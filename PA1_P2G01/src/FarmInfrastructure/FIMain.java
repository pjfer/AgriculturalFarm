package FarmInfrastructure;

import FarmInfrastructure.Com.FIServer;
import FarmInfrastructure.GUI.FarmInfGUI;
import FarmInfrastructure.Thread.TFarmer;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;
import java.util.Scanner; 

public class FIMain {
    
    public static void main(String[] args){
        
        FarmInfGUI fiGUI = new FarmInfGUI();
        
        
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
        
        Thread threads[] = new Thread[5];
        for(int i = 1; i <= 5; i++){
            threads[i-1] = new TFarmer(i, gr, path, sh, sa);
            threads[i-1].start();
        }
        Scanner scan = new Scanner(System.in);
        
        FIServer server = new FIServer(gr, path, sa, sh);
        while(true){
            int i = scan.nextInt();
            switch(i){
                case(0):
                    System.out.println("Preparing Farm");
                    server.prepareFarm(5, 500, 2);
                    break;
                case(1):
                    server.startCollection();
                    break;
                case(2):
                    server.collectCorn();
                    break;
                case(3):
                    server.returnWCorn();
                    break;
                
            }
        }
        
    
    
    }
    
    
}
