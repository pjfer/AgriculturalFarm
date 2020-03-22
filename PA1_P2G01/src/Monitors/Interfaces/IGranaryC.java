package Monitors.Interfaces;

/**
 * Interface for the Farm Controller to access the Granary.
 */
public interface IGranaryC {
    /**
     * 
     * @param to
     * @param nCobs 
     */
    void prepareSimulation(int to, int nCobs);
    
    /**
     * 
     */
    void allFarmersInGranary();
    
    /**
     * 
     */
    void returnToStoreHouse();
    
    /**
     * 
     */
    void stopSimulation();
}
