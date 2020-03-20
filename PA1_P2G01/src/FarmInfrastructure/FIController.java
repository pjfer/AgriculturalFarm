package FarmInfrastructure;

import FarmInfrastructure.GUI.FarmInfGUI;
import Monitors.MGranary;
import Monitors.MPath;
import Monitors.MStandingArea;
import Monitors.MStoreHouse;

/**
 * Classe responsável por atualizar a Interface Gráfica e por
 * enviar mensagens ao CC.
 */
public class FIController {
    
    FarmInfGUI fiGUI;
    public boolean allCorbsCollected;
    public boolean allFarmersInGranary;
    private MStoreHouse sh;
    private MGranary gr;
    private MPath path;
    private MStandingArea sa;
    
    public FIController(FarmInfGUI fiGUI, MGranary gr, 
            MPath path, MStandingArea sa, MStoreHouse sh){
        
        this.fiGUI = fiGUI;
        this.sh = sh;
        this.gr = gr;
        this.path = path;
        this.sa = sa;
        
    }
    public FIController(FarmInfGUI fiGUI){
        
        this.fiGUI = fiGUI;
        
    }

    public void setSh(MStoreHouse sh) {
        this.sh = sh;
    }

    public void setGr(MGranary gr) {
        this.gr = gr;
    }


    public void setPath(MPath path) {
        this.path = path;
    }

    public void setSa(MStandingArea sa) {
        this.sa = sa;
    }
    
    
    
    public void farmerAwaiting(int id) {
        System.out.println("Farmer: " + id + " awaiting.");
        //Mandar mensagem para CC a confirmar que está à espera.
    }

    public void farmerEnterSH(int id, int position) {
        fiGUI.moveFarmer(id, new Integer[] {0, position});
        System.out.println("Farmer: " + id + "entered sh in pos: "+position);
        //Mandar mensagem para CC a confirmar a entrada.
    }

    public void farmerStanding(int id, int position) {
        fiGUI.moveFarmer(id, new Integer[] {1, position});
        System.out.println("Farmer: " + id + "entered sa in pos: "+position);
        //Mensagem de Entrada na Standing Area.
    }

    public void movePath(Integer id, Integer[] position) {
        fiGUI.moveFarmer(id, new Integer[] {2, position[0], position[1]});
        System.out.println("Farmer: " + id + "moved in path pos: "+position[0] 
                +" : " + position[1]);
        //Mensagem de movimento no Path.
    }

    public void moveGranary(Integer id, Integer position) {
        fiGUI.moveFarmer(id, new Integer[] {3, position});
        System.out.println("Farmer: " + id + "entered granary in pos: "+ position);
        //Mensagem de entrada no Granary.
    }
    
    public void prepareFarm(int nf, int to, int ns){
        gr.prepareSimulation(to);
        path.prepareSimulation(nf, to, ns);
        sh.prepareSimulation(nf, to);
        sa.prepareSimulation();
    }
    
    public void startCollection(){
        sa.proceedToPath();
    }
    
    public void collectCorn(){
        gr.allFarmersInGranary();
    }
    
    public void returnWCorn(){
        gr.returnToStoreHouse();
    }
    
    public void stopHarvest(){
        sa.stopSimulation();
        path.stopSimulation();
        gr.stopSimulation();
        sh.stopSimulation();
    }
    
    public void exitSimulation(){
        sa.stopSimulation();
        path.stopSimulation();
        gr.stopSimulation();
        sh.exitSimulation();
    }
    
}
