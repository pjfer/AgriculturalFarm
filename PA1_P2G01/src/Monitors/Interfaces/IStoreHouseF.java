package Monitors.Interfaces;

/**
 * Interface for the Farmer to access the Store House.
 */
public interface IStoreHouseF {
    
    /**
     * Method called by a farmer when they need to deposit cobs.
     * 
     * @param farmerId Farmer ID.
     * @return Indicates if more simulations are to be done or not.
     */
    boolean enterSH(int farmerId);
    
    /**
     * Method called by a farmer when they enter the store house 
     * for the first time or they ended a simulation.
     * @param farmerId Farmer ID.
     */  
    void startSimulation(int farmerId);
    
    /**
     * Method called from a farmer when he needs to deposit cobs.
     * @param farmerId Farmer ID.
     * @return Indicates if the farmer has deposited every cob.
     */
    boolean depositCorn(int farmerId);
    
}
