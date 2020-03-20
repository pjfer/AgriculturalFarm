package ControlCenter;

import Communication.HarvestConfig;
import Communication.HarvestState;
import ControlCenter.GraphicalInterface.ControlCenterGUI;
import ControlCenter.Thread.TFICom;
import java.io.IOException;
import java.net.Socket;

/**
 * Class 
 *
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class CCController {
    private Socket socket;
    private TFICom fiCom;
    private final ControlCenterGUI ccGUI;
    
    public CCController() {
        ccGUI = new ControlCenterGUI(this);
        ccGUI.startGUI(ccGUI);
    }
    
    public boolean setupFICom(String host, Integer fiPort) {
        try {
            socket = new Socket(host, fiPort);
            
            return true;
        }
        catch (IOException e) {
            System.err.println("ERROR: Unable to connect to FI server!");
            
            return false;
        }
    }
    
    public void prepareHarvest(Integer numCornCobs, Integer numFarmers, 
            Integer maxSteps, Integer timeout) {
        HarvestConfig hc = new HarvestConfig(numCornCobs, numFarmers, 
                maxSteps, timeout);
        fiCom = new TFICom(socket);
        fiCom.setHarvestConfig(hc);
        fiCom.setHarvestState(HarvestState.Prepare);
    }
    
    public void prepComplete() {
        ccGUI.prepComplete();
    }

    public void startHarvest() {
        fiCom = new TFICom(socket);
        fiCom.setHarvestState(HarvestState.Start);
    }
    
    public void readyToCollect() {
        ccGUI.readyToCollect();
    }

    public void startCollecting() {
        fiCom = new TFICom(socket);
        fiCom.setHarvestState(HarvestState.Collect);
    }
    
    public void readyToReturn() {
        ccGUI.readyToReturn();
    }

    public void returnHarvest() {
        fiCom = new TFICom(socket);
        fiCom.setHarvestState(HarvestState.Return);
    }

    public void stop() {
        fiCom = new TFICom(socket);
        fiCom.setHarvestState(HarvestState.Stop);
    }
    
    public void fiStopped() {
        ccGUI.fiStopped();
    }

    public void exit() {
        fiCom = new TFICom(socket);
        fiCom.setHarvestState(HarvestState.Exit);
    }
    
    public void fiExited() {
        ccGUI.fiExited();
    }
}
