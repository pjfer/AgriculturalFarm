package Monitors.Interfaces;

/**
 * Interface for the Farm Controller to access the Store House.
 *
 * @author Rafael Teixeira e Pedro Ferreira
 */
public interface IStoreHouseC {
    
    /**
     * Method called when starting a simulation.
     * Resets the values of the different variables.
     * 
     * @param nf Number of Farmers.
     * @param to Time a Farmer takes to deposit a cob.
     * @param nCobs Number of cobs to be deposited.
     */
    void prepareSimulation(int nf, int to, int nCobs);
    
    /**
     * Method called when the Control Center sends a Stop.
     * It turns the stop flag true and frees every farmer 
     * waiting to move to the path so that they can be reseted.
     */
    void stopSimulation();
    
    /**
     * Method called when the Control Center sends a Exit.
     * It turns the stop flag and the exit flag true and frees every farmer 
     * waiting to move to the standing area so that they end their execution.
     */
    void exitSimulation();
    
}
