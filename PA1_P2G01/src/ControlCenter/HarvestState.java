package ControlCenter;

/**
 * Enumerate which contains all the possible states of the harvest.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public enum HarvestState {
    Initial, Prepare, Walk, WaitToCollect, Collect, WaitToReturn, Return, Store
}
