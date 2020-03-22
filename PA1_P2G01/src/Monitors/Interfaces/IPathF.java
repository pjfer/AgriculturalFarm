package Monitors.Interfaces;

/**
 * Interface for the Farmer to access the Path.
 */
public interface IPathF {
    /**
     * 
     * @param id
     * @param toGranary 
     */
    void enterPath(int id, boolean toGranary);
    
    /**
     * 
     * @param id
     * @return 
     */
    boolean moveForward(int id);
}
