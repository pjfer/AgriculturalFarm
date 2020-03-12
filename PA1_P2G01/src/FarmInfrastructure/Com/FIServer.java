package FarmInfrastructure.Com;

import FarmInfrastructure.GUI.FarmInfGUI;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;

/**
 * 
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class FIServer {
    
    MStoreHouse sh;
    MGranary gr;
    MPath path;
    MStandingArea sa;
    
    public FIServer(MGranary gr, MPath path, MStandingArea sa, MStoreHouse sh){
        this.sh = sh;
        this.gr = gr;
        this.path = path;
        this.sa = sa;
    }
    
    public void prepareFarm(int nf, int to){
        gr.prepareSimulation(to);
        path.prepareSimulation(nf, to);
        sh.prepareSimulation(nf, to);
    }
    
    public void startCollection(){
        sa.proceedToPath();
    }
    
    public void collectCorn(){
        gr.allFarmersInGranary();
    }
    
    public void returnWCorn(){
        gr.allCorbsCollected();
    }
    
    public void stopHarvest(){
    
    }
    
    public void exitSimulation(){
    
    }
    
}
