package Monitors;

import FarmInfrastructure.FIController;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MStandingArea {
    
    FIController fiController;
    boolean proceed;
    ReentrantLock rl;
    Condition proceedPath;
    private final int totalFarmers = 5;
    private int[] farmersPosition;
    private boolean stopSimulation = false;
    
    public MStandingArea(FIController fiController){
        this.fiController = fiController;
        proceed = false;
        rl = new ReentrantLock();
        proceedPath = rl.newCondition();
        farmersPosition = new int[5];
        for(int i = 0; i < totalFarmers; i ++){
            farmersPosition[i] = -1;
        }
        
    }
    
    public void enterSA(int id){
        rl.lock();
        
        try{
            if(!stopSimulation){
                int position = this.selectPosition(id);
                fiController.farmerStanding(id, position);
                while(!proceed && !stopSimulation){
                    proceedPath.await();
                }
                farmersPosition[position] = -1;
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(MStandingArea.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rl.unlock();
        }
    }
    
    public void proceedToPath(){
        rl.lock();
        try{
            proceed = true;
            proceedPath.signalAll();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    private int selectPosition(int id) {
        int position = (int) Math.round((Math.random() * (totalFarmers - 1)));
        while(farmersPosition[position] != -1){
            position = (int) Math.round((Math.random() * (totalFarmers - 1)));
        }
        farmersPosition[position] = id;
        return position; 
    }
    
    public void prepareSimulation(){
        stopSimulation = false;
        proceed = false;
        farmersPosition = new int[5];
        for(int i = 0; i < totalFarmers; i ++){
            farmersPosition[i] = -1;
        }
    }
    
    public void stopSimulation(){
        rl.lock();
        try{
            stopSimulation = true;
            proceedPath.signalAll();
        }
        finally{
            rl.unlock();
        }
        
    }
    
    
    
}
