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
    private int[] positions;
    private final FIController fiController;
    private boolean waitingForAllFarmers;
    private boolean allCorbsCollected;
    private final ReentrantLock rl;
    private final Condition farmerEnteringGranary;
    private final Condition farmerCobsCollected;
    
    
    public MGranary(FIController fiController) {
        this.collectDuration = 250;
        this.numPositions = 5;
        this.positions = new int[numPositions];
        this.fiController = fiController;
        this.waitingForAllFarmers = true;
        this.allCorbsCollected = false;
        this.rl = new ReentrantLock();
        this.farmerEnteringGranary = rl.newCondition();
        this.farmerCobsCollected = rl.newCondition();
    }
    
    public MGranary(FIController fiController, 
            Integer collectDuration,
            Integer numPositions)
    {
        this.collectDuration = collectDuration;
        this.numPositions = numPositions;
        this.positions = new int[numPositions];
        this.fiController = fiController;
        this.waitingForAllFarmers = true;
        this.allCorbsCollected = false;
        this.rl = new ReentrantLock();
        this.farmerEnteringGranary = rl.newCondition();
        this.farmerCobsCollected = rl.newCondition();
    }
    
    public void prepareSimulation(int to) {
        this.collectDuration = to;
        this.positions = new int[numPositions];
        this.waitingForAllFarmers = true;
        this.allCorbsCollected = false;
    }
    
    public void enterGranary(Integer id) {
        rl.lock();
        
        try {
            Integer position;
            
            do {
                position = (int)(Math.random() * numPositions);
            } while (positions[position] == 1);
            
            positions[position] = 1;
            fiController.moveGranary(id, position);
            
            while (waitingForAllFarmers)
                farmerEnteringGranary.await();
            
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
    
    public void returnToStoreHouse() {
        rl.lock();
        
        try {
            allCorbsCollected = true;
            farmerCobsCollected.signalAll();
            fiController.allCorbsCollected = true;
        }
        finally {
            rl.unlock();
        }
    }
    
    public void allFarmersInGranary() {
        rl.lock();
        
        try {
            waitingForAllFarmers = false;
            farmerEnteringGranary.signalAll();
            fiController.allFarmersInGranary = true;
        }
        finally {
            rl.unlock();
        }
    }
}
