package Monitors;

import FarmInfrastructure.FIServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class with the task of controlling the access to shared regions of the 
 * Path, dealing with the associated concurrence between the farmers.
 *
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class MPath {
    private final Integer movementTime;
    private final Integer numPositions;
    private Map<Integer, Integer[]> positions;
    private ArrayList<Integer> order;
    private final FIServer fi;
    private final ReentrantLock rl;
    
    public MPath() {
        this.movementTime = 250;
        this.numPositions = 5;
        this.positions = new HashMap<>();
        this.order = new ArrayList<>();
        this.fi = new FIServer();
        this.rl = new ReentrantLock();
    }
    
    public MPath(FIServer fi) {
        this.movementTime = 250;
        this.numPositions = 5;
        this.positions = new HashMap<>();
        this.order = new ArrayList<>();
        this.fi = fi;
        this.rl = new ReentrantLock();
    }
    
    public MPath(FIServer fi, Integer movementTime, Integer numPositions) {
        this.movementTime = movementTime;
        this.numPositions = numPositions;
        this.positions = new HashMap<>();
        this.order = new ArrayList<>();
        this.fi = fi;
        this.rl = new ReentrantLock();
    }
    
    public boolean moveForward(Integer id) {
        rl.lock();
        
        try {
            Integer[] position = positions.get(id);
            Integer[] outsideCoordinates = {-1, -1};

            if (position != outsideCoordinates) {
                positions.put(id, position);
                fi.movePath(id, position);
                Thread.sleep(movementTime);
                
                if (!order.isEmpty()) {
                    
                }
            }
        }
        catch (InterruptedException e) {
            System.err.println();
        }
        finally {
            rl.unlock();
        }
    }
}
