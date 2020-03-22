package Monitors.Interfaces;

/**
 * Interface for the Farmer to access the Granary.
 */
public interface IGranaryF {
    /**
     * 
     * @param farmerId 
     */
    void enterGranary(int farmerId);
    
    /**
     * 
     * @param farmerId
     * @return 
     */
    boolean collectCob(int farmerId);
    
    /**
     * 
     * @param farmerId 
     */
    void waitForColleagues(int farmerId);
}
