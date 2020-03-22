package Monitors.Interfaces;

/**
 * Interface for the Farmer to access the Path.
 */
public interface IPathF {
    /**
     * Each farmer enters the path and waits for all of his colleague farmers
     * to enter the path.
     * 
     * @param id farmer's thread id.
     * @param toGranary going from the storehouse to the granary, or vice-versa.
     */
    void enterPath(int id, boolean toGranary);
    
    /**
     * Each farmer moves forward, in the path, a random number, lesser or equal
     * to the maximum number, of steps, until it reaches the end of the path.
     * 
     * @param id farmer's thread id.
     * @return true, if he reaches the end of the path, the granary or the 
     *               storehouse, or the simulation stops.
     *         false, otherwise.
     */
    boolean moveForward(int id);
}
