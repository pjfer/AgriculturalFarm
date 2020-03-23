package Monitors.Interfaces;

/**
 * Interface for the Farm Controller to access the Standing Area.
 */
public interface IStandingAreaC {
    /**
     * Method called when starting a simulation.
     * Resets the values of the different variables.
     */
    void prepareSimulation();
    
    /**
     * Method called after receiving a start from the CC.
     * Frees all the farmers waiting to proceed to the Path.
     */
    void proceedToPath();
        
        
    /**
     * Method called when the Control Center sends a Stop.
     * It turns the stop flag true and frees every farmer 
     * waiting to move to the path so that they can be reseted.
     */
    void stopSimulation();
}
