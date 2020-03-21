package FarmInfrastructure;

import Communication.ServerCom;
import FarmInfrastructure.Com.CcStub;
import FarmInfrastructure.GUI.FarmInfGUI;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;

/**
 * Class responsible for the update of the graphical interface,
 * execution of the request from the Control Center and the delivery of the
 * update messages to the it.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class FIController {
    
    /**
     * Farm Interface Graphical Interface Controller.
     */
    FarmInfGUI fiGUI;
    
    /**
     * Flag that signals that all farmers collect all cobs.
     */
    public boolean allCobsCollected;
    
    /**
     * Flag that signals that all farmers are awaiting in the granary.
     */
    public boolean allFarmersInGranary;
    
    /**
     * Monitor used to Control the Store House.
     */
    private MStoreHouse sh;
    
    /**
     * Monitor used to Control the Granary.
     */
    private MGranary gr;
    
    /**
     * Monitor used to Control the Path.
     */
    private MPath path;
    
    /**
     * Monitor used to Control the Standing Area.
     */
    private MStandingArea sa;
    
    /**
     * Stub Class used to send messages to the Control Center.
     */
    private CcStub cc;
    
    /**
     * Class representative of the Farm Infrastructure Server.
     */
    private FIMain fiMain;
    
    private ServerCom sconi;
    
    /**
     * Integer that indicates the number of farmers awaiting an event.
     */
    private int counter;
    
    /**
     * Integer that represents the number of 
     * farmers running on the present simulation.
     */
    private int nFarmers;
    
    /**
     *
     * @param fiGUI Farm Interface Graphical Interface Controller.
     * @param gr Monitor used to Control the Granary.
     * @param path Monitor used to Control the Path.
     * @param sa Monitor used to Control the Standing Area.
     * @param sh Monitor used to Control the Store House.
     * @param cc Stub Class used to send messages to the Control Center.
     */
    public FIController(FarmInfGUI fiGUI, MGranary gr, 
            MPath path, MStandingArea sa, MStoreHouse sh, CcStub cc){
        
        this.fiGUI = fiGUI;
        this.sh = sh;
        this.gr = gr;
        this.path = path;
        this.sa = sa;
        this.cc = cc;
        this.nFarmers = 5;
    }
    
    /**
     * Default Constructor
     * 
     * @param fiGUI Farm Interface Graphical Interface Controller.
     * @param cc  Stub Class used to send messages to the Control Center.
     */
    public FIController(FarmInfGUI fiGUI, CcStub cc){
        this.fiGUI = fiGUI;
        this.cc = cc;
        this.nFarmers = 5;
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
    
    public void setFiServer(FIMain fiMain){
        this.fiMain = fiMain;
    }

    public void setSconi(ServerCom sconi) {
        this.sconi = sconi;
    }
    
    /**
     * Method called when a farmer enters the Store House.
     * Sends an update message to the Control Center signaling the event.
     * 
     * @param farmerId Farmer ID.
     * @param position Position in which the farmer was positioned.
     */
    public void farmerEnterSH(int farmerId, int position) {
        fiGUI.moveFarmer(farmerId, new Integer[] {0, position});
        System.out.println("Farmer: " + farmerId 
                + " entered in the Store House in position: " + position);
        
        cc.update("Farmer: " + farmerId 
                + " entered in the Store House in position: " + position + "\n");
    }
    
    /**
     * Method Called when a farmer waits the start of the Simulation.
     * Sends an update message to the Control Center for each farmer awaiting
     * and a WaitToStart message once all farmers are awaiting.
     * 
     * @param farmerId Farmer ID.
     */
    public void farmerAwaiting(int farmerId) {
        System.out.println("Farmer: " + farmerId + " is awaiting in the SH.");
        cc.update("Farmer: " + farmerId + " is awaiting in the Store House.\n");
        counter ++;
        
        if(counter == nFarmers){
            cc.farmersReady();
            System.out.println("All farmers Awaiting");
            counter = 0;
        }       
    }
    /**
     * Method called when a farmer enters the Standing Area.
     * Sends an update message to the Control Center for each farmer awaiting
     * and a WaitToWalk message once all farmers are in the Standing Area.
     * 
     * @param farmerId Farmer ID.
     * @param position Position in which the farmer was positioned.
     */
    public void farmerStanding(int farmerId, int position) {
        fiGUI.moveFarmer(farmerId, new Integer[] {1, position});
        System.out.println("Farmer: " + farmerId + 
                " entered the Standing Area in the position: " 
                + (position + 1));
        
        cc.update("Farmer: " + farmerId + 
                " entered the Standing Area in the position: " 
                + (position + 1) + "\n" );
        
        counter ++;
        
        if(counter == nFarmers){
            System.out.println("All Farmers entered the Standing Area");
            cc.farmersPrepared();
            counter = 0;
        }
    }

    /**
     * Method called when a farmer moves in Path.
     * Sends an update message to the Control Center for each farmer movement.
     * 
     * @param farmerId Farmer ID.
     * @param position Position in which the farmer was positioned.
     */
    public void movePath(Integer farmerId, Integer[] position) {
        fiGUI.moveFarmer(farmerId, 
                new Integer[] {2, position[0], position[1]});
        
        System.out.println("Farmer: " + 
                farmerId + " moved in Path to the position: " 
                + position[0] + " : "+ position[1]);
        
        cc.update("Farmer: " + farmerId + " moved in Path to the position: " 
                + position[0] + " : "+ position[1] + "\n");
    }

    /**
     * Method called when a farmer enters the Granary.
     * Sends an update message to the Control Center for each farmer that enters.
     * 
     * @param farmerId Farmer ID.
     * @param position Position in which the farmer was positioned. 
     */
    public void moveGranary(Integer farmerId, Integer position) {
        fiGUI.moveFarmer(farmerId, new Integer[] {3, position});
        System.out.println("Farmer: " + farmerId + 
                " entered the Granary in the position: "+ position);
        
        cc.update("Farmer: " + farmerId + 
                " entered the Granary in the position: "+ position + "\n");
        
        counter ++;
        
        if(counter == nFarmers){
            System.out.println("All Farmers entered the Granary");
            cc.farmersWCollect();
            counter = 0;
        }
    }
    
    /**
     * Method called when a farmer collects a Cob.
     * Sends an update message to the Control Center 
     * for each cob collected for each farmer.
     * 
     * @param farmerId Farmer ID.
     */
    public void collectCorn(Integer farmerId){
        System.out.println("Farmer: " + farmerId + " colected one cobs.");
        cc.update("Farmer: " + farmerId + " colected one corn.\n");
    }
    
    
    
    public void waitForCollegues(int farmerId) {
        System.out.println("Farmer: " + farmerId + " colected all cobs.");
        cc.update("Farmer: " + farmerId + " colected all corn.\n");
        
        counter ++;
        
        if(counter == nFarmers){
            System.out.println("All Farmers Colected the Cobs");
            cc.farmersWProceed();
            counter = 0;
        }
    }
    
    /**
     * Method called when a farmer stores a Cob.
     * Sends an update message to the Control Center 
     * for each cob stored for each farmer.
     * 
     * @param farmerId Farmer ID.
     */
    public void storeCorn(Integer farmerId) {
        System.out.println("Farmer: " + farmerId + " stored one cobs.");
        cc.update("Farmer: " + farmerId + " stored one corn.\n");
    }
    
    /**
     * Method called to indicate the end of the a Farmer Thread.
     * 
     * @param farmerId Farmer ID.
     */
    public void farmerTerminated(int farmerId) {
        cc.farmerTerminated(farmerId);
    }
    
    /**
     * Method executed when Control Center sends a PrepareFarm message.
     * Calls the prepareSimulation of every monitor resenting multiple variables 
     * to default and setting the variables of the simulation to the values
     * received from the Control Center.
     * 
     * @param nf Number of Farmers.
     * @param to TimeOut of the Farmers.
     * @param ns Number of maximum steps.
     */
    public void prepareFarm(int nf, int to, int ns){
        System.out.println("Preparing Farm.");
       
        nFarmers = nf;
        counter = 0;
        gr.prepareSimulation(to);
        path.prepareSimulation(nf, to, ns);
        sa.prepareSimulation();
        
        System.out.println("Farmers Proceed to Standing Area.");
        sh.prepareSimulation(nf, to);
        
    }
    
    /**
     * Method executed when the Control Center sends a Start.
     * Executes a method on the standing area so that the farmers can proceed.
     * 
     */
    public void startMove(){
        System.out.println("Farmers proceeded to Path.");
        sa.proceedToPath();
    }
    
    /**
     * Method executed when the Control Center sends a Collect.
     * Executes a method on the granary 
     * so that the farmers can start collecting cobs.
     */
    public void startCollection(){
        System.out.println("Farmers start collecting Cobs.");
        gr.allFarmersInGranary();
    }
    
    /**
     * Method executed when the Control Center sends a Return.
     * Executes a method on the granary 
     * so that the farmers can proceed to the path.
     */
    public void returnWCorn(){
        System.out.println("Farmers return with the Cobs.");
        gr.returnToStoreHouse();
    }
    
    /**
     * Method executed when the Control Center sends a Stop.
     * Executes the stopSimulation on every monitor, disabling
     * all logic making farmers go to the beginning of the farm.
     */
    public void stopHarvest(){
        System.out.println("Stop the Harvest.");
        sa.stopSimulation();
        path.stopSimulation();
        gr.stopSimulation();
        sh.stopSimulation();
    }
    
    /**
     * Method executed when the Control Center sends a Exit.
     * Executes the stopSimulation on every monitor, disabling
     * all logic and executes the exit simulation on the Store House, 
     * to signal the farmers threads that they must die.
     * Executes the close method on the server 
     * so that it stops receiving messages.
     */
    public void exitSimulation(){
        FIMain.waitconnection = false;
      
        sa.stopSimulation();
        path.stopSimulation();
        gr.stopSimulation();
        sh.exitSimulation();
        System.out.println("Exit Simulation.");

    }
    
}
