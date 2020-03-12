package Monitors;

import FarmInfrastructure.FIController;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class with the task of controlling the access to shared regions of the 
 * Granary, dealing with the associated concurrence between the farmers.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class MGranary {
    private Integer collectDuration;
    private final Integer numPositions;
    private Integer[] positions;
    private final FIController fiController;
    private boolean waitingForAllFarmers;
    private boolean allCorbsCollected;
    private final ReentrantLock rl;
    private final Condition farmerEnteringGranary;
    private final Condition farmerCobsCollected;
    
    
    public MGranary(FIController fiController) {
        this.collectDuration = 250;
        this.numPositions = 5;
        this.positions = new Integer[numPositions];
        this.fiController = fiController;
        this.waitingForAllFarmers = true;
        this.allCorbsCollected = false;
        this.rl = new ReentrantLock();
        this.farmerEnteringGranary = rl.newCondition();
        this.farmerCobsCollected = rl.newCondition();
    }
    
    public MGranary(FIController fiController, Integer collectDuration,
            Integer numPositions) {
        this.collectDuration = collectDuration;
        this.numPositions = numPositions;
        this.positions = new Integer[numPositions];
        this.fiController = fiController;
        this.waitingForAllFarmers = true;
        this.allCorbsCollected = false;
        this.rl = new ReentrantLock();
        this.farmerEnteringGranary = rl.newCondition();
        this.farmerCobsCollected = rl.newCondition();
    }
    
    public void enterGranary(Integer id) {
        rl.lock();
        
        try {
            while (waitingForAllFarmers)
                farmerEnteringGranary.await();
            
            Integer position = (int)(Math.random() * (numPositions - 1));
            
            while (positions[position] == 1)
                position = (int)(Math.random() * (numPositions - 1));
            
            positions[position] = 1;
            fiController.moveGranary(id, position);
        }
        catch (InterruptedException e) {
            System.err.println("ERROR: Farmer was badly interrupted when "
                    + "entering the Granary!");
        }
        finally {
            rl.unlock();
        }
    }
    
    public synchronized void collectCob() {
        try {
            Thread.sleep(collectDuration);
        }
        catch (InterruptedException e) {
            System.err.println("ERROR: Farmer was badly interrupted when "
                    + "collecting a cob!");
        }
    }
    
    public void waitForColleagues() {
        rl.lock();
        
        try {
            while (!allCorbsCollected)
                farmerCobsCollected.await();
        }
        catch (InterruptedException e) {
            System.err.println("ERROR: Farmer was badly interrupted when "
                    + "waiting for colleagues to collect all the corbs!");
        }
        finally {
            rl.unlock();
        }
    }
    
    public synchronized void allCorbsCollected() {
        allCorbsCollected = true;
        farmerCobsCollected.signalAll();
        fiController.allCorbsCollected = true;
    }
    
    public synchronized void allFarmersInGranary() {
        waitingForAllFarmers = false;
        farmerEnteringGranary.signalAll();
        fiController.allFarmersInGranary = true;
    }
    
    public void prepareSimulation(int to){
        collectDuration = to;
        positions = new Integer[numPositions];
        this.waitingForAllFarmers = true;
        this.allCorbsCollected = false;
    }
}
