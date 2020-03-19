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
        boolean exit = false;
        while(!exit){
            sh.startSimulation(id);
            sa.enterSA(id);
            path.enterPath(id, true);
            while(!ended_path){
                ended_path = path.moveForward(id);
                
            }
            System.out.println("entering granary");
            granary.enterGranary(id);
            for(int i = 0; i < 10; i++){
                granary.collectCob();
            }
            System.out.println("Farmer " + id + ": collected every corn cob!");
            granary.waitForColleagues();
            path.enterPath(id, false);
            ended_path = false;
            while(!ended_path){
                ended_path = path.moveForward(id);
            }
            exit = sh.enterSH(id);
            for(int i = 0; i < 10; i++){
                sh.depositCorn();
            }
            ended_path = false;
        }
        
    }
    
}
