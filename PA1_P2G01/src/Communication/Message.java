package Communication;

import java.io.Serializable;

/**
 * Class responsible for a message's infrastructure.
 *
 * @author Pedro Ferreira and Rafael Teixeira
 */
public class Message implements Serializable {
    private String body;
    private HarvestState type;
    private Long farmerID = -1L;
    
    private Integer numCornCobs = -1;
    private Integer numFarmers = -1;
    private Integer numMaxSteps = -1;
    private Integer timeoutPath = -1;
    
    
    public Message() {
        body = "";
        type = HarvestState.Prepare;
    }
    
    public Message(String body) {
        this.body = body;
    }
    
    public Message(HarvestState type) {
        this.type = type;
    }
    
    public Message(Long farmerID) {
        this.farmerID = farmerID;
    }
    
    public Message(String body, HarvestState type) {
        this.body = body;
        this.type = type;
    }
    
    public Message(String body, HarvestState type, Long farmerID) {
        this.body = body;
        this.type = type;
        this.farmerID = farmerID;
    }
    
    public Message(HarvestState type, Integer numCornCobs, 
            Integer numFarmers, Integer numMaxSteps, Integer timeoutPath) {
        this.type = type;
        this.numCornCobs = -numCornCobs;
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
    
    public Long getFarmerID() {
        return farmerID;
    }
    
    public void setFarmerID(Long farmerID) {
        this.farmerID = farmerID;
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

