package ControlCenter;

/**
 * Class where is defined all the chosen configurations for the harvest.
 * 
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class HarvestConfig {
    private Integer numFarmers;
    private Integer numMaxSteps;
    private Integer timeoutPath;
    
    public HarvestConfig() {
        this.numFarmers = 5;
        this.numMaxSteps = 1;
        this.timeoutPath = 250;
    }
    
    public HarvestConfig(Integer numFarmers, Integer numMaxSteps, 
            Integer timeoutPath) {
        this.numFarmers = numFarmers;
        this.numMaxSteps = numMaxSteps;
        this.timeoutPath = timeoutPath;
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
}
