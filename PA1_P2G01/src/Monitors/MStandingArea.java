package Monitors;

import FarmInfrastructure.FIController;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class with the task of controlling the access to the Standing Area.
 * 
 * @author Rafael Teixeira e Pedro Ferreira
 */
public class MStandingArea {
    
    /**
     * Farm Interface Controller.
     */
    FIController fiController;
    
    /**
     * Flag that avoids farmers proceeding from random signals.
     */
    boolean proceed;
    
    /**
     * Lock that controls the concurrent access to the Standing Area.
     */
    ReentrantLock rl;
    
    /**
     * Condition that blocks the farmers 
     * until the Control Center presses Start.
     */
    Condition proceedPath;
    
    /**
     * Number of total farmers in the simulation.
     */
    private final int totalFarmers = 5;
    
    /**
     * Farmers position in the Standing Area.
     */
    private int[] farmersPosition;
    
    /**
     * Flag that indicates that the simulation has been stopped.
     */
    private boolean stopSimulation = false;
    
    /**
     * Default Constructor
     * 
     * @param fiController Farm Interface Controller.
     */
    public MStandingArea(FIController fiController){
        this.fiController = fiController;
        proceed = false;
        rl = new ReentrantLock();
        proceedPath = rl.newCondition();
        farmersPosition = new int[5];
        for(int i = 0; i < totalFarmers; i ++){
            farmersPosition[i] = -1;
        }
        
    }
    
    /**
     * Method called by a farmer to enter the Standing Area.
     * @param farmerId FarmerId.
     */
    public void enterSA(int farmerId){
        rl.lock();
        
        try{
            /*Verifies if simulation has stoped */
            if(!stopSimulation){
                /*Selects a position on the Standing Area */
                int position = this.selectPosition(farmerId);
                
                /*Notifies the Control Center */
                fiController.farmerStanding(farmerId, position);
                
                /* Waits the proceed message from the Control Center */
                while(!proceed && !stopSimulation){
                    proceedPath.await();
                }
                farmersPosition[position] = -1;
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(MStandingArea.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Method called after receiving a start from the CC.
     * Frees all the farmers waiting to proceed to the Path.
     */
    public void proceedToPath(){
        rl.lock();
        try{
            /* Turns Flag true so the farmers get unstuck */
            proceed = true;
            
            /* Signal all the farmers */
            proceedPath.signalAll();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }
    
    /**
     * Internal method to select a position in the standing area.
     * 
     * @param farmerId Farmer Id
     * @return Returns the position taken by the farmer.
     */
    private int selectPosition(int farmerId) {
        
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
     * Method called when starting a simulation.
     * Resets the values of the different variables.
     */
    public void prepareSimulation(){
        stopSimulation = false;
        proceed = false;
        farmersPosition = new int[5];
        for(int i = 0; i < totalFarmers; i ++){
            farmersPosition[i] = -1;
        }
    }
    
    /**
     * Method called when the Control Center sends a Stop.
     * It turns the stop flag true and frees every farmer 
     * waiting to move to the path so that they can be reseted.
     */
    public void stopSimulation(){
        rl.lock();
        try{
            stopSimulation = true;
            proceedPath.signalAll();
        }
        finally{
            rl.unlock();
        }
        
    }
    
    
    
}
