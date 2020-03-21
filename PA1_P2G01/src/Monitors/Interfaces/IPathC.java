package Monitors.Interfaces;

/**
 * Interface for the Farm Controller to access the Path.
 *
 * @author Rafael Teixeira e Pedro Ferreira
 */
public interface IPathC {
        
    void prepareSimulation(int nf, int to, int ns);

    void stopSimulation();
    

}
