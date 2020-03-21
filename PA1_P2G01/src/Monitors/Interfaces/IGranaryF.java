package Monitors.Interfaces;

/**
 * Interface for the Farmer to access the Granary.
 *
 * @author Rafael Teixeira e Pedro Ferreira
 */
public interface IGranaryF {
    
    void enterGranary(int farmerId);
    boolean collectCob(int farmerId);
    void waitForColleagues(int farmerId);
}
