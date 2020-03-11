package Monitors;

import FarmInfrastructure.FIServer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MStandingArea {
    
    FIServer fi;
    boolean proceed;
    ReentrantLock rl;
    Condition proceedPath;
    private final int totalFarmers = 5;
    private final int[] farmersPosition;
    
    public MStandingArea(FIServer fi){
        this.fi = fi;
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
            int position = this.selectPosition(id);
            fi.farmerStanding(id, position);
            while(!proceed){
                proceedPath.await();
            }
            farmersPosition[position] = -1;
            
        } catch (InterruptedException ex) {
            Logger.getLogger(MStandingArea.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rl.unlock();
        }
    }
    
    public void proceedToPath(){
        proceed = true;
        proceedPath.signalAll();
    }

    private int selectPosition(int id) {
        int position = (int) Math.round((Math.random() * (totalFarmers - 1)));
        while(farmersPosition[position] != -1){
            position = (int) Math.round((Math.random() * (totalFarmers - 1)));
        }
        farmersPosition[position] = id;
        return position; 
    }
    
    
    
}
