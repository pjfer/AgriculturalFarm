package ControlCenter;

/**
 * Enumerate which contains all the possible states of the harvest.
 * 
 * @author Pedro Ferreira
 */
public enum HarvestState {
    Initial, Prepare, Walk, WaitToCollect, Collect, WaitToReturn, Return, Store
}
