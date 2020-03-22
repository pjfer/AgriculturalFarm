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
 */
public class MPath implements IPathC, IPathF {
    /**
     * Represents the first element (head) of the order queue.
     */
    private Integer selected;
    
    /**
     * Indicates if the farmers, in the path, are going from the storehouse 
     * to the granary, or vice-versa.
     */
    private boolean toGranary;
    
    /**
     * Time needed for each farmer to take steps in the path.
     */
    private Integer movementTime;
    
    /**
     * Total number of farmers that are going to harvest.
     */
    private Integer numFarmers;
    
    /**
     * Size of the path.
     */
    private final Integer pathLength;
    
    /**
     * Maximum number of steps that each farmer can do in the path.
     */
    private Integer numSteps;
    
    /**
     * Total number of farmers waiting when entering the path.
     */
    private Integer farmersWaiting;
    
    /**
     * Holds the order of which the farmers entered the path.
     */
    private Queue<Integer> order;
    
    /**
     * Holds the last position associated to each farmer.
     */
    private Map<Integer, Integer[]> positions;
    
    /**
     * Communication manager with the farm infrastructure GUI.
     */
    private final FIController fiController;
    
    /**
     * Synchronization mediator for accessing methods with shared resources.
     */
    private final ReentrantLock rl;
    
    /**
     * Thread execution mediator for accessing methods with shared resources.
     * Make the farmers wait for each other when moving in the path.
     */
    private final Condition farmerMoveForward;
    
    /**
     * Indicates if the simulation stopped.
     */
    private boolean stopSimulation = false;
    
    /**
     * Instantiation of the path with the default number of farmers, time to 
     * move and maximum number of steps.
     * 
     * @param fiController communication manager with farm infrastructure GUI.
     */
    public MPath(FIController fiController) {
        this.pathLength = 10;
        this.fiController = fiController;
        this.rl = new ReentrantLock(true);
        this.farmerMoveForward = rl.newCondition();
    }
    
    /**
     * Instantiation of the path with the custom number of farmers, time to 
     * move and maximum number of steps.
     * 
     * @param nf total number of farmers that are going to harvest.
     * @param to time needed for each farmer to collect a corn cob.
     * @param ns maximum number of steps that each farmer can do in the path.
     */
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
    
    /**
     * Signal all the farmers that are awaiting, to stop the simulation.
     */
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
    
    /**
     * Each farmer enters the path and waits for all of his colleague farmers
     * to enter the path.
     * 
     * @param id farmer's thread id.
     * @param toGranary going from the storehouse to the granary, or vice-versa.
     */
    @Override
    public void enterPath(int id, boolean toGranary) {
        rl.lock();
        
        try {
            if (!stopSimulation) {
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
    
    /**
     * Each farmer moves forward, in the path, a random number, lesser or equal
     * to the maximum number, of steps, until it reaches the end of the path.
     * 
     * @param id farmer's thread id.
     * @return true, if he reaches the end of the path, the granary or the 
     *               storehouse, or the simulation stops.
     *         false, otherwise.
     */
    @Override
    public boolean moveForward(int id) {
        rl.lock();
        
        try {
            if (!stopSimulation) {
                Integer[] position;
                Integer[] outsidePosition = { -1, -1 };
                
                if (toGranary)
                    position = this.getPositionToGranary(positions.get(id));
                else
                    position = this.getPositionToStoreHouse(positions.get(id));
                
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
    
    /**
     * Get a new position in the path, based on the previous one, from the 
     * storehouse to the granary.
     * 
     * @param prevPosition array with the x and y coordinates on the path.
     * @return newPosition in the path.
     */
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
    
    /**
     * Get a new position in the path, based on the previous one, from the 
     * granary to the storehouse.
     * 
     * @param prevPosition array with the x and y coordinates on the path.
     * @return newPosition in the path.
     */
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
    
    /**
     * Verify if the new position is already occupied by another farmer.
     * 
     * @param position array with the x and y coordinates on the path.
     * @return true, if the position is already taken.
     *         false, otherwise.
     */
    private boolean positionTaken(Integer[] position) {
        return positions.values().stream().anyMatch((posTaken) -> 
                (Arrays.equals(position, posTaken)));
    }
    
}
