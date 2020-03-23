package Monitors.Interfaces;

/**
 * Interface for the Farmer to access the Granary.
 */
public interface IGranaryF {
    /**
     * Each farmer enters the granary and waits for all of his colleague farmers
     * to enter the granary.
     * 
     * @param farmerId farmer's thread id.
     */
    void enterGranary(int farmerId);
    
    /**
     * Each farmer collects one corn cob at a time.
     * 
     * @param farmerId farmer's thread id.
     * @return true, if the farmer collects all of his corn cobs, or the 
     *               simulation stops.
     *         false, otherwise.
     */
    boolean collectCob(int farmerId);
    
    /**
     * Each farmer collects all of his corn cobs and waits for all of his 
     * colleague farmers to also collect all of theirs corn cobs.
     * 
     * @param farmerId farmer's thread id.
     */
    void waitForColleagues(int farmerId);
}
