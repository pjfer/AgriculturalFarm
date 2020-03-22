package Monitors;

import FarmInfrastructure.FIController;
import Monitors.Interfaces.IStoreHouseC;
import Monitors.Interfaces.IStoreHouseF;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class with the task of controlling the access to the Store House.
 */
public class MStoreHouse implements IStoreHouseC, IStoreHouseF {
    
    /**
     * Farm Interface Controller.
     */
    private final FIController fiController;
    
    /**
     * Lock that controls the concurrent access to the Store House.
     */
    private final ReentrantLock rl;
    
    /**
     * Number of Farmers that can proceed to the Standing Area.
     */
    private int fToRelease;
    
    /**
     * Number of farmers used in the simulation.
     */
    private int nFarmers;
    
    /**
     * Condition that blocks the farmers 
     * until the Control Center sends a prepare.
     */
    private final Condition waitStart;
    
    /**
     * Sleep time of a farmer while he deposits the cobs.
     */
    private int depositDurantion;
    
    /**
     * Farmers position in the Store House.
     */
    private final int farmersPosition[];
    
    /**
     * Total number of Farmers.
     */
    private final int totalFarmers = 5;
    
    /**
     * Flag that indicates that the simulation has been stopped.
     */
    private boolean stopSimulation;
    
    /**
     * Flag that indicates that the simulation must end.
     */
    private boolean exitSimulation;
    
    /**
     * Stores the number of cobs deposited for each farmer.
     */
    private int[] cobsDeposited;
    
    /**
     * Number of cobs a farmer must deposit.
     */
    private int nCobs;
    
    /**
     * Default Constructor.
     * 
     * @param fiController Farm Interface Controller.
     */
    public MStoreHouse(FIController fiController){
        this.fiController = fiController;
        this.rl = new ReentrantLock();
        this.fToRelease = 0;
        waitStart = rl.newCondition();
        farmersPosition = new int[totalFarmers];
        for(int i = 0; i < totalFarmers; i ++){
            farmersPosition[i] = -1;
        }
    }
    

    @Override
    public void prepareSimulation(int nf, int to, int nCobs){
        rl.lock();
        try{
            this.nFarmers = nf;
            this.depositDurantion = to;
            fToRelease = nFarmers;
            this.stopSimulation = false;
            this.exitSimulation = false;
            this.cobsDeposited = new int[]{0,0,0,0,0};
            this.nCobs = nCobs;
            waitStart.signalAll();
        }
        finally{
            rl.unlock();
        }
    }
    

    @Override
    public void stopSimulation(){
        rl.lock();
        try {
            this.stopSimulation = true;
        } finally {
            rl.unlock();
        }
        
    }
    

    @Override
    public void exitSimulation(){
        rl.lock();
        try {
            this.exitSimulation = true;
            this.stopSimulation = true;
            waitStart.signalAll();
        } finally {
            rl.unlock();
        }
    }
      
    @Override
    public void startSimulation(int farmerId){
        rl.lock();
        try{
            /* Verifies if the no more simulations will be done. */
            if(!exitSimulation){
                /*Signal the Control Center that he is awaiting.*/
                fiController.farmerAwaiting(farmerId);
                
                /* Waits the signal from the Control Center. */
                while(fToRelease == 0 && !exitSimulation){
                    waitStart.await();
                }
                /* Reduces the number of farmers released. */
                fToRelease --;
                /* Releases the position ocupied by the farmer */
                this.releasePosition(farmerId);
            }
        }
        catch(InterruptedException Ex){}
        finally{
            rl.unlock();
        }
    }
    

    @Override
    public synchronized boolean enterSH(int farmerId){
        /* Verifies if more simulations will be done. */
        if(!exitSimulation){
            /* Selects a position from the Store House */
            int position = this.selectPosition(farmerId);
            
            /* Updates the Control Center on the farmer status. */
            fiController.farmerEnterSH(farmerId, position);
        }
        
        return this.exitSimulation;
    }
    

    @Override
    public synchronized boolean depositCorn(int farmerId){
        try {
            /* Verifies if the simulation was stoped */
            if(!stopSimulation){
                /* Adds a cob to the deposited cobs */
                cobsDeposited[farmerId-1] ++;
                
                /* Time that a deposit takes. */
                Thread.sleep(depositDurantion);
                
                /* Updates the farmer status on the Control Center */
                fiController.storeCorn(farmerId);
                
                /* Verifies if all cobs have been deposited */
                if (cobsDeposited[farmerId-1] == nCobs){
                    return true;
                }  
            }
            else{
                return true;
            }
        } catch (InterruptedException ex) {
            System.err.println("ERROR: Farmer " + farmerId + " was unable to "
                    + "deposit the corn cob!");
        }
        return false;
    }
    
    /**
     * Internal Method to select a free position in the Store House.
     * 
     * @param farmerId Farmer ID.
     * @return Returns the position taken by the farmer.
     */
    private int selectPosition(int farmerId){
        /* Generates a random position */
        int position = (int) Math.round((Math.random() * (totalFarmers - 1)));
        
        /* Verifies if the position is or not ocupied */
        while(farmersPosition[position] != -1){
            /* If ocupied generates a new random position */
            position = (int) Math.round((Math.random() * (totalFarmers - 1)));
        }
        /* Atributes the free position to the farmer */
        farmersPosition[position] = farmerId;
        return position;
    }
    
    /**
     * Internal Method used to free the position occupied by a farmer.
     * 
     * @param farmerId Farmer ID.
     */
    private void releasePosition(int farmerId){
        /* Searches for the farmerID. */
        for(int i = 0; i < totalFarmers; i++){
            if(farmersPosition[i] == farmerId){
                /* Replaces the farmer ID by the default value */
                farmersPosition[i] = -1;
            }
        }
    }
    
    
        
}
