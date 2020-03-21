package Monitors.Interfaces;

/**
 * Interface for the Farm Controller to access the Granary.
 *
 * @author Rafael Teixeira e Pedro Ferreira
 */
public interface IGranaryC {
    void prepareSimulation(int to, int nCobs);
    void allFarmersInGranary();
    void returnToStoreHouse();
    void stopSimulation();
}
