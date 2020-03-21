package Monitors;

import FarmInfrastructure.FIController;
import Monitors.Interfaces.IPathC;
import Monitors.Interfaces.IPathF;
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
public class MPath implements IPathC, IPathF{
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
    private boolean stopSimulation = false;
    
    public MPath(FIController fiController) {
        this.pathLength = 10;
        this.fiController = fiController;
        this.rl = new ReentrantLock(true);
        this.farmerMoveForward = rl.newCondition();
    }
    
    @Override
    public void prepareSimulation(int nf, int to, int ns) {
        this.toGranary = true;
        this.numFarmers = nf;
        this.movementTime = to;
        this.farmersWaiting = 0;
        this.order = new LinkedList<>();
        this.positions = new HashMap<>();
        this.numSteps = ns;
        this.selected = -1;
        this.stopSimulation = false;
    }
    
    @Override
    public void stopSimulation(){
        rl.lock();
        try{
            stopSimulation = true;
            farmerMoveForward.signalAll(); 
        }
        finally {
            rl.unlock();
        }
    }
    
    @Override
    public void enterPath(int id, boolean toGranary) {
        rl.lock();
        
        try {
            if(!stopSimulation){
                this.toGranary = toGranary;
                Integer[] position;

                if (toGranary)
                    position = this.getPositionToGranary(new Integer[] {-1, -1});
                else
                    position = this.getPositionToStoreHouse(new Integer[] {-1, -1});

                positions.put(id, position);
                fiController.movePath(id, position);
                farmersWaiting++;
                Thread.sleep(movementTime);
                while (!Objects.equals(farmersWaiting, 0) && !stopSimulation) {
                    if (Objects.equals(farmersWaiting, numFarmers) 
                            && !stopSimulation) {
                        farmersWaiting = 0;
                        farmerMoveForward.signal();
                    }

                    farmerMoveForward.await();
                }
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
    
    @Override
    public boolean moveForward(int id) {
        rl.lock();
        try {
            if(!stopSimulation){
                Integer[] position;
                if (toGranary)
                    position = this.getPositionToGranary(positions.get(id));
                else
                    position = this.getPositionToStoreHouse(positions.get(id));
                Integer[] outsidePosition = { -1, -1 };
                if (!Arrays.equals(position, outsidePosition) 
                        && !stopSimulation) {
                    positions.put(id, position);
                    fiController.movePath(id, position);
                    Thread.sleep(movementTime);
                    if (!Objects.equals(farmersWaiting, numFarmers - 1)) {
                        farmerMoveForward.signal();
                        selected = order.peek();
                        order.add(id);
                        while (!Objects.equals(selected, id) 
                                && !stopSimulation)
                            farmerMoveForward.await();
                        order.remove();
                    }
                    return false;
                }
                if (!order.isEmpty()) {
                    farmersWaiting++;
                    selected = order.peek();
                    farmerMoveForward.signal();
                }
                else {
                    farmersWaiting = 0;
                    positions.clear();
                }
                return true;
            }
            else{
                return true;
            }
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
