package Communication;

/**
 * Enumerate which contains all the possible states of the harvest.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public enum HarvestState {
    Prepare, Start, WaitToWalk,  Walking, WaitToCollect, Collect, WaitToReturn, Return, Store,
    Stop, Exit

}
