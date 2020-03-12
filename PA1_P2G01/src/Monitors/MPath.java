package Monitors;

import FarmInfrastructure.FIServer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class with the task of controlling the access to shared regions of the 
 * Path, dealing with the associated concurrence between the farmers.
 *
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class MPath {
    private final Integer movementTime;
    private final Integer numFarmers;
    private final Integer pathLength;
    private Integer numSteps;
    private Integer farmersWaiting;
    private final Map<Integer, Integer[]> positions;
    private final FIServer fi;
    private final ReentrantLock rl;
    private final Condition farmerProceed;
    private final Condition farmerMoveForward;
    
    public MPath() {
        this.movementTime = 250;
        this.numFarmers = 5;
        this.pathLength = 10;
        this.numSteps = 2;
        this.farmersWaiting = 0;
        this.positions = new HashMap<>();
        this.fi = new FIServer();
        this.rl = new ReentrantLock(true);
        this.farmerProceed = rl.newCondition();
        this.farmerMoveForward = rl.newCondition();
    }
    
    public MPath(FIServer fi) {
        this.movementTime = 250;
        this.numFarmers = 5;
        this.pathLength = 10;
        this.numSteps = 2;
        this.farmersWaiting = 0;
        this.positions = new HashMap<>();
        this.fi = fi;
        this.rl = new ReentrantLock(true);
        this.farmerProceed = rl.newCondition();
        this.farmerMoveForward = rl.newCondition();
    }
    
    public MPath(FIServer fi, Integer movementTime) {
        this.movementTime = movementTime;
        this.numFarmers = 5;
        this.pathLength = 10;
        this.numSteps = 2;
        this.farmersWaiting = 0;
        this.positions = new HashMap<>();
        this.fi = fi;
        this.rl = new ReentrantLock(true);
        this.farmerProceed = rl.newCondition();
        this.farmerMoveForward = rl.newCondition();
    }
    
    public MPath(FIServer fi, 
            Integer movementTime, 
            Integer numFarmers,
            Integer numSteps) 
    {
        this.movementTime = movementTime;
        this.numFarmers = numFarmers;
        this.pathLength = 10;
        this.numSteps = numSteps;
        this.farmersWaiting = 0;
        this.positions = new HashMap<>();
        this.fi = fi;
        this.rl = new ReentrantLock(true);
        this.farmerProceed = rl.newCondition();
        this.farmerMoveForward = rl.newCondition();
    }
    
    public void enterPath(Integer id, boolean forward) {
        rl.lock();
        
        try {
            Integer[] position = this.getPosition(new Integer[] {-1, -1});
            fi.movePath(id, position);
            farmersWaiting++;
            
            while (!Objects.equals(farmersWaiting, numFarmers))
                farmerProceed.await();
            
            if (Objects.equals(farmersWaiting, numFarmers)) {
                farmersWaiting = 0;
                farmerProceed.signalAll();
            }
        }
        catch (InterruptedException e) {
            System.err.println("ERROR: Farmer was badly interrupted when was "
                    + "entering the Path!");
        }
        finally {
            rl.unlock();
        }
        
    }
    
    public boolean moveForward(Integer id) {
        rl.lock();
        
        try {
            Integer[] position = this.getPosition(positions.get(id));
            Integer[] outsideCoordinates = {-1, -1};

            if (position != outsideCoordinates) {
                positions.put(id, position);
                fi.movePath(id, position);
                Thread.sleep(movementTime);
                
                while (rl.getHoldCount() != numFarmers)
                    farmerMoveForward.await();
                
                if (rl.getHoldCount() == numFarmers)
                    farmerMoveForward.signalAll();
                
                return false;
            }
            
            if (rl.getHoldCount() != 0)
                farmerMoveForward.signal();
            else
                positions.clear();
                
            return true;
        }
        catch (InterruptedException e) {
            System.err.println("ERROR: Farmer was badly interrupted when "
                    + "moving forward in the Path!");
        }
        finally {
            rl.unlock();
        }
        
        return false;
    }
    
    private Integer[] getPosition(Integer[] prevPosition) {
        Integer[] newPosition = prevPosition.clone();
        
        if (!Objects.equals(newPosition[0], pathLength)) {
            newPosition[0] += (int)(Math.random() * numSteps + 1);
            newPosition[1] = (int)(Math.random() * (numFarmers - 1));
        }
        else {
            newPosition[0] = -1;
            newPosition[1] = -1;
        }
        
        return newPosition;
    }
}
