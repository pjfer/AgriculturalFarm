package FarmInfrastructure;

import FarmInfrastructure.GUI.FarmInfGUI;

/**
 * Classe responsável por atualizar a Interface Gráfica e por
 * enviar mensagens ao CC.
 */
public class FIController {
    
    FarmInfGUI fiGUI;
    public boolean allCorbsCollected;
    public boolean allFarmersInGranary;
    
    public FIController(FarmInfGUI fiGUI){
        this.fiGUI = fiGUI;
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
    
}
