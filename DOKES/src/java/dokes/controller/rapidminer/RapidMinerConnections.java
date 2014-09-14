package dokes.controller.rapidminer;

//<editor-fold defaultstate="collapsed" desc="Imports">

import com.rapidminer.RapidMiner;

//</editor-fold>

/**
 *
 * @author Luis Paiva
 */
public class RapidMinerConnections {
    
    //<editor-fold defaultstate="collapsed" desc="Variables">
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public RapidMinerConnections() {}
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Methods">

    public void Init() {
        RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.COMMAND_LINE);
        RapidMiner.init();
    }
    public void Process() {
    }
    
    public void rapidminerInit() {
        RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.COMMAND_LINE);
        RapidMiner.init();
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Properties">

    public Object GetResults() {
        return Object.class;
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Main">

    public static void main(String[] args) {}
    
    //</editor-fold>
    
}
