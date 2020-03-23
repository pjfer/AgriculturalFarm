package Monitors.Interfaces;

/**
 * Interface for the Farmer to access the Standing Area.
 */
public interface IStandingAreaF {
    /**
     * Method called by a farmer to enter the Standing Area.
     * @param farmerId FarmerId.
     */
    void enterSA(int farmerId);
}
