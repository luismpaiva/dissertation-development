package dokes.controller.ontologyenrichment;

import java.util.ArrayList;

/**
 *
 * @author Luis Paiva
 */
public class Keyword {
  
    //<editor-fold defaultstate="collapsed" desc="Variables">
    
    private String Name;
    private ArrayList<Concept> MatchedConcepts;
    private boolean Matches = false;

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">  

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Methods">  

    public boolean hasMatches() { return Matches; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Properties">  
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public ArrayList<Concept> getMatchedConcepts() {
        return MatchedConcepts;
    }

    public void setMatchedConcepts(ArrayList<Concept> MatchedConcepts) {
        this.MatchedConcepts = MatchedConcepts;
    }
    
    public void setMatches(boolean Matches) {
        this.Matches = Matches;
    }
    
    //</editor-fold>

}
