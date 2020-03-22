package Monitors.Interfaces;

/**
 * Interface for the Farm Infrastructure Controller to access the Path.
 */
public interface IPathC {
    /**
     * Instantiation of the path with the custom number of farmers, time to 
     * move and maximum number of steps.
     * 
     * @param nf total number of farmers that are going to harvest.
     * @param to time needed for each farmer to collect a corn cob.
     * @param ns maximum number of steps that each farmer can do in the path.
     */
    void prepareSimulation(int nf, int to, int ns);

    /**
     * Signal all the farmers that are awaiting, to stop the simulation.
     */
    void stopSimulation();
}
