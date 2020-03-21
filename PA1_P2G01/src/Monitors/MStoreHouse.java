package Monitors;

import FarmInfrastructure.FIController;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MStoreHouse {
    
    private final FIController fiController;
    private final ReentrantLock rl;
    private int fToRelease;
    private int nFarmers;
    private final Condition waitStart;
    private int depositDurantion;
    private final int farmersPosition[];
    private final int totalFarmers = 5;
    private boolean stopSimulation;
    private boolean exitSimulation;
    private int[] cobsDeposited;
    private int nCobs;
    
    public MStoreHouse(FIController fiController){
        this.fiController = fiController;
        this.rl = new ReentrantLock();
        this.fToRelease = 0;
        waitStart = rl.newCondition();
        farmersPosition = new int[totalFarmers];
        for(int i = 0; i < totalFarmers; i ++){
            farmersPosition[i] = -1;
        }
    }
    
    public void prepareSimulation(int nf, int to, int nCobs){
        rl.lock();
        try{
            this.nFarmers = nf;
            this.depositDurantion = to;
            fToRelease = nFarmers;
            this.stopSimulation = false;
            this.exitSimulation = false;
            this.cobsDeposited = new int[]{0,0,0,0,0};
            this.nCobs = nCobs;
            waitStart.signalAll();
        }
        finally{
            rl.unlock();
        }
    }
    public void stopSimulation(){
        rl.lock();
        try {
            this.stopSimulation = true;
        } finally {
            rl.unlock();
        }
        
    }
    
    public void releaseWaitingStart(){
        rl.lock();
        try {
            waitStart.signalAll();
        } finally {
            rl.unlock();
        }
    }
    
    public void exitSimulation(){
        rl.lock();
        try {
            this.exitSimulation = true;
            this.stopSimulation = true;
            waitStart.signalAll();
        } finally {
            rl.unlock();
        }
    }
        
    public void startSimulation(int id){
        rl.lock();
        try{
            if(!exitSimulation){
                fiController.farmerAwaiting(id);
                while(fToRelease == 0 && !exitSimulation){
                    waitStart.await();
                }
                fToRelease --;
                this.releasePosition(id);
            }
        }
        catch(InterruptedException Ex){}
        finally{
            rl.unlock();
        }
    }
    
    public synchronized boolean enterSH(int id){
        if(!exitSimulation){
            int position = this.selectPosition(id);
            fiController.farmerEnterSH(id, position);
        }
        return this.exitSimulation;
    }
    
    public synchronized boolean depositCorn(Integer farmerId){
        try {
            if(!stopSimulation){
                cobsDeposited[farmerId-1] ++;
                Thread.sleep(depositDurantion);
                fiController.storeCorn(farmerId);
                
                if (cobsDeposited[farmerId-1] == nCobs){
                    return true;
                }  
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(MStoreHouse.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
