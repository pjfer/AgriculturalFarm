package Monitors;

import FarmInfrastructure.FIController;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class with the task of controlling the access to shared regions of the 
 * Path, dealing with the associated concurrence between the farmers.
 *
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class MPath {
    private Integer selected;
    private boolean toGranary;
    private Integer movementTime;
    private Integer numFarmers;
    private final Integer pathLength;
    private Integer numSteps;
    private Integer farmersWaiting;
    private Queue<Integer> order;
    private Map<Integer, Integer[]> positions;
    private final FIController fiController;
    private final ReentrantLock rl;
    private final Condition farmerMoveForward;
    
    public MPath(FIController fiController) {
        this.toGranary = true;
        this.movementTime = 250;
        this.numFarmers = 5;
        this.pathLength = 10;
        this.numSteps = 2;
        this.farmersWaiting = 0;
        this.order = new LinkedList<>();
        this.positions = new HashMap<>();
        this.fiController = fiController;
        this.rl = new ReentrantLock(true);
        this.farmerMoveForward = rl.newCondition();
    }
    
    public MPath(FIController fiController, 
            Integer movementTime, 
            Integer numPositions)
    {
        this.toGranary = true;
        this.movementTime = movementTime;
        this.numFarmers = 5;
        this.pathLength = 10;
        this.numSteps = 2;
        this.farmersWaiting = 0;
        this.order = new LinkedList<>();
        this.positions = new HashMap<>();
        this.fiController = fiController;
        this.rl = new ReentrantLock(true);
        this.farmerMoveForward = rl.newCondition();
    }
    
    public MPath(FIController fiController, 
            Integer movementTime, 
            Integer numFarmers,
            Integer numSteps) 
    {
        this.toGranary = true;
        this.movementTime = movementTime;
        this.numFarmers = numFarmers;
        this.pathLength = 10;
        this.numSteps = numSteps;
        this.farmersWaiting = 0;
        this.order = new LinkedList<>();
        this.positions = new HashMap<>();
        this.fiController = fiController;
        this.rl = new ReentrantLock(true);
        this.farmerMoveForward = rl.newCondition();
    }
    
    public void prepareSimulation(int nf, int to) {
        this.toGranary = true;
        this.numFarmers = nf;
        this.movementTime = to;
        this.farmersWaiting = 0;
        this.order = new LinkedList<>();
        this.positions = new HashMap<>();
    }
    
    public void enterPath(Integer id, boolean toGranary) {
        rl.lock();
        
        try {
            this.toGranary = toGranary;
            Integer[] position;
            
            if (toGranary)
                position = this.getPositionToGranary(new Integer[] {-1, -1});
            else
                position = this.getPositionToStoreHouse(new Integer[] {-1, -1});
            
            positions.put(id, position);
            fiController.movePath(id, position);
            farmersWaiting++;
            
            if (Objects.equals(farmersWaiting, numFarmers)) {
                farmersWaiting = 0;
                farmerMoveForward.signal();
                farmerMoveForward.await();
            }
            
            while (!Objects.equals(farmersWaiting, 0))
                farmerMoveForward.await();
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
            Integer[] position;
            
            if (toGranary)
                position = this.getPositionToGranary(positions.get(id));
            else
                position = this.getPositionToStoreHouse(positions.get(id));
            
            Integer[] outsidePosition = { -1, -1 };

            if (!Arrays.equals(position, outsidePosition)) {
                positions.put(id, position);
                fiController.movePath(id, position);
                Thread.sleep(movementTime);
                farmerMoveForward.signal();
                selected = order.peek();
                order.add(id);
                
                while (!Objects.equals(selected, id))
                    farmerMoveForward.await();
                
                order.remove();
                
                return false;
            }
            
            if (!order.isEmpty()) {
                selected = order.peek();
                farmerMoveForward.signal();
            }
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
    
    private Integer[] getPositionToGranary(Integer[] prevPosition) {
        Integer[] newPosition = prevPosition.clone();
        
        if (newPosition[0] == -1 && newPosition[1] == -1) {
            newPosition[0] = 0;
            
            do {
                newPosition[1] = (int)(Math.random() * numFarmers);
            } while(positionTaken(newPosition));
        }
        else {
            newPosition[0] = prevPosition[0] + 
                    (int)(Math.random() * numSteps + 1);
            
            if (newPosition[0] < pathLength) {
                do {
                    newPosition[1] = (int)(Math.random() * numFarmers);
                } while(positionTaken(newPosition));
            }
            else {
                newPosition[0] = -1;
                newPosition[1] = -1;
            }
        }
        
        return newPosition;
    }
    
    private Integer[] getPositionToStoreHouse(Integer[] prevPosition) {
        Integer[] newPosition = prevPosition.clone();
        
        if (newPosition[0] == -1 && newPosition[1] == -1) {
            newPosition[0] = pathLength - 1;
            
            do {
                newPosition[1] = (int)(Math.random() * numFarmers);
            } while(positionTaken(newPosition));
        }
        else {
            newPosition[0] = prevPosition[0] - 
                    (int)(Math.random() * numSteps + 1);
            
            if (newPosition[0] >= 0) {
                do {
                    newPosition[1] = (int)(Math.random() * numFarmers);
                } while(positionTaken(newPosition));
            }
            else {
                newPosition[0] = -1;
                newPosition[1] = -1;
            }
        }
        
        return newPosition;
    }
    
    private boolean positionTaken(Integer[] position) {
        return positions.values().stream().anyMatch((posTaken) -> 
                (Arrays.equals(position, posTaken)));
    }
}
