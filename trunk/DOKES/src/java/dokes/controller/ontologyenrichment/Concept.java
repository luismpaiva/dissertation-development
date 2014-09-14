package dokes.controller.ontologyenrichment;

/**
 * This class represents a concept match from a specific keyword.
 * @author Luis Paiva
 */
public class Concept {
  
    //<editor-fold defaultstate="collapsed" desc="Variables">  
    
    private String name;
    private int distance;
    private boolean exactMatch;

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">  

    public Concept() {
        this.exactMatch = false;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Methods"> 
    
    /**
     * @return TRUE if the keyword has an 100% Match; FALSE otherwise
     */
    public boolean isExactMatch() { return exactMatch; }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Properties">  
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    //</editor-fold>

}
