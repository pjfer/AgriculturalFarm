package Communication;

import java.io.Serializable;

/**
 * Class responsible for a message's infrastructure, used in the communication
 * between clients and servers.
 */
public class Message implements Serializable {
    /**
     * Information about the system that is mainly used as context.
     */
    private String body = "";
    
    /**
     * States the state of the message, as well as the state of the system.
     */
    private HarvestState type;
    
    /**
     * Total number of corn cobs that each farmer needs to get.
     */
    private Integer numCornCobs = -1;
    
    /**
     * Total number of farmers that are going to harvest.
     */
    private Integer numFarmers = -1;
    
    /**
     * Total number of maximum steps that each farmer can do in the path.
     */
    private Integer numMaxSteps = -1;
    
    /**
     * Total time needed for each farmer, when he moves in the path.
     */
    private Integer timeoutPath = -1;
    
    /**
     * Instantiation of a message from a non-farmer entity.
     * 
     * @param body information about the system.
     * @param type state of the system.
     */
    public Message(String body, HarvestState type) {
        this.body = body;
        this.type = type;
    }
    
    /**
     * Instantiation of a message when the control center sends the preparation
     * configuration to the farm infrastructure.
     * 
     * @param type state of the system.
     * @param numCornCobs total number of corn cobs that each farmer has to get.
     * @param numFarmers total number of farmers that are going to harvest.
     * @param numMaxSteps maximum number of steps that each farmer can do.
     * @param timeoutPath time needed for each farmer, when moving in the path.
     */
    public Message(HarvestState type, Integer numCornCobs, 
            Integer numFarmers, Integer numMaxSteps, Integer timeoutPath) {
        this.type = type;
        this.numCornCobs = numCornCobs;
        this.numFarmers = numFarmers;
        this.numMaxSteps = numMaxSteps;
        this.timeoutPath = timeoutPath;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public HarvestState getType() {
        return type;
    }
    
    public void setType(HarvestState type) {
        this.type = type;
    }
    
    public Integer getNumCornCobs() {
        return numCornCobs;
    }
    
    public Integer getNumFarmers() {
        return numFarmers;
    }
    
    public Integer getNumMaxSteps() {
        return numMaxSteps;
    }

    public Integer getTimeoutPath() {
        return timeoutPath;
    }

}

