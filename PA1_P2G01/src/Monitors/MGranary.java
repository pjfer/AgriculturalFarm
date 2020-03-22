package Monitors;

import FarmInfrastructure.FIController;
import Monitors.Interfaces.IGranaryC;
import Monitors.Interfaces.IGranaryF;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class with the task of controlling the access to the Granary.
 */
public class MGranary implements IGranaryC, IGranaryF {
    /**
     * Time needed for each farmer to collect a corn cob.
     */
    private Integer collectDuration;
    
    /**
     * Total number of farmers that are going to collect.
     */
    private final Integer numPositions;
    
    /**
     * Array with the occupied positions in the granary.
     */
    private int[] positions;
    
    /**
     * Communication manager with the farm infrastructure GUI.
     */
    private final FIController fiController;
    
    /**
     * Indicates if all farmers are already inside the granary.
     */
    private boolean waitingForAllFarmers;
    
    /**
     * Indicates if all farmers already collected all the corn cobs.
     */
    private boolean allCorbsCollected;
    
    /**
     * Synchronization mediator for accessing methods with shared resources.
     */
    private final ReentrantLock rl;
    
    /**
     * Thread execution mediator for accessing methods with shared resources.
     * Make the farmers wait for each other when entering the granary.
     */
    private final Condition farmerEnteringGranary;
    
    /**
     * Thread execution mediator for the methods with shared resources.
     * Make the farmer wait for the other farmers when he finishes to collect
     * all of his corn cobs.
     */
    private final Condition farmerCobsCollected;
    
    /**
     * Indicates if the simulation stopped.
     */
    private boolean stopSimulation;
    
    /**
     * Array with the number of corn cobs collected by each farmer.
     */
    private int[] cobsCollected;
    
    /**
     * Total number of corn cobs that each farmer needs to get.
     */
    private int nCobs;
    
    /**
     * Instantiation of the granary with the default time to collect and total
     * number of corn cobs.
     * 
     * @param fiController communication manager with farm infrastructure GUI.
     */
    public MGranary(FIController fiController) {
        this.numPositions = 5;
        this.fiController = fiController;
        this.rl = new ReentrantLock();
        this.farmerEnteringGranary = rl.newCondition();
        this.farmerCobsCollected = rl.newCondition();
        this.cobsCollected = new int[]{0,0,0,0,0};
    }
    
    /**
     * Instantiation of the granary with the custom time to collect and total
     * number of corn cobs.
     * 
     * @param to time needed for each farmer to collect a corn cob.
     * @param nCobs total number of corn cobs that each farmer needs to get.
     */
    @Override
    public void prepareSimulation(int to, int nCobs) {
        this.collectDuration = to;
        this.positions = new int[numPositions];
        this.waitingForAllFarmers = true;
        this.allCorbsCollected = false;
        this.stopSimulation = false;
        this.cobsCollected = new int[]{0,0,0,0,0};
        this.nCobs = nCobs;
    }
    
    /**
     * Signal all the farmers that are awaiting, to stop the simulation.
     */
    @Override
    public void stopSimulation(){
        rl.lock();
        
        try {
            stopSimulation = true;
            farmerEnteringGranary.signalAll();
            farmerCobsCollected.signalAll();
        }
        finally {
            try{
                Thread.sleep(this.randomTimeout());
            } catch (InterruptedException ex) {}
            rl.unlock();
        }
    }
    
    /**
     * Each farmer enters the granary and waits for all of his colleague farmers
     * to enter the granary.
     * 
     * @param id farmer's thread id.
     */
    @Override
    public void enterGranary(int id) {
        rl.lock();
        
        try {
            if (!stopSimulation) {
                Integer position;

                do {
                    position = (int)(Math.random() * numPositions);
                    System.out.println("stuck");
                } while (positions[position] == 1);

                positions[position] = 1;
                fiController.moveGranary(id, position);

                while (waitingForAllFarmers && !stopSimulation)
                    farmerEnteringGranary.await();
            }
            
        }
        catch (InterruptedException e) {
            System.err.println("ERROR: Farmer was badly interrupted when "
                    + "entering the Granary!");
        }
        finally {
            try{
                Thread.sleep(this.randomTimeout());
            } catch (InterruptedException ex) {}
            rl.unlock();
        }
    }
    
    /**
     * Each farmer collects one corn cob at a time.
     * 
     * @param farmerId farmer's thread id.
     * @return true, if the farmer collects all of his corn cobs, or the 
     *               simulation stops.
     *         false, otherwise.
     */
    @Override
    public synchronized boolean collectCob(int farmerId) {
        try {
            if (!stopSimulation) {
                cobsCollected[farmerId - 1]++;
                Thread.sleep(collectDuration);
                fiController.collectCorn(farmerId);
                
                if (cobsCollected[farmerId - 1] == nCobs) {
                    return true;
                }   
            }
            else {
                return true;
            }
        }
        catch (InterruptedException e) {
            System.err.println("ERROR: Farmer was badly interrupted when "
                    + "collecting a cob!");
        }
        return false;
    }
    
    /**
     * Each farmer collects all of his corn cobs and waits for all of his 
     * colleague farmers to also collect all of theirs corn cobs.
     * 
     * @param farmerId farmer's thread id.
     */
    @Override
    public void waitForColleagues(int farmerId) {
        rl.lock();
        
        try {
            if (!stopSimulation) {
                fiController.waitForCollegues(farmerId);
                
                while (!allCorbsCollected && !stopSimulation)
                    farmerCobsCollected.await();
            }
        }
        catch (InterruptedException e) {
            System.err.println("ERROR: Farmer was badly interrupted when "
                    + "waiting for colleagues to collect all the corbs!");
        }
        finally {
            try{
                Thread.sleep(this.randomTimeout());
            } catch (InterruptedException ex) {}
            rl.unlock();
        }
    }
    
    /**
     * After all the farmers collect all of theirs corn cobs, 
     * return all the farmers to the storehouse.
     */
    @Override
    public void returnToStoreHouse() {
        rl.lock();
        
        try {
            allCorbsCollected = true;
            farmerCobsCollected.signalAll();
            fiController.allCobsCollected = true;
        }
        finally {
            try{
                Thread.sleep(this.randomTimeout());
            } catch (InterruptedException ex) {}
            rl.unlock();
        }
    }
    
    /**
     * After all the farmers enter the granary, make all of them ready to start
     * collect the corn cobs.
     */
    @Override
    public void allFarmersInGranary() {
        rl.lock();
        
        try {
            waitingForAllFarmers = false;
            farmerEnteringGranary.signalAll();
            fiController.allFarmersInGranary = true;
        }
        finally {
            try{
                Thread.sleep(this.randomTimeout());
            } catch (InterruptedException ex) {}
            rl.unlock();
        }
    }
    
    /**
     * Get the random time, between 0 and 100, to put the thread to sleep.
     * 
     * @return random number between 0 and 100.
     */
    private long randomTimeout(){
        return new Random().nextInt(101);
    }
}
