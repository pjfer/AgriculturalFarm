package Communication;

/**
 * Enumerate which contains all the possible states of the harvest.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public enum HarvestState {
    Initial, Prepare, Start, Walk, WaitToCollect, Collect, WaitToReturn, Return,
    Store, Stop, Exit
}
