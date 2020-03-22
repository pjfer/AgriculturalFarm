package Monitors.Interfaces;

/**
 * Interface for the Farm Controller to access the Path.
 */
public interface IPathC {
    /**
     * 
     * @param nf
     * @param to
     * @param ns 
     */
    void prepareSimulation(int nf, int to, int ns);

    /**
     * 
     */
    void stopSimulation();
}
