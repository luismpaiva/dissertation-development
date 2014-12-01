package dokes.controller.ontologyenrichment;

/**
 * This class represents a concept match from a specific keyword.
 * @author Luis Paiva
 */
public class ConceptRelated {
  
    //<editor-fold defaultstate="collapsed" desc="Variables">  
    
    private String name;

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">  

    public ConceptRelated() {

    }
    
    public ConceptRelated(String Name) {
        this.name = Name;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Methods"> 
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Properties">  
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //</editor-fold>

}
