package Communication;

/**
 * Enumerate which contains all the possible states of the harvest.
 */
public enum HarvestState {
    WaitToStart, Prepare, WaitToWalk, Start, WaitToCollect, Collect, 
    WaitToReturn, Return, Stop, Exit, Update, FarmerTerminated, Error, Ok
}
