package Monitors;

import FarmInfrastructure.FIController;
import Monitors.Interfaces.IGranaryC;
import Monitors.Interfaces.IGranaryF;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class with the task of controlling the access to the Granary.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class MGranary implements IGranaryC, IGranaryF {
    private Integer collectDuration;
    private final Integer numPositions;
    private int[] positions;
    private final FIController fiController;
    private boolean waitingForAllFarmers;
    private boolean allCorbsCollected;
    private final ReentrantLock rl;
    private final Condition farmerEnteringGranary;
    private final Condition farmerCobsCollected;
    private boolean stopSimulation;
    private int[] cobsCollected;
    private int nCobs;
    
    public MGranary(FIController fiController) {
        this.numPositions = 5;
        this.fiController = fiController;
        this.rl = new ReentrantLock();
        this.farmerEnteringGranary = rl.newCondition();
        this.farmerCobsCollected = rl.newCondition();
        this.cobsCollected = new int[]{0,0,0,0,0};
    }
    
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
    
    @Override
    public void stopSimulation(){
        rl.lock();
        try{
            this.stopSimulation = true;
            farmerEnteringGranary.signalAll();
            farmerCobsCollected.signalAll();
        }
        finally{
            rl.unlock();
        }
    }
    
    @Override
    public void enterGranary(int id) {
        rl.lock();
        
        try {
            if(!stopSimulation){
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
            rl.unlock();
        }
    }
    
    @Override
    public synchronized boolean collectCob(int farmerId) {
        try {
            if(!stopSimulation){
                cobsCollected[farmerId-1] ++;
                Thread.sleep(collectDuration);
                fiController.collectCorn(farmerId);
                if (cobsCollected[farmerId-1] == nCobs){
                    return true;
                }   
            }
            else{
                return true;
            }
        }
        catch (InterruptedException e) {
            System.err.println("ERROR: Farmer was badly interrupted when "
                    + "collecting a cob!");
        }
        return false;
    }
    
    @Override
    public void waitForColleagues(int farmerId) {
        rl.lock();
        try {
            if(!stopSimulation){
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
            rl.unlock();
        }
    }
    
    @Override
    public void returnToStoreHouse() {
        rl.lock();
        
        try {
            allCorbsCollected = true;
            farmerCobsCollected.signalAll();
            fiController.allCobsCollected = true;
        }
        finally {
            rl.unlock();
        }
    }
    
    @Override
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
