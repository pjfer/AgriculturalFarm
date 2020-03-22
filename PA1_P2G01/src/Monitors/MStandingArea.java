package Monitors;

import FarmInfrastructure.FIController;
import Monitors.Interfaces.IStandingAreaC;
import Monitors.Interfaces.IStandingAreaF;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Class with the task of controlling the access to the Standing Area.
 */
public class MStandingArea implements IStandingAreaC, IStandingAreaF{
    
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
    
    @Override
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
            System.err.println("ERROR: Farmer " + farmerId + " was unable to "
                    + "enter the standing area!");
        } finally {
            try{
                Thread.sleep(this.randomTimeout());
            } catch (InterruptedException ex) {}
            rl.unlock();
        }
    }
    
    @Override
    public void proceedToPath(){
        rl.lock();
        try{
            /* Turns Flag true so the farmers get unstuck */
            proceed = true;
            
            /* Signal all the farmers */
            proceedPath.signalAll();
        }catch(Exception e){}
        finally{
            try{
                Thread.sleep(this.randomTimeout());
            } catch (InterruptedException ex) {}
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
    

    @Override
    public void prepareSimulation(){
        stopSimulation = false;
        proceed = false;
        farmersPosition = new int[5];
        for(int i = 0; i < totalFarmers; i ++){
            farmersPosition[i] = -1;
        }
    }

    @Override
    public void stopSimulation(){
        rl.lock();
        try{
            stopSimulation = true;
            proceedPath.signalAll();
        }
        finally{
            try{
                Thread.sleep(this.randomTimeout());
            } catch (InterruptedException ex) {}
            rl.unlock();
        }
        
    }
    
    private long randomTimeout(){
        Random rand = new Random();

        int n = rand.nextInt(100);

        n += 1;
        
        return n;
    }
    
}
