package dokes.controller.ontologyenrichment;

import java.util.ArrayList;
import java.util.HashMap;
import seks.basic.ontology.OntologyInteractionImpl;

/**
 *
 * @author Luis Paiva
 */
public class Keyword {
  
    //<editor-fold defaultstate="collapsed" desc="Variables">
    
    private String Name;
    
    private Double Distance;
    private Integer CommonWords;

    private ArrayList<String> NameWordList;
    private ArrayList<ConceptRelated> ConceptReltdAL;

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">  

    public Keyword(String Name) {
        this.ConceptReltdAL = new ArrayList<ConceptRelated>();
        this.Name = Name;
        this.NameWordList = processWord(Name, " ");
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Methods">  

    public boolean hasMatches() { 
        return ConceptReltdAL.isEmpty(); 
    }
    
    /**
     * Calculates the similiraty between the frequent item and a keyword, and saves it in the keyword Distance property.
     * @param frequentItem 
     */
    public void calculateCosineSimilarity(String frequentItem) {
        ArrayList<String> frequentItemAL = processWord(frequentItem, "_"), nameAL = processWord(Name, " ");
        
        Integer commonWords = getComonWords(frequentItemAL, nameAL);
        this.setDistance( ( ( ( commonWords.doubleValue() ) * commonWords )/( frequentItemAL.size() * nameAL.size() ) )*100 );
    }

    /**
     * Calculates the similiraty between the frequent item and a keyword, and saves it in the keyword Distance property.
     * @param frequentItem 
     */
    public void calculateCosineSimilarity(FrequentItem frequentItem) {
        this.setDistance( ( ( ( CommonWords.doubleValue() ) * CommonWords )/( frequentItem.getNumWords() * NameWordList.size()) )*100 );
    }

    /**
     * Receives a word in the form of xxxx_yyyy_zzzz with a specific separator (in this example sperator is "_" ) and separates it in an array list in form of {"xxxx", "yyyy", "zzzz"}
     * @param wordProcessing
     * @param separator
     * @return 
     */
    public ArrayList<String> processWord(String wordProcessing, String separator) {
      ArrayList<String> arrayListResult = new ArrayList<String> ();
      int underscorePos;

      while (wordProcessing.contains(separator)){
        underscorePos = wordProcessing.indexOf(separator);
        arrayListResult.add(wordProcessing.substring(0, underscorePos));
        wordProcessing = wordProcessing.substring(underscorePos+1);
      }

      arrayListResult.add(wordProcessing);  

      return arrayListResult;
    }
    
    /**
     * Calculates the number of common words between frequent item and keyword ngrams
     * @param frequentItemList
     * @param keywordList
     * @return 
     */
    public Integer getComonWords(ArrayList<String> frequentItemList, ArrayList<String> keywordList) {
        int equalKeys = 0;
        for (String frequentItem : frequentItemList){
            for (String keywordListItem : keywordList) {
                if ( keywordListItem.startsWith(frequentItem) ){
                    equalKeys++;
                }
            }
        }
      return equalKeys;
    }
    
    public HashMap<String, ArrayList<String>> getConceptsRelated(ArrayList<String> keywordList, OntologyInteractionImpl oi){
        HashMap<String,ArrayList<String>> conceptsList = new HashMap<String,ArrayList<String>> ();

        if (!keywordList.isEmpty())
          for (int i=0, j=keywordList.size(); i<j; i++)
            conceptsList.put(keywordList.get(i), oi.getSubjectsFromProperty(keywordList.get(i), "has_Keyword"));

        return conceptsList;
    }
    
    public void loadConceptsRelated(OntologyInteractionImpl oi){
        ArrayList<String> conceptALs = oi.getSubjectsFromProperty(Name, "has_Keyword");
        ConceptRelated cncptRel;
        
        for (String concept : conceptALs){
            cncptRel = new ConceptRelated(concept);
            ConceptReltdAL.add(cncptRel);
        }
        
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Properties">  
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public ArrayList<ConceptRelated> getConceptRelated() {
        return ConceptReltdAL;
    }

    public void setConceptRelated(ArrayList<ConceptRelated> ConceptReltd) {
        this.ConceptReltdAL = ConceptReltd;
    }

    public Double getDistance() {
        return Distance;
    }

    public void setDistance(Double Distance) {
        this.Distance = Distance;
    }
    
    public Integer getCommonWords() {
        return CommonWords;
    }

    public void setCommonWords(Integer CommonWords) {
        this.CommonWords = CommonWords;
    }
   
    
    //</editor-fold>

}
