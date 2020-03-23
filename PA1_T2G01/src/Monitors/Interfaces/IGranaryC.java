package Monitors.Interfaces;

/**
 * Interface for the Farm Infrastructure Controller to access the Granary.
 */
public interface IGranaryC {
    /**
     * Instantiation of the granary with the custom time to collect and total
     * number of corn cobs.
     * 
     * @param to time needed for each farmer to collect a corn cob.
     * @param nCobs total number of corn cobs that each farmer needs to get.
     */
    void prepareSimulation(int to, int nCobs);
    
    /**
     * After all the farmers enter the granary, make all of them ready to start
     * collect the corn cobs.
     */
    void allFarmersInGranary();
    
    /**
     * After all the farmers collect all of theirs corn cobs, 
     * return all the farmers to the storehouse.
     */
    void returnToStoreHouse();
    
    /**
     * Signal all the farmers that are awaiting, to stop the simulation.
     */
    void stopSimulation();
}
