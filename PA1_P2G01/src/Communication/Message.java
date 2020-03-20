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
    private Long farmerID;
    
    public Message() {
        body = "";
        type = HarvestState.Prepare;
        farmerID = -1L;
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
}
