package Monitors;

import FarmInfrastructure.FIServer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MStoreHouse {
    
    private final FIServer fi;
    private final ReentrantLock rl;
    private int fToRelease;
    private int nFarmers;
    private final Condition waitStart;
    private int depositDurantion;
    private final int farmersPosition[];
    private final int totalFarmers = 5;
    
    public MStoreHouse(FIServer fi){
        this.fi = fi;
        this.rl = new ReentrantLock();
        this.fToRelease = 0;
        waitStart = rl.newCondition();
        farmersPosition = new int[totalFarmers];
        for(int i = 0; i < totalFarmers; i ++){
            farmersPosition[i] = -1;
        }
    }
    
    public void startSimulation(int id){
        rl.lock();
        try{
            fi.farmerAwaiting(id);
            while(fToRelease == 0){
                waitStart.await();
            }
            fToRelease --;
            this.releasePosition(id);
        }
        catch(Exception Ex){
        
        }
        finally{
            rl.unlock();
        }
    }
    
    public synchronized void enterSH(int id){
        int position = this.selectPosition(id);
        fi.farmerEnterSH(id, position);
    }
    
    public void proceedToSA(){
        fToRelease = nFarmers;
        waitStart.signalAll();
        
    }
    
    public synchronized void depositCorn(){
        try {
            Thread.sleep(depositDurantion);
        } catch (InterruptedException ex) {
            Logger.getLogger(MStoreHouse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void prepareSimulation(int nf, int to){
        this.nFarmers = nf;
        this.depositDurantion = to;
        this.fToRelease = 0;
    }
    
    private int selectPosition(int id){
        int position = (int) Math.round((Math.random() * (totalFarmers - 1)));
        while(farmersPosition[position] != -1){
            position = (int) Math.round((Math.random() * (totalFarmers - 1)));
        }
        farmersPosition[position] = id;
        return position;
    }
    
    private void releasePosition(int id){
        for(int i = 0; i < totalFarmers; i++){
            if(farmersPosition[i] == id){
                farmersPosition[i] = -1;
            }
        }
    }
        
}
