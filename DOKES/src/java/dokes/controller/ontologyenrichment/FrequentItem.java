package dokes.controller.ontologyenrichment;

import java.util.ArrayList;
import java.util.HashMap;
import seks.basic.ontology.OntologyInteractionImpl;

/**
 *
 * @author Luis Paiva
 */
public class FrequentItem {

    //<editor-fold defaultstate="collapsed" desc="Variables">  
    
    private String Name;
    private ArrayList<String> wordList;
    private Integer numWords;

    private ArrayList<Keyword> keywordsRelated;
    private ArrayList<ConceptRelated> conceptsRelated;

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">  

    public FrequentItem(String Name) {
        this.Name = Name;
        this.wordList = processWord(Name, "_");
        this.numWords = wordList.size();
        
        this.conceptsRelated = new ArrayList<ConceptRelated>();
        this.keywordsRelated = new ArrayList<Keyword>();
        getAllRelatedKeywords();
    }
  
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Methods">  
    
    /**
     * Maps this Frequent Item with all related concepts in ontology
     * @param allKeywords 
     */
    public void mapFrequentItem(ArrayList<String> allKeywords, OntologyInteractionImpl oi) {
        ArrayList<String> freqItLst, keyWrdLst;
        Keyword kw = new Keyword("");
        
        // get all concepts related to this frequent item
        
        // compare frequent item with all keyword list to see if there is a match

        // calculate similarity distance
        loadKeywordsRelated(allKeywords);
        
        for (Keyword kwrd : keywordsRelated) {
            kwrd.loadConceptsRelated(oi);
        }
        // create new concept related and add it to conceptRelated list
    }
    
    /**
     * Divides a composed word (NGram) into a list containing each individual word.
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
     * From all keywords in the ontology, searches the ones that match exactly with word. 
     * NOTE: word param is a stememd frequent item, and each keyword in ontology is a full word.
     * @param word
     * @param allkeywords
     * @return 
     */
    public ArrayList<String> getKeywordExactMatch(String word, ArrayList<String> allkeywords){
      ArrayList<String> arrayListResult = new ArrayList<String> ();
      ArrayList<String> ngramList;
      ArrayList<String> keywordList;
      int equalKeys = 0, ngramListSize;

      ngramList = processWord(word, "_");
      ngramListSize = ngramList.size();

      for (int iterator = 0; iterator<ngramListSize; iterator++)
        System.out.print("ngram"+iterator+":"+ngramList.get(iterator)+";");
      System.out.print("\n");

      if (!allkeywords.isEmpty()) 
        for (int i=0, i2 = allkeywords.size();i<i2;i++){
          keywordList = processWord(allkeywords.get(i), " ");

          if (ngramListSize == keywordList.size())
            for (int h=0; h<ngramListSize;h++){
              if ( keywordList.get(h).startsWith(ngramList.get(h)) ){
                equalKeys++;
                System.out.println("keywordList»"+keywordList+"«ngramsList»"+ngramList.get(h)+"«equalKeys:"+equalKeys);
              }
              else
                h = ngramListSize+999;
            }
            if (equalKeys == ngramListSize){
              arrayListResult.add(allkeywords.get(i));
              System.out.println("Added: "+allkeywords.get(i));
            }
            equalKeys = 0;
        }
      return arrayListResult;
    }    

    private ArrayList<Keyword> getAllRelatedKeywords() {
        //TODO
        return null;
    }
    
    /**
     * Loads all keywords related to this frequent item to the keyword related list
     * @param allkeywords 
     */
    public void loadKeywordsRelated(ArrayList<String> allkeywords){
      ArrayList<String> keywordList;

      int equalKeys = 0, i=0;
      Keyword kwRelated;

      if (!allkeywords.isEmpty()) 
        for (String keyword : allkeywords){
            keywordList = processWord(keyword, " ");
            
            for (String frequentItemWord : wordList){
                for (String keywordListItem : keywordList)
                    if ( keywordListItem.startsWith(frequentItemWord) ){
                        equalKeys++;
                    }
                  }
            if (equalKeys > 0)
            {
                kwRelated = new Keyword(keyword);
                kwRelated.setCommonWords(equalKeys);
                kwRelated.calculateCosineSimilarity(this);
                keywordsRelated.add(kwRelated);
            }
            equalKeys = 0;
        }
    }

    
    
    private ArrayList<String> getAllKeywords() {
        //TODO
        return null;
    }
    
    public boolean hasKeywords() {
        return !this.keywordsRelated.isEmpty();
    }
    
    public boolean hasConceptsRelated() {
        return !this.conceptsRelated.isEmpty();
    }
    
    public HashMap<String, ArrayList<String>> getConceptsRelated(ArrayList<String> keywordList){
      HashMap<String,ArrayList<String>> conceptsList = new HashMap<String,ArrayList<String>> ();

      if (!keywordList.isEmpty())
        for (int i=0, j=keywordList.size(); i<j; i++){
//          conceptsList.put(keywordList.get(i), oi.getSubjectsFromProperty(keywordList.get(i), "has_Keyword"));
        }
      return conceptsList;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Properties">  

    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public ArrayList<Keyword> getKeywordsRelated() {
        return keywordsRelated;
    }
    public void setKeywordsRelated(ArrayList<Keyword> keywordsRelated) {
        this.keywordsRelated = keywordsRelated;
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<String> wordList) {
        this.wordList = wordList;
    }

    public Integer getNumWords() {
        return numWords;
    }

    public void setNumWords(Integer numWords) {
        this.numWords = numWords;
    }
    
    //</editor-fold>

//    public static void main(String[] args)
    public static void main(String[] args) {
        OntologyInteractionImpl oi = new OntologyInteractionImpl();
        ArrayList<String> allkw = oi.getAllValuesFromProperty("has_Keyword");
        FrequentItem FI = new FrequentItem("wast_manag");
        FI.mapFrequentItem(allkw, oi);
    }
}
