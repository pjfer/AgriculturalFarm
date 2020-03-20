package FarmInfrastructure;

import FarmInfrastructure.Com.CcStub;
import FarmInfrastructure.Com.FIServer;
import FarmInfrastructure.GUI.FarmInfGUI;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;

/**
 * Classe responsável por atualizar a Interface Gráfica e por
 * enviar mensagens ao CC.
 */
public class FIController {
    
    FarmInfGUI fiGUI;
    public boolean allCorbsCollected;
    public boolean allFarmersInGranary;
    private MStoreHouse sh;
    private MGranary gr;
    private MPath path;
    private MStandingArea sa;
    private CcStub cc;
    private FIServer fiServer;
    private int counter;
    private int nFarmers;
    
    public FIController() {
    }
    
    public FIController(FarmInfGUI fiGUI, MGranary gr, 
            MPath path, MStandingArea sa, MStoreHouse sh, CcStub cc){
        
        this.fiGUI = fiGUI;
        this.sh = sh;
        this.gr = gr;
        this.path = path;
        this.sa = sa;
        this.cc = cc;
        
    }
    public FIController(FarmInfGUI fiGUI, CcStub cc){
        this.fiGUI = fiGUI;
        this.cc = cc;
    }

    public void setSh(MStoreHouse sh) {
        this.sh = sh;
    }

    public void setGr(MGranary gr) {
        this.gr = gr;
    }

    public void setPath(MPath path) {
        this.path = path;
    }

    public void setSa(MStandingArea sa) {
        this.sa = sa;
    }
    
    public void setFiServer(FIServer fiServer){
        this.fiServer = fiServer;
    }
    
    public void farmerEnterSH(int id, int position) {
        fiGUI.moveFarmer(id, new Integer[] {0, position});
        System.out.println("Farmer: " + id 
                + " entered in the Store House in position: " + position);
        
        cc.update("Farmer: " + id 
                + " entered in the Store House in position: " + position);
    }
    
    public void farmerAwaiting(int id) {
        System.out.println("Farmer: " + id + "is awaiting in the SH.");
        cc.update("Farmer: " + id + " is awaiting in the Store House.");
        counter ++;
        
        if(counter == nFarmers){
            cc.farmersReady();
            System.out.println("All farmers Awaiting");
            counter = 0;
        }       
    }

    public void farmerStanding(int id, int position) {
        fiGUI.moveFarmer(id, new Integer[] {1, position});
        System.out.println("Farmer: " + id + " entered the Standing Area in the position: " + (position + 1));
        cc.update("Farmer: " + id + " entered the Standing Area in the position: " + (position + 1));
        
        counter ++;
        
        if(counter == nFarmers){
            System.out.println("All Farmers entered the Standing Area");
            cc.farmersPrepared();
            counter = 0;
        }
    }

    public void movePath(Integer id, Integer[] position) {
        fiGUI.moveFarmer(id, new Integer[] {2, position[0], position[1]});
        System.out.println("Farmer: " + id + " moved in Path to the position: " 
                + position[0] + " : "+ position[1]);
        
        cc.update("Farmer: " + id + " moved in Path to the position: " 
                + position[0] + " : "+ position[1]);
    }

    public void moveGranary(Integer id, Integer position) {
        fiGUI.moveFarmer(id, new Integer[] {3, position});
        System.out.println("Farmer: " + id + " entered the Granary in the position: "+ position);
        cc.update("Farmer: " + id + " entered the Granary in the position: "+ position);
        counter ++;
        
        if(counter == nFarmers){
            System.out.println("All Farmers entered the Granary");
            cc.farmersWCollect();
            counter = 0;
        }
    }
    
    public void collectCorn(Integer id){
        System.out.println("Farmer: " + id + " colected one cobs.");
        cc.update("Farmer: " + id + " colected one corn.");
    }
    
    public void storeCorn(Integer id) {
        System.out.println("Farmer: " + id + " stored one cobs.");
        cc.update("Farmer: " + id + " stored one corn.");
    }
    
    public void farmerTerminated(int farmerId) {
        cc.farmerTerminated(farmerId);
    }
    
    public void prepareFarm(int nf, int to, int ns){
        System.out.println("Preparing Farm");
       
        nFarmers = nf;
        counter = 0;
        gr.prepareSimulation(to);
        path.prepareSimulation(nf, to, ns);
        sa.prepareSimulation();
        
        System.out.println("Farmers Proceed to Standing Area");
        sh.prepareSimulation(nf, to);
        
    }
    
    public void startMove(){
        System.out.println("Farmers proceeded to Path");
        sa.proceedToPath();
    }
    
    public void startCollection(){
        gr.allFarmersInGranary();
    }
    
    public void returnWCorn(){
        gr.returnToStoreHouse();
    }
    
    public void stopHarvest(){
        sa.stopSimulation();
        path.stopSimulation();
        gr.stopSimulation();
        sh.stopSimulation();
    }
    
    public void exitSimulation(){
        sa.stopSimulation();
        path.stopSimulation();
        gr.stopSimulation();
        sh.exitSimulation();
        fiServer.close();
    }
    
}
