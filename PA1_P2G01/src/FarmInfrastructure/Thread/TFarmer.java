package FarmInfrastructure.Thread;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;

/**
 * Class that represents a farmer.
 * 
 * @author Rafael Teixeira e Pedro Ferreira
 */
public class TFarmer extends Thread{
    
    /**
     *  Farmer Id.
     */
    private final int id;
    
    /**
     * Monitor used to Control the Granary.
     */
    private final MGranary granary;
    
    /**
     * Monitor used to Control the Path.
     */
    private final MPath path;
    
    /**
     * Monitor used to Control the Store House.
     */
    private final MStoreHouse sh;
    
    /**
     * Monitor used to Control the Standing Area.
     */
    private final MStandingArea sa;
    
    /**
     * Default Constructor.
     * 
     * @param id Farmer Id.
     * @param granary Monitor used to Control the Granary.
     * @param path Monitor used to Control the Path.
     * @param sh Monitor used to Control the Store House.
     * @param sa Monitor used to Control the Standing Area.
     */ 
    public TFarmer(int id, MGranary granary, MPath path,
            MStoreHouse sh, MStandingArea sa){
        
        this.id = id;
        this.granary = granary;
        this.path = path;
        this.sh = sh;
        this.sa = sa;
        
    }
    
    @Override
    /**
     * Standard Run method of a thread, executes the farmer logic.
     */
    public void run(){
        /**
         * Flag that signals that a farmer reached the end of a task.
         */
        boolean has_finished = false;
        /**
         * Flag that signals that the farmer should die.
         */
        boolean exit = false;
        
        
        /* First time entering the Store House */
        sh.enterSH(id);
        
        /* Farmer Logic. */
        while(!exit){
            /*After entering the store house, waits the start of the simulation.*/
            sh.startSimulation(id);
            
            /*Enters the standing area.*/
            sa.enterSA(id);
            
            /*Enters Path.*/
            path.enterPath(id, true);
            
            /* Travels the path until it reaches the end.*/
            while(!has_finished){
                has_finished = path.moveForward(id);
                
            }
            has_finished = false;
            
            /* Enters the granary. */
            granary.enterGranary(id);
            
            /* Collects the cobs. */
            while(!has_finished){
                has_finished = granary.collectCob(id);
            }
            has_finished = false;
            /* Waits for the proceed signal. */
            granary.waitForColleagues(id);
            
            /* Enters Path again*/
            path.enterPath(id, false);
            
            /* Travels the path until it reaches the end.*/
            while(!has_finished){
                has_finished = path.moveForward(id);
            }
            has_finished = false;
            
            /*Enters the store house again and checks if it has to die.*/
            exit = sh.enterSH(id);
            /*Deposits the corn cobs.*/
            while(!has_finished){
                has_finished = sh.depositCorn(id);
            }
            has_finished = false;
        }
        
    }
    
}
