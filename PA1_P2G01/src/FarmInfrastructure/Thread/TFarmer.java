package FarmInfrastructure.Thread;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;


public class TFarmer extends Thread{
    
    private final int id;
    private final MGranary granary;
    private final MPath path;
    private final MStoreHouse sh;
    private final MStandingArea sa;
    
    public TFarmer(int id, MGranary granary, MPath path,
            MStoreHouse sh, MStandingArea sa){
        
        this.id = id;
        this.granary = granary;
        this.path = path;
        this.sh = sh;
        this.sa = sa;
        
    }
    
    @Override
    public void run(){
        boolean ended_path = false;
        sh.enterSH(id);
        while(true){
            sh.startSimulation(id);
            sa.enterSA(id);
            path.enterPath(id, true);
            while(!ended_path){
                ended_path = path.moveForward(id);
            }
            granary.enterGranary(id);
            for(int i = 0; i < 10; i++){
                granary.collectCob();
            }
            path.enterPath(id, false);
            ended_path = false;
            while(!ended_path){
                ended_path = path.moveForward(id);
            }
            
            sh.enterSH(id);
            for(int i = 0; i < 10; i++){
                sh.depositCorn();
            }
            ended_path = false;
        }
        
    }
    
}
