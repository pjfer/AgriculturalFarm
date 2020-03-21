package Monitors.Interfaces;

/**
 * Interface for the Farmer to access the Path.
 *
 * @author Rafael Teixeira e Pedro Ferreira
 */
public interface IPathF {
    
    void enterPath(int id, boolean toGranary);
    boolean moveForward(int id);
    
}
