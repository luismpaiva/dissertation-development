/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dokes.controller;

import java.util.ArrayList;
import java.util.HashMap;
import seks.basic.ontology.OntologyInteractionImpl;
import seks.basic.ontology.OntologyPersistenceImpl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import seks.basic.exceptions.MissingParamException;
/**
 *
 * @author Luis
 */
public class Concepts {
  
  private static String namespace = ("http://www.knowspaces.com/ontology_v1.owl#");
  private ArrayList<String> allkeywords;
  
  public Concepts(OntologyInteractionImpl oi) {
      this.allkeywords = oi.getAllValuesFromProperty("has_Keyword");
  }
  
  /**
   * 
   * @param word (the n-gram word to evaluate)
   * @return 0 for no matches or the arrayListResult with all matches
   */
  
  public ArrayList<String> getNgramList(String word, OntologyInteractionImpl oi){
    ArrayList<String> arrayListResult = new ArrayList<String> ();
    ArrayList<String> ngramList;
    ArrayList<String> keywordList;
    int equalKeys = 0, ngramListSize;
    
    ngramList = processWord(word, "_");
    ngramListSize = ngramList.size();
    
    for (int iterator = 0; iterator<ngramListSize; iterator++)
      System.out.print("ngram"+iterator+":"+ngramList.get(iterator)+";");
    System.out.print("\n");
    
    if (!this.allkeywords.isEmpty()) 
      for (int i=0, i2 = this.allkeywords.size();i<i2;i++){
        keywordList = processWord(this.allkeywords.get(i), " ");

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
            arrayListResult.add(this.allkeywords.get(i));
            System.out.println("Added: "+this.allkeywords.get(i));
          }
          equalKeys = 0;
      }
    return arrayListResult;
  }
  
  public int getNGramWordCount (String word) {
    ArrayList<String> xpto = this.processWord(word, " ");
    return xpto.size();
  }
  
  /**
   * When it does not find an exact match, it tries to find candidates for an aproximate match
   * @param word (the n-gram word to evaluate)
   * @return 0 for no approximate matches or the arrayListResult with all candidate matches
   */
  
  public ArrayList<String> getOnegramCandidatesList(String word, OntologyInteractionImpl oi){
    ArrayList<String> arrayListResult = new ArrayList<String> ();
    ArrayList<String> ngramList = new ArrayList<String> ();
    ArrayList<String> keywordList = new ArrayList<String> ();
    ArrayList<String> allkeywords;
    int equalKeys = 0;
    
    
    ngramList = processWord(word, "_");
    
    allkeywords = this.allkeywords;
    
    if (!allkeywords.isEmpty()) 
      for (int i=0;i<allkeywords.size();i++){
          keywordList = processWord(allkeywords.get(i), " ");
          if (keywordList.size() <= 3 ) {
            for (int h=0; h<ngramList.size();h++){
              for (int j=0; j<keywordList.size();j++)
                if ( keywordList.get(j).startsWith(ngramList.get(h)) ){
                  equalKeys++;
                  System.out.println("keywordList»"+keywordList+"«ngramsList»"+ngramList.get(h)+"«equalKeys:"+equalKeys);
              }
            }
            if (equalKeys > 0){
              arrayListResult.add(allkeywords.get(i));
              System.out.println("Added: "+allkeywords.get(i));
            }
          }
          equalKeys = 0;
      }
    return arrayListResult;
  }
  
  public HashMap <String, ArrayList<String>> getOnegramCandidatesListV2(String word, OntologyInteractionImpl oi){
    ArrayList<String> arrayListLevel1Result = new ArrayList<String> ();
    
    ArrayList<String> arrayListLevel2Result = new ArrayList<String> ();
    ArrayList<String> keywordList = new ArrayList<String> ();
    HashMap <String, ArrayList<String>> resultHM = new HashMap ();
    
    String matchLevel = "";
    boolean matchFlag = false;
    
    if (!this.allkeywords.isEmpty()) 
      for (int i=0, i1=this.allkeywords.size();i<i1;i++){
          keywordList = processWord(this.allkeywords.get(i), " ");
          if (keywordList.size() <= 3 ) {
            for (int j=0; j<keywordList.size();j++)
              if ( keywordList.get(j).startsWith(word) ){
                matchFlag = true;
                switch (keywordList.size()) {
                  case 2: {
                      matchLevel = "level1";
                      arrayListLevel1Result.add(this.allkeywords.get(i));
                      break;
                  }
                  case 3:{
                      matchLevel = "level2";
                      arrayListLevel2Result.add(this.allkeywords.get(i));
                      break;
                  }
                  default:{
                    break;
                  }
                }
                System.out.println("Added ("+matchLevel+"): <"+this.allkeywords.get(i) +"> for word «"+word+"»");
              }
          }
          
      }
    if (matchFlag) {
      resultHM.put("level1", arrayListLevel1Result);
      resultHM.put("level2", arrayListLevel2Result);
      
    }

    return resultHM;
  }
  public HashMap <String, ArrayList<String>> getOnegramCandidatesListV3(String word, OntologyInteractionImpl oi){
    ArrayList<String> resultAL = new ArrayList<String> ();
    ArrayList<String> keywordList = new ArrayList<String> ();
    HashMap <String, ArrayList<String>> hmResult = new HashMap ();
    HashMap <String, ArrayList<String>> resultHM = new HashMap ();
    
    boolean matchFlag = false;
    
    if (!this.allkeywords.isEmpty()) 
      for (int i=0, i1=this.allkeywords.size();i<i1;i++){
          keywordList = processWord(this.allkeywords.get(i), " ");
          if (keywordList.size() <= 3 ) {
            for (int j=0, j1=keywordList.size(); j<j1;j++)
              if ( keywordList.get(j).startsWith(word) ){
                matchFlag = true;
                if (!resultAL.contains(this.allkeywords.get(i)))
                    resultAL.add(this.allkeywords.get(i));
                System.out.println("Added (for "+word+"): <"+allkeywords.get(i) +">");
              }
          }
      }
    
    if (matchFlag){
        resultHM.put(word, resultAL);
    }
    return resultHM;
  }
  
  public Integer getComonWords(ArrayList<String> arrayList1, ArrayList<String> arrayList2) {
    int equalKeys = 0;
    for (int h=0; h<arrayList1.size();h++){
                for (int j=0; j<arrayList2.size();j++){
                  if (arrayList2.get(j).startsWith(arrayList1.get(h))){
                    equalKeys++;
                  }
                }
              }
    return equalKeys;
  }
  
  public Double getCosineSimilarity(ArrayList<String> arrayList1, ArrayList<String> arrayList2){
    Integer commonWords = this.getComonWords(arrayList1, arrayList2);
    Double cosSim = (((commonWords.doubleValue())*(commonWords))/(arrayList1.size()*arrayList2.size()));
    return cosSim;
  }
  
  public String getSimilarityClass(Double similarity){
    String levelName ="";
    if (similarity == 100)
        levelName = "level0";
    else if (similarity < 100 && similarity >= 80)
        levelName = "level1";
    else if (similarity < 80 && similarity >= 60)
        levelName = "level2";
    else if (similarity < 60 && similarity >= 40)
        levelName = "level3";
    else if (similarity < 40 && similarity >= 20)
        levelName = "level4";
    else if (similarity < 20)
        levelName = "level5";
    return levelName;
  }
  
  public HashMap <String, ArrayList<String>> getNgramListV2(String word, OntologyInteractionImpl oi){
    ArrayList<String> arrayListLevel1Result = new ArrayList<String> ();
    ArrayList<String> arrayListLevel2Result = new ArrayList<String> ();
    ArrayList<String> keywordList = new ArrayList<String> ();
    ArrayList<String> allkeywords;
    HashMap <String, ArrayList<String>> hmResult = new HashMap ();
    
    String matchLevel = "";
    boolean matchFlag = false;
    
    allkeywords = this.allkeywords;
    
    if (!allkeywords.isEmpty()) 
      for (int i=0;i<allkeywords.size();i++){
          keywordList = processWord(allkeywords.get(i), " ");
          if (keywordList.size() <= 3 ) {
            for (int j=0; j<keywordList.size();j++)
              if ( keywordList.get(j).startsWith(word) ){
                matchFlag = true;
                switch (keywordList.size()) {
                  case 2: {
                      matchLevel = "level1";
                      arrayListLevel1Result.add(allkeywords.get(i));
                      break;
                  }
                  case 3:{
                      matchLevel = "level2";
                      arrayListLevel2Result.add(allkeywords.get(i));
                      break;
                  }
                  default:{
                    break;
                  }
                }
                System.out.println("Added ("+matchLevel+"): <"+allkeywords.get(i) +"> for word «"+word+"»");
              }
          }
          
      }
    if (matchFlag) {
      hmResult.put("level1", arrayListLevel1Result);
      hmResult.put("level2", arrayListLevel2Result);
    }

    return hmResult;
  }
  
  public HashMap <String, ArrayList<String>> getBigramCandidatesList(String word, OntologyInteractionImpl oi){
    HashMap <String, ArrayList<String>> levelCandidates = new HashMap<String, ArrayList<String>>();
    ArrayList<String> level0 = new ArrayList<String> ();
    ArrayList<String> level1 = new ArrayList<String> ();
    ArrayList<String> level2 = new ArrayList<String> ();
    ArrayList<String> level3 = new ArrayList<String> ();
    ArrayList<String> level4 = new ArrayList<String> ();
    ArrayList<String> level5 = new ArrayList<String> ();
    ArrayList<String> ngramList;
    ArrayList<String> keywordList;
    ArrayList<String> allkeywords;

    int equalKeys = 0;
    int level = 6;
    String sourceWord;
    
    
    ngramList = processWord(word, "_");
    
    allkeywords = this.allkeywords;
  
    // 80% compare bi-gram with tri-gram - level1
    // 60% compare bi-gram with trigram (partitioned trigrams) - level2
    // 40% compare bi-gram with bi-gram (one word in common) - level3
    // 20% compare bi-gram with tri-gram (one word in common) - level4
    // 5% compare bi-gram with uni-gram (one word in common) - level5
  
    if (!allkeywords.isEmpty()) {
      for (int i=0;i<allkeywords.size();i++){
          keywordList = processWord(allkeywords.get(i), " ");
          level = 0;
          equalKeys = 0;
          if (keywordList.size() == 3 ) {
            if ((keywordList.get(0).startsWith(ngramList.get(0)))&& (keywordList.get(1).startsWith(ngramList.get(1)))){
              level = 1;
              level1.add(allkeywords.get(i));
            } else if ((keywordList.get(0).startsWith(ngramList.get(0)))&& (keywordList.get(1).startsWith(ngramList.get(1)))){
              level = 1;
              level1.add(allkeywords.get(i));
            } else {
              for (int h=0; h<ngramList.size();h++){
                for (int j=0; j<keywordList.size();j++){
                  if (keywordList.get(j).startsWith(ngramList.get(h))){
                    equalKeys++;
                  }
                }
              }
              if (equalKeys == 1){
                level = 4;
                level4.add(allkeywords.get(i));
              } else if (equalKeys == 2){
                  level = 2;
                  level2.add(allkeywords.get(i));
                }
            }
            
          } else if (keywordList.size() == 2 ) {
              for (int h=0; h<ngramList.size();h++){
                for (int j=0; j<keywordList.size();j++){
                  if (keywordList.get(j).startsWith(ngramList.get(h)))
                    equalKeys++;
                }
              } 
              if (equalKeys == 1){
                level = 3;
                level3.add(allkeywords.get(i));
              }
              if ((equalKeys == 2) && (keywordList.get(0).startsWith(ngramList.get(0)))) {
                level = 0;
                level0.add(allkeywords.get(i));
              }
            
            } else if (keywordList.size() == 1){
                for (int h=0; h<ngramList.size();h++){
                  for (int j=0; j<keywordList.size();j++){
                    if (keywordList.get(j).startsWith(ngramList.get(h)))
                      equalKeys++;
                  }
                }
                if (equalKeys == 1){
                  level = 5;
                  level5.add(allkeywords.get(i));
                }
             }
      }
    }
    else {System.out.println("Allkeywords is empty");}
    
    levelCandidates.put("level0", level0);
    levelCandidates.put("level1", level1);
    levelCandidates.put("level2", level2);
    levelCandidates.put("level3", level3);
    levelCandidates.put("level4", level4);
    levelCandidates.put("level5", level5);
    
    return levelCandidates;
  }
  
  public HashMap <String, ArrayList<String>> getBigramCandidatesListV2(String word, OntologyInteractionImpl oi){
    ArrayList<String> arrayListLevel1Result = new ArrayList<String> ();
    ArrayList<String> arrayListLevel2Result = new ArrayList<String> ();
    ArrayList<String> keywordList = new ArrayList<String> ();
    ArrayList<String> allkeywords;
    HashMap <String, ArrayList<String>> hmResult = new HashMap ();
    
    String matchLevel = "";
    boolean matchFlag = false;
    
    allkeywords = this.allkeywords;
    
    if (!allkeywords.isEmpty()) 
      for (int i=0;i<allkeywords.size();i++){
          keywordList = processWord(allkeywords.get(i), " ");
          if (keywordList.size() <= 3 ) {
            for (int j=0; j<keywordList.size();j++)
              if ( keywordList.get(j).startsWith(word) ){
                matchFlag = true;
                switch (keywordList.size()) {
                  case 2: {
                      matchLevel = "level1";
                      arrayListLevel1Result.add(allkeywords.get(i));
                      break;
                  }
                  case 3:{
                      matchLevel = "level2";
                      arrayListLevel2Result.add(allkeywords.get(i));
                      break;
                  }
                  default:{
                    break;
                  }
                }
                System.out.println("Added ("+matchLevel+"): <"+allkeywords.get(i) +"> for word «"+word+"»");
              }
          }
          
      }
    if (matchFlag) {
      hmResult.put("level1", arrayListLevel1Result);
      hmResult.put("level2", arrayListLevel2Result);
    }

    return hmResult;
  }
  
  
  
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
  
  public HashMap<String, ArrayList<String>> getConceptsRelated(ArrayList<String> keywordList, OntologyInteractionImpl oi){
    HashMap<String,ArrayList<String>> conceptsList = new HashMap<String,ArrayList<String>> ();
    
    if (!keywordList.isEmpty())
      for (int i=0, j=keywordList.size(); i<j; i++)
        conceptsList.put(keywordList.get(i), oi.getSubjectsFromProperty(keywordList.get(i), "has_Keyword"));

    return conceptsList;
  }
  
  public HashMap<String, ArrayList<String>> getAllConceptsRelated(String word, OntologyInteractionImpl oi){
    
    return getConceptsRelated(getNgramList(word, oi), oi);
  }
  
  public String getClassColorConceptsName (int classNumber){
    String ngramsMatchColorClass = "";
                        switch (classNumber) {
                          case 0:ngramsMatchColorClass = "level0";
                            break;
                          case 1:ngramsMatchColorClass = "level1";
                            break;
                          case 2:ngramsMatchColorClass = "level2";
                            break;
                          case 3:ngramsMatchColorClass = "level3";
                            break;
                          case 4:ngramsMatchColorClass = "level4";
                            break;
                          default:ngramsMatchColorClass = "level5";
                            break;
                        }
    return ngramsMatchColorClass;
  }
  
  
  public static void main(String[] args){
      try {
          // TEST AREA BELOW
          
          /* HashMap<String,ArrayList<String>> allConceptsList = new HashMap<String,ArrayList<String>> ();
          HashMap<String,ArrayList<String>> allConceptsUnigramsList = new HashMap<String,ArrayList<String>> ();
          HashMap<String,ArrayList<String>> levelCandidates = new HashMap<String,ArrayList<String>> ();
          HashMap<Integer,ArrayList<String>> testHM = new HashMap<Integer,ArrayList<String>> ();*/
          OntologyInteractionImpl oi = new OntologyInteractionImpl() ;
          OntologyPersistenceImpl op = new OntologyPersistenceImpl() ;
          /*ArrayList<String> allkeywords2;
          allkeywords2 = oi.getAllValuesFromProperty("has_Keyword");
          for (int abc=0; abc<allkeywords2.size(); abc++){
              System.out.println(allkeywords2.get(abc)+", ");
          }
          Concepts cp = new Concepts();
          ArrayList<String> ngramsCandidates = new ArrayList<String> ();
          
          String word = "energ_consumpt_wast";
          String word2 = "energy consumption waste";
          String word3 = "energy waste consumption";
          String word4 = "waste energy consumption";
          String word5 = "waste waste energy";
          
          String matchPercentage = "";
          for (int abc=0; abc<allkeywords2.size(); abc++)
            if ((allkeywords2.get(abc).contains("energi")) && (cp.processWord(allkeywords2.get(abc), " ").size() <=3))
              System.out.println(""+allkeywords2.get(abc));
          
          levelCandidates = cp.getBigramCandidatesList(word);
          
          if (!levelCandidates.isEmpty()){
            for (int i=0; i<levelCandidates.size(); i++){
              switch (i) {
                  case 0: {
                    ngramsCandidates = levelCandidates.get("level0");
                    matchPercentage = "Level 0 [100.0%]";
                    break;
                  }
                  case 1:{
                    ngramsCandidates = levelCandidates.get("level1");
                    matchPercentage = "Level 1 [80.0%]";
                    break;
                  }
                  case 2:{
                    ngramsCandidates = levelCandidates.get("level2");
                    matchPercentage = "Level 2 [60.0%]";
                    break;
                  }
                  case 3:{
                    ngramsCandidates = levelCandidates.get("level3");
                    matchPercentage = "Level 3 [40.0%]";
                    break;
                  }
                  case 4:{
                    ngramsCandidates = levelCandidates.get("level4");
                    matchPercentage = "Level 4 [20.0%]";
                    break;
                  }
                  case 5:{
                    ngramsCandidates = levelCandidates.get("level5");
                    matchPercentage = "Level 5 [5.0%]";
                    break;
                  }
                  default:{
                    ngramsCandidates.add("Empty");
                    matchPercentage = "0";
                    break;
                  }
                }
                System.out.println("<"+matchPercentage+"> "+ngramsCandidates.size()+" Candidates:");
                for (int j=0; j<ngramsCandidates.size(); j++) {
                  System.out.print("<"+ngramsCandidates.get(j)+">");
                }
                System.out.println();
              }
              ngramsCandidates = new ArrayList<String>();
            }
            else{
              System.out.println("NoCandidates found");
            }
          
          // ------- Connection to database functions below!!! 
          Integer i2 = cp.getComonWords(cp.processWord(word, "_"), cp.processWord(word2, " "));
          Integer i3 = cp.getComonWords(cp.processWord(word, "_"), cp.processWord(word3, " "));
          Integer i4 = cp.getComonWords(cp.processWord(word, "_"), cp.processWord(word4, " "));
          Integer i5 = cp.getComonWords(cp.processWord(word, "_"), cp.processWord(word5, " "));
          
          Double d2 = cp.getCosineSimilarity(cp.processWord(word, "_"), cp.processWord(word2, " "))*100;
          Double d3 = cp.getCosineSimilarity(cp.processWord(word, "_"), cp.processWord(word3, " "))*100;
          Double d4 = cp.getCosineSimilarity(cp.processWord(word, "_"), cp.processWord(word4, " "))*100;
          Double d5 = cp.getCosineSimilarity(cp.processWord(word, "_"), cp.processWord(word5, " "))*100;

          if (d2 == 100.0) {
              System.out.println("d2"+d2);
             if (!cp.processWord(word2, " ").get(0).startsWith(cp.processWord(word, "_").get(0)))
                d2 -= 1;
             if (!cp.processWord(word2, " ").get(1).startsWith(cp.processWord(word, "_").get(1)))
                d2 -= 1;
             if (!cp.processWord(word2, " ").get(2).startsWith(cp.processWord(word, "_").get(2)))
                d2 -= 1;
          }
           
          if (d3 == 100.0) {
             if (!cp.processWord(word3, " ").get(0).startsWith(cp.processWord(word, "_").get(0)))
                d3 -= 1;
             if (!cp.processWord(word3, " ").get(1).startsWith(cp.processWord(word, "_").get(1)))
                d3 -= 1;
             if (!cp.processWord(word3, " ").get(2).startsWith(cp.processWord(word, "_").get(2)))
                d3 -= 1;
          }
           
          if (d4 == 100.0) {
             if (!cp.processWord(word4, " ").get(0).startsWith(cp.processWord(word, "_").get(0)))
                d4 -= 1;
             if (!cp.processWord(word4, " ").get(1).startsWith(cp.processWord(word, "_").get(1)))
                d4 -= 1;
             if (!cp.processWord(word4, " ").get(2).startsWith(cp.processWord(word, "_").get(2)))
                d4 -= 1;
          }
           
          if (d5 == 100.0) {
             if (!cp.processWord(word5, " ").get(0).startsWith(cp.processWord(word, "_").get(0)))
                d5 -= 1;
             if (!cp.processWord(word5, " ").get(1).startsWith(cp.processWord(word, "_").get(1)))
                d5 -= 1;
             if (!cp.processWord(word5, " ").get(2).startsWith(cp.processWord(word, "_").get(2)))
                d5 -= 1;
          }
           
          System.out.println("Common words between <"+word+"> and <"+word2+"> - "+i2+". And Cosine Similarity is "+d2);
          System.out.println("Common words between <"+word+"> and <"+word3+"> - "+i3+". And Cosine Similarity is "+d3);
          System.out.println("Common words between <"+word+"> and <"+word4+"> - "+i4+". And Cosine Similarity is "+d4);
          System.out.println("Common words between <"+word+"> and <"+word5+"> - "+i5+". And Cosine Similarity is "+d5);
          
          System.out.println("Common words between <"+word+"> and <"+word2+"> - "+i2+". And Cosine Similarity is "+String.format("%3.0f %n", d2));
          
          System.out.println("Common words between <"+word+"> and <"+word3+"> - "+i3+". And Cosine Similarity is "+String.format("%3.0f %n", d3));
          
          System.out.println("Common words between <"+word+"> and <"+word4+"> - "+i4+". And Cosine Similarity is "+String.format("%3.0f %n", d4));
          
          System.out.println("Common words between <"+word+"> and <"+word5+"> - "+i5+". And Cosine Similarity is "+String.format("%3.0f %n", d5));*/
          
          /*Connection con;
           * Database db = new Database();
           * con = db.databaseConnect("jdbc:mysql://localhost:3306/mydb", "root", "", "com.mysql.jdbc.Driver");*/
          /*    String[] valuestoinsert = {"xpto", "xpto2", "abcd", "boss", "abcd2"};
           * String[] values = {"xpto"};
           * int idcontain;
           * for (int a=0; a<valuestoinsert.length; a++){
           * values[0] = valuestoinsert[a];
           * idcontain = db.databaseContainsConcept(con, "concepts", "name", values[0]);
           * if (idcontain == 0){
           * String query = "INSERT INTO concepts(idconcepts, name) values(?, ?)";
           * int lastID = db.databaseGetTableLastID(con, "concepts", "idconcepts");
           * db.databaseInsertOneDataRecord(con, query, 2, values, lastID);
           * System.out.println(values[0]+" - inserted in db with id "+(lastID+1));
           * }
           * else{
           * System.out.println(values[0]+" - already in database with id "+idcontain);
           * }
           * }
           * String table = "concepts";
           * String column = "name";
           * String element = "boss";
           * System.out.println("All ok to here?");
           * db.databaseContainsConcept(con, table, column, element);
           * db.databaseGetOneRule(con, 1);
           * db.databaseGetOneRule(con, 5);
           * db.databaseGetOneRule(con, 8);
           * System.out.println("All ok to here?");*/
          
          // db.databaseDeleteAllRecordsFromTable(con, "concepts");
          
          //String sqlQuery = "SELECT * FROM stemmed_word";
          //String[] values = {"xpto"};
          //String query = "INSERT INTO stemmed_word(idstemmed_word, stemmed_word) values(?, ?)";
          //db.databaseInsertOneDataRecord(con, query, 2, values);
          
          //int id = db.databaseRecordID(con, "stemmed_word", "idstemmed_word", word, "stemmed_word");
          /*ArrayList<String> wordAL = new ArrayList<String>();
           * String num = "1";
           * wordAL.add(0, "");
           * wordAL.add(1, word);
           * wordAL.add(2, word);
           * wordAL.add(3, num);
           * wordAL.add(4, num);
           * wordAL.add(5, num);
           * wordAL.add(6, num);
           * wordAL.add(7, num);
           * wordAL.add(8, num);*/
          
      //    db.databaseInsertRule(con, wordAL);
      //    db.databaseInsertRule(con, wordAL);
      //    db.databaseInsertRule(con, wordAL);
          
          //db.databaseInsertRule(con, word);
          //db.databaseSelectData(con, sqlQuery);

          //db.databaseDeleteAllRecordsFromTable(con, "stemmed_word");
          //db.databaseSelectData(con, sqlQuery);

          //db.databaseSelectData(con, sqlQuery);
          // db.databaseDeleteAllRecordsFromTable(con, "rules_stemmed");
          //db.databaseDisconnect(con);
          
          String owlClassName = "Concept";
          
          String owlSetClassName = "Object2";

          ArrayList <String> allclasses = oi.getSubclasses("Concept");
          for (int a=0; a<allclasses.size(); a++)
              System.out.println("Concept subclasses: "+allclasses.get(a));
          
          boolean xpto = oi.isIndividual("Actor_Individual");
          System.out.println("Actor_Individual is in DB? "+xpto);
//          xpto = oi.isClass(owlSetClassName);
//          System.out.println(owlSetClassName+"is in DB? "+xpto);
          
//          ArrayList <String> allclasses2 = oi.getSubclasses(owlClassName);
//          for (int a=0; a<allclasses2.size(); a++)
//              System.out.println(owlClassName+" subclasses: "+allclasses2.get(a));
          System.out.println("Next step");
          
          oi.setClass(owlSetClassName, owlClassName);
          oi.setIndividual(owlSetClassName+"_Individual", owlSetClassName);
          oi.setPropertyValue(owlSetClassName+"_Individual", "has_Keyword", "obj");
         
          ArrayList<String> list = oi.getSubjectsFromProperty("obj", "has_Keyword") ;
          
          for (String subject : list) {
              System.out.println(subject) ;
          }
          System.out.println("Writing next...") ;

          try {
              File file = new File("F:\\Dissertacao\\FrontEnd\\OWLtest.txt");              
              //FileOutputStream fis;              
              //fis = new FileOutputStream(file);
              //System.out.println(op.getModel());
//              op.getModel().write(fis);
              op.writeOnt(new FileOutputStream(file));
          } catch (FileNotFoundException fileNotFoundException) {
          } catch (NullPointerException NullPointerException) {
              System.out.println("It was null: " + NullPointerException.getMessage());
          }
          
          
          
      } catch (MissingParamException ex) {
          Logger.getLogger(Concepts.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
          Logger.getLogger(Concepts.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ClassNotFoundException ex) {
          Logger.getLogger(Concepts.class.getName()).log(Level.SEVERE, null, ex);
      }
    
    
    
    
    }
}
