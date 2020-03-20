package Communication;

/**
 * Class where is defined all the chosen configurations for the harvest.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class HarvestConfig {
    private Integer numCornCobs;
    private Integer numFarmers;
    private Integer numMaxSteps;
    private Integer timeoutPath;
    
    public HarvestConfig() {
        this.numCornCobs = 10;
        this.numFarmers = 5;
        this.numMaxSteps = 1;
        this.timeoutPath = 250;
    }
    
    public HarvestConfig(Integer numCornCobs, Integer numFarmers, 
            Integer numMaxSteps, Integer timeoutPath) {
        this.numCornCobs = numCornCobs;
        this.numFarmers = numFarmers;
        this.numMaxSteps = numMaxSteps;
        this.timeoutPath = timeoutPath;
    }
    
    public void setNumCornCobs(Integer numCornCobs) {
        this.numCornCobs = numCornCobs;
    }
    
    public Integer getNumCornCobs() {
        return this.numCornCobs;
    }
    
    public void setNumFarmers(Integer numFarmers) {
        this.numFarmers = numFarmers;
    }
    
    public Integer getNumFarmers() {
        return this.numFarmers;
    }
    
    public void setNumMaxSteps(Integer numMaxSteps) {
        this.numMaxSteps = numMaxSteps;
    }
    
    public Integer getNumMaxSteps() {
        return this.numMaxSteps;
    }
    
    public void setTimeoutPath(Integer timeoutPath) {
        this.timeoutPath = timeoutPath;
    }
    
    public Integer getTimeoutPath() {
        return this.timeoutPath;
    }
    
    @Override
    public String toString() {
        return "{numCornCobs: " + numCornCobs +
                ", numFarmers: " + numFarmers + 
                ", numMaxSteps: " + numMaxSteps +
                ", timeoutPath: " + timeoutPath + "}";
    }
}
