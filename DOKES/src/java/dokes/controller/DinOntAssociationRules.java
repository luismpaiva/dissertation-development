package dokes.controller;

//<editor-fold defaultstate="collapsed" desc="Imports">  
import dokes.controller.ontologyenrichment.Rule;
import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.XMLException;
import dokes.controller.ontologyenrichment.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

import dokes.controller.utilities.LogHandlerClass;
import dokes.controller.utilities.Database;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import org.apache.commons.lang.time.StopWatch;
import seks.basic.ontology.OntologyInteractionImpl;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
//</editor-fold> 

/**
 *
 * @author Luis Paiva
 */
public class DinOntAssociationRules {
    
    //<editor-fold defaultstate="collapsed" desc="Variables">
    // Paths with necessary files
    private static String RAPID_MINER_PROCESS_XML = "D:\\Dissertacao\\FrontEnd\\processXML\\process5.XML";
    private static String ROOT_PATH = "D:\\Dissertacao\\FrontEnd\\";
    private static String LOG_FILE = ROOT_PATH+"DARprocessResultsFromDatabaseV2log.txt";
    private static String DATA_FILE = ROOT_PATH+"Datafile.txt";
    private static String RULE_XML_FILE = "D:\\NetBeansProjects\\DOKES\\web\\xml\\rules.xml";

    //Log info
    LogHandlerClass log;
    LogHandlerClass logData;
    LogHandlerClass ruleXmlFile;

    // Properties
    private String results = "";
    private String resultsconcepts = "";
    private String ruletosave = "";
    private String resultsToDatabase = "false";
    private String premiseString = "";
    private String conclusionString = "";
    
    // Database info
    private String databaseURLName = "jdbc:mysql://localhost:3306/associationrulesdbv2";
    private String databaseDriverName = "com.mysql.jdbc.Driver";
    private String databaseUserName = "root";
    private String databasePasswordName = "";
    private String[] tablenames = {"rules", "rules_stemmed", "stemmed_word", "concepts"};
    
    // Rapidminer info
    private static Process rm5;
    
    // Ontology info
    private OntologyInteractionImpl oi;
    
    // Database info
    private Database dbRules = new Database();
    
    // Concept class info
    private Concepts cp;
    
    // Class info
    private Integer associationruleID = 0;
    private HashMap<Integer, Rule> Rules = new HashMap<Integer, Rule>();
    private ArrayList<Rule> RulesList = new ArrayList<Rule>();
    
    private Rule rule = new Rule();
    private ArrayList<String> allKeywords;

    // Strings for XML Elements
		private String conceptStr = "concept";
		private String ruleStr = "rule";
		private String keywordStr = "keyword";
		private String optionsStr = "options";
		private String optionStr = "option";
		private String nameStr = "name";
		private String distanceStr = "distance";
		private String metricsStr = "metrics";
		private String supportStr = "support";
		private String confidenceStr = "confidence";
		private String convictionStr = "conviction";
		private String gainStr = "gain";
		private String liftStr = "lift";
		private String psStr = "ps";	
		private String laplaceStr = "laplace";

		//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">

    public DinOntAssociationRules() {
        loggingInit();
        StopWatch ctorStopWatch = new StopWatch();
        ctorStopWatch.reset();
        ctorStopWatch.start();
        oi = new OntologyInteractionImpl();
        ctorStopWatch.stop();
        log.printLnToFile("DinOntAssociationRules CTOR: OntologyInteractionImpl init: "+elapsedTimeInMin(ctorStopWatch)+"s.");
        
        
        ctorStopWatch.reset();
        ctorStopWatch.start();
        cp = new Concepts(oi);
        ctorStopWatch.stop();
        log.printLnToFile("DinOntAssociationRules CTOR: Concepts Class init: "+elapsedTimeInMin(ctorStopWatch));
        
// allKeywords = oi.getAllValuesFromProperty("has_Keyword");
        allKeywords = cp.getAllkeywords();
        premiseString = "empty_premise";
        conclusionString = "empty_conclusion";
        ruletosave = "empty_rule";
        results = "";
        resultsconcepts = "";
        resultsToDatabase = "false";
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Methods">
    
    private void loggingInit(){
        // Log Initialization
        log = new LogHandlerClass();
        logData = new LogHandlerClass();
        ruleXmlFile = new LogHandlerClass();

        log.openLogFile(LOG_FILE);
        logData.openFile(DATA_FILE);
    }
    
    public void rapidminerInit() {
        RapidMiner.setExecutionMode(ExecutionMode.COMMAND_LINE);
        RapidMiner.init();
    }

    // Original name is processResults() - It's working, do not mess with it.
    public void processResults() {
        try {
            rm5 = new Process(new File(RAPID_MINER_PROCESS_XML));
            IOContainer ioResult = rm5.run();
            ExampleSet resultSet;
            int num_rules = 0;
            if (ioResult.getElementAt(0) instanceof ExampleSet) {
                resultSet = (ExampleSet) ioResult.getElementAt(0);
                for (int i = 0; i <= resultSet.size() - 1; i++) {
                    if ((resultSet.getExample(i).get("Premise Items").equals(1)) && (resultSet.getExample(i).get("Conclusion Items").equals(1))) {
                        num_rules++;
                        results += "<ul><li title=\"Premise\">" + resultSet.getExample(i).get("Premise") + "</li>"
                                + "<li title=\"Conclusion\">" + resultSet.getExample(i).get("Conclusion") + "</li>"
                                + "<li title=\"Confidence\" class=\"metrics\">" + String.format("%f%n", resultSet.getExample(i).get("Confidence")) + "</li>"
                                + "<li title=\"Conviction\" class=\"metrics\">";

                        if (resultSet.getExample(i).get("Conviction").equals("Infinity")) {
                            results += resultSet.getExample(i).get("Conviction");
                        } else {
                            results += String.format("%f%n", resultSet.getExample(i).get("Conviction"));
                        }

                        results += "</li>"
                                + "<li title=\"Gain\" class=\"metrics\">" + String.format("%f%n", resultSet.getExample(i).get("Gain")) + "</li>"
                                + "<li title=\"Laplace\" class=\"metrics\">" + String.format("%f%n", resultSet.getExample(i).get("Laplace")) + "</li>"
                                + "<li title=\"Lift\" class=\"metrics\">" + String.format("%f%n", resultSet.getExample(i).get("Lift")) + "</li>"
                                + "<li title=\"Ps\" class=\"metrics\">" + String.format("%f%n", resultSet.getExample(i).get("Ps")) + "</li>"
                                + "<li title=\"Total Support\" class=\"metrics\">" + String.format("%f%n", resultSet.getExample(i).get("Total Support")) + "</li><li class=\"num_rules\">" + num_rules + "</li></ul>";
                    } else {
                        break;
                    }
                }
            } else {
                results = "No results found.";
            }

            results = results.replace("[", "");
            results = results.replace("]", "");


        } catch (OperatorException ex) {
            Logger.getLogger(DinOntAssociationRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DinOntAssociationRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLException ex) {
            Logger.getLogger(DinOntAssociationRules.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void processResultsToDatabaseV2() {
        int idstemmed_wordA, idstemmed_wordB, lastID_stemmed_word;

        String conclusionTMP, premiseTMP;
        Object premise, conclusion, confidence, conviction, laplace, gain, lift, ps, totalSupport;

        dbRules = new Database(this.databaseURLName, this.databaseUserName, this.databasePasswordName, this.databaseDriverName, log);
        Connection con = dbRules.databaseConnect();

        for (int idclean = 0, idclean2 = tablenames.length; idclean < idclean2; idclean++) {
            dbRules.databaseDeleteAllRecordsFromTable(con, this.tablenames[idclean]);
        }

        try {
            rm5 = new Process(new File(RAPID_MINER_PROCESS_XML));

            IOContainer ioResult = rm5.run();
            ExampleSet resultSet;

            if (ioResult.getElementAt(0) instanceof ExampleSet) {
                resultSet = (ExampleSet) ioResult.getElementAt(0);
                for (int i = 0; i <= resultSet.size() - 1; i++) {
                    if ((resultSet.getExample(i).get("Premise Items").equals(1)) && (resultSet.getExample(i).get("Conclusion Items").equals(1))) {
                        resultsToDatabase = "false";
                        premiseTMP = (String) resultSet.getExample(i).get("Premise");
                        premiseTMP = premiseTMP.replace("]", "");
                        premise = premiseTMP.replace("[", "");

                        idstemmed_wordA = dbRules.databaseContainsConcept(con, this.tablenames[2], "stemmed_word", premise.toString());
                        if (idstemmed_wordA == 0) {
                            lastID_stemmed_word = dbRules.databaseGetTableLastID(con, this.tablenames[2], "idstemmed_word");
                            String[] values = {premise.toString()};
                            String query = "INSERT INTO stemmed_word(idstemmed_word, stemmed_word) values(?, ?)";
                            dbRules.databaseInsertOneDataRecord(con, query, 2, values, lastID_stemmed_word);
                            idstemmed_wordA = dbRules.databaseContainsConcept(con, this.tablenames[2], "stemmed_word", premise.toString());
                        }

                        conclusionTMP = (String) resultSet.getExample(i).get("Conclusion");
                        conclusionTMP = conclusionTMP.replace("[", "");
                        conclusion = conclusionTMP.replace("]", "");

                        idstemmed_wordB = dbRules.databaseContainsConcept(con, this.tablenames[2], "stemmed_word", conclusion.toString());
                        if (idstemmed_wordB == 0) {
                            lastID_stemmed_word = dbRules.databaseGetTableLastID(con, this.tablenames[2], "idstemmed_word");
                            String[] values = {conclusion.toString()};
                            String query = "INSERT INTO stemmed_word(idstemmed_word, stemmed_word) values(?, ?)";
                            dbRules.databaseInsertOneDataRecord(con, query, 2, values, lastID_stemmed_word);
                            idstemmed_wordB = dbRules.databaseContainsConcept(con, this.tablenames[2], "stemmed_word", conclusion.toString());
                        }

                        confidence = resultSet.getExample(i).get("Confidence");
                        conviction = resultSet.getExample(i).get("Conviction");

                        if (conviction.toString().contains("Infinity")) {
                            conviction = 8888;
                        }

                        gain = resultSet.getExample(i).get("Gain");
                        laplace = resultSet.getExample(i).get("Laplace");
                        lift = resultSet.getExample(i).get("Lift");
                        ps = resultSet.getExample(i).get("Ps");
                        totalSupport = resultSet.getExample(i).get("Total Support");

                        ArrayList<String> ruleArray = new ArrayList<String>();
                        ruleArray.add(0, "");
                        ruleArray.add(1, Integer.toString(idstemmed_wordA));
                        ruleArray.add(2, Integer.toString(idstemmed_wordB));
                        ruleArray.add(3, conviction.toString());
                        ruleArray.add(4, gain.toString());
                        ruleArray.add(5, lift.toString());
                        ruleArray.add(6, laplace.toString());
                        ruleArray.add(7, ps.toString());
                        ruleArray.add(8, totalSupport.toString());
                        ruleArray.add(9, confidence.toString());

                        dbRules.databaseInsertRulev2(con, ruleArray);

                        resultsToDatabase = "true";
                    } else {
                        break;
                    }
                }
            }



        } catch (OperatorException ex) {
            Logger.getLogger(DinOntAssociationRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DinOntAssociationRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLException ex) {
            Logger.getLogger(DinOntAssociationRules.class.getName()).log(Level.SEVERE, null, ex);
        }
        dbRules.databaseDisconnect(con);
    }

    public void processResultsFromDatabaseV4() {
        
        // Variable declaration section -----------------------
        Object confidence, conviction, laplace, gain, lift, ps, totalSupport;
        FrequentItem premise, conclusion;
        Metrics ruleMetrics;

        HashMap<String, ArrayList<String>> hmConcepts = new HashMap<String, ArrayList<String>>();
        HashMap<String, ArrayList<String>> ngramsListHM = new HashMap<String, ArrayList<String>>();

        // HashMap<Integer, ArrayList<String>> rulesHM = new HashMap<Integer, ArrayList<String>>();
        
        HashMap<String, ArrayList<String>> allConceptsRelatedHM = new HashMap<String, ArrayList<String>>();

        HashMap<String, Double> allConceptsRelatedSimilarityHM = new HashMap<String, Double>();

        HashMap<String, HashMap<String, Double>> allConceptsRelatedSimilarityKeywordHM = new HashMap<String, HashMap<String, Double>>();

        ArrayList<String> allConceptsRelatedAL = new ArrayList<String>();
        ArrayList<String> ngramsList, premiseWordList, conclusionWordList;
       
        DecimalFormat df = new DecimalFormat("##0.00");

        Double distancePercentage;
        String premiseWordString, conclusionWordString;
        String xml = "";
        
        Integer ngramsWordCount;

        // XML creation - creates the elements necessary for the XML Rules file
        
        Element rulesElement = new Element("rules");

        Element creationinfo = new Element("creationinfo");
        
        Element ruleElement = new Element(ruleStr);
        Element conceptElement = new Element(conceptStr);
        Element keywordElement = new Element(keywordStr);
        Element optionsElement = new Element(optionsStr);
        Element optionElement = new Element(optionStr);
        Element optionNameElement = new Element(nameStr);
        Element optionDistanceElement = new Element(distanceStr);
        
        Element metricsElement = new Element(metricsStr);
        Element supportXML = new Element(supportStr);
        Element confidenceXML = new Element(confidenceStr);
        Element convictionXML = new Element(convictionStr);
        Element gainXML = new Element(gainStr);
        Element liftXML = new Element(liftStr);
        Element psXML = new Element("ps");
        Element laplaceXML = new Element("laplace");
        
        // XML creation information initializing - gets date and hour of the creation for the XML Rules file

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfdate = new SimpleDateFormat("dd/MM/YYYY");        
        String dataactual = sdfdate.format(Calendar.getInstance().getTime()).toString();
        String horaactual = sdf.format(Calendar.getInstance().getTime()).toString();
        creationinfo.setAttribute("date", dataactual);
        creationinfo.setAttribute("time", horaactual);
        rulesElement.addContent(creationinfo);
        
        // End of XML initializing
        
        
        getRulesListFromDB();
        
        // ArrayList<FrequentItem> allFI = new ArrayList<FrequentItem>();
        HashMap<String, FrequentItem> allFrequentItems = new HashMap<String, FrequentItem>();
        StopWatch st = new StopWatch();
        StopWatch st2 = new StopWatch();
        st2.reset();
        st2.start();
        // This cycle through all rules and builds a structure with all unique premise and conclusion items.
        for (Rule ruleToMap : RulesList) {
            st.reset();
            st.start();
            if (!allFrequentItems.containsKey(ruleToMap.getPremise().getName())) {
                ruleToMap.getPremise().mapFrequentItem(allKeywords, oi);
                allFrequentItems.put(ruleToMap.getPremise().getName(), ruleToMap.getPremise());
            }
            else{
                ruleToMap.setPremise(allFrequentItems.get(ruleToMap.getPremise().getName()));
            }
            
            if (!allFrequentItems.containsKey(ruleToMap.getConclusion().getName())){
                ruleToMap.getConclusion().mapFrequentItem(allKeywords, oi);
                allFrequentItems.put(ruleToMap.getPremise().getName(), ruleToMap.getPremise());
            }
            else {
                ruleToMap.setConclusion(allFrequentItems.get(ruleToMap.getConclusion().getName()));
            }

            st.stop();
            log.printLnToFile("Rule <" + ruleToMap.id + "> Building structure: MAPPING: Rule Time: "+elapsedTimeInSec(st)+"s");
            
        }
        log.printLnToFile("All Rules mapping elapsed time: "+elapsedTimeInMin(st2)+"m");
        
        if (!Rules.isEmpty()) {
            for (int i = 0; i <= Rules.size() - 1; i++) {
            
                st.reset();
                st.start();
               
                rule = Rules.get(i+1);

                // log.printLnToFile("(" + (rule.id) + ")" + " Start: Premise <" + rulesHM.get(i + 1).get(0) + "> Conclusion <" + rulesHM.get(i + 1).get(1) + ">");
                log.printLnToFile("(" + rule.id + ")" + " Start: Premise <" + rule.getPremise().getName() + "> Conclusion <" + rule.getConclusion().getName() + ">");
                
                ruleElement.setAttribute("id", Integer.toString(i+1));
                
                /*
                jksda++;
                if (jksda == 103){
                int asdasd = 0;
                }
                */                
                if (i == 4) {
                    log.printLnToFile("-------------------------------------- Rule (" + rule.id + ") ---------------------------------------------");
                }
                st2.reset();
                st2.start();
                        
// ------------------------------------- Premise words finding

                //premise = rulesHM.get(i + 1).get(0);
                premise = rule.getPremise();
                
                //premiseWordString = "<p id=\"premise_" + (i + 1) + "\">" + premise.getName();
                premiseWordString = "<p id=\"premise_" + (rule.id) + "\">" + premise.getName();
                
                conceptElement.setAttribute("value", "premise");
                keywordElement.setText(premise.getName());
                conceptElement.addContent(keywordElement);
                
                if (!allConceptsRelatedHM.containsKey(premise.getName())) {
                    ngramsWordCount = cp.processWord(premise.getName(), "_").size();
                    log.printLnToFile("(" + (rule.id) + ")" + "Number of words of Ngram" + ngramsWordCount);
                    ngramsList = cp.getNgramList(premise.getName());

                    // --------------------

                    if (ngramsList.isEmpty()) {  // If the premise word did not found any exact match within the ontology, tries others
                        premiseWordString += " (exact match not found) Candidates:</p><select name=\"premise" + (rule.id) + "\" id=\"premise" + (rule.id) + "\">";
                        
                        switch (ngramsWordCount) {
                            case 1: {
                                ngramsListHM = cp.getOnegramCandidatesListV3(premise.getName());

                                if (!ngramsListHM.isEmpty()) {
                                        if (!ngramsListHM.get(premise.getName()).isEmpty()) {
                                            hmConcepts = cp.getConceptsRelated(ngramsListHM.get(premise.getName()));
                                            for (int n = 0; n < hmConcepts.size(); n++) {
                                                if (!hmConcepts.isEmpty()) {
                                                    premiseWordList = hmConcepts.get(ngramsListHM.get(premise.getName()).get(n));
                                                    log.printLnToFile("(" + (rule.id) + ")" + "Showing " + ngramsListHM.get(premise.getName()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(premise.getName()).get(n)) + ") premiseWordList - " + premiseWordList);
                                                    for (int m2 = 0; m2 < premiseWordList.size(); m2++) {
                                                        if (!premiseWordString.contains(premiseWordList.get(m2))) {
                                                            distancePercentage = cp.getCosineSimilarity(cp.processWord(premise.getName(), "_"), cp.processWord(ngramsListHM.get(premise.getName()).get(n), " ")) * 100;
                                                            premiseWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + premiseWordList.get(m2) + "\">" + premiseWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                            allConceptsRelatedAL.add(premiseWordList.get(m2));
                                                            allConceptsRelatedSimilarityHM.put(premiseWordList.get(m2), distancePercentage);
                                                            log.printLnToFile("(" + (rule.id) + ") Common words Cosine similarity:" + "<" + premise.getName() + "><" + cp.processWord(ngramsListHM.get(premise.getName()).get(m2), " ").toString() + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                            // set content
																														optionElement.setAttribute("exactmatch", "NO");
                                                            optionNameElement.setText(premiseWordList.get(m2).toString());
                                                            optionDistanceElement.setText(df.format(distancePercentage));
                                                            // add content
																														optionElement.addContent(optionNameElement);
                                                            optionElement.addContent(optionDistanceElement);
                                                            optionsElement.addContent(optionElement);
                                                            // reset element
																														optionElement = new Element(optionStr);
                                                            optionNameElement = new Element(nameStr);
                                                            optionDistanceElement = new Element("distance");
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            log.printLnToFile("(" + (rule.id) + ")" + "No candidates found for ->" + ngramsListHM.get(premise.getName()));
                                        }
                                    premiseWordString += "</select>";
                                } else {
                                    log.printLnToFile("(" + (rule.id) + ")" + "No Candidates Found for - " + premise.getName());
                                    premiseWordString += "<option>(Empty)</option></select>";
                                    premiseWordString += "<a href=# onclick=\"javascript:conceptsTree('premise_" + (rule.id) + "');\">New...</a>";
                                }
                                break;
                            }
                            case 2: {
                                ngramsListHM = new HashMap<String, ArrayList<String>>();
                                ArrayList<String> keywordList = cp.processWord(premise.getName(), "_");
                                for (int wordI = 0; wordI < ngramsWordCount; wordI++) {
                                    ngramsListHM.putAll(cp.getOnegramCandidatesListV3(keywordList.get(wordI)));
                                }
                                log.printLnToFile("(" + (rule.id) + ")" + "Bigram (" + premise.getName() + ") ngramslist" + ngramsListHM);

                                if (!ngramsListHM.isEmpty()) {
                                    for (int m = 1; m <= ngramsListHM.size(); m++) {
                                        if (!ngramsListHM.get(premise.getName()).isEmpty()) {
                                            hmConcepts = cp.getConceptsRelated(ngramsListHM.get(premise.getName()));

                                            for (int n = 0; n < hmConcepts.size(); n++) {
                                                if (!hmConcepts.isEmpty()) {
                                                    premiseWordList = hmConcepts.get(ngramsListHM.get(premise.getName()).get(n));
                                                    log.printLnToFile("(" + (rule.id) + ")" + "Showing " + ngramsListHM.get(premise.getName()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(premise.getName()).get(n)) + ") premiseWordList - " + premiseWordList);
                                                    for (int m2 = 0; m2 < premiseWordList.size(); m2++) {
                                                        if (!premiseWordString.contains(premiseWordList.get(m2))) {
                                                            distancePercentage = cp.getCosineSimilarity(cp.processWord(premise.getName(), "_"), cp.processWord(ngramsListHM.get(premise.getName()).get(n), " ")) * 100;
                                                            premiseWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + premiseWordList.get(m2) + "\">" + premiseWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                            allConceptsRelatedAL.add(premiseWordList.get(m2));
                                                            allConceptsRelatedSimilarityHM.put(premiseWordList.get(m2), distancePercentage);
                                                            log.printLnToFile("(" + (rule.id) + ") Common words Cosine similarity:" + "<" + premise.getName() + "><" + cp.processWord(ngramsListHM.get(premise.getName()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                            // set content
																														optionElement.setAttribute("exactmatch", "NO");
                                                            optionNameElement.setText(premiseWordList.get(m2).toString());
                                                            optionDistanceElement.setText(df.format(distancePercentage));
                                                            // add content
																														optionElement.addContent(optionNameElement);
                                                            optionElement.addContent(optionDistanceElement);
                                                            optionsElement.addContent(optionElement);
                                                            // reset element
																														optionElement = new Element(optionStr);
                                                            optionNameElement = new Element(nameStr);
                                                            optionDistanceElement = new Element("distance");
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            log.printLnToFile("(" + (rule.id) + ")" + "No candidates found for ->" + ngramsListHM.get(m));
                                        }
                                    }
                                    premiseWordString += "</select>";
                                } else {
                                    log.printLnToFile("(" + (rule.id) + ")" + "No Candidates Found for - " + premise.getName());
                                    premiseWordString += "<option>(Empty)</option></select>";
                                }
                                break;
                            }
                            case 3: {
                                break;
                            }
                        }
                    } else {
                        // 100% - Found exact matches
                        premiseWordString += "</p><select name=\"premise" + (rule.id) + "\">";
                        hmConcepts = cp.getConceptsRelated(ngramsList);
                        distancePercentage = 100.00;
                        for (int i2 = 0, i3 = hmConcepts.size(); i2 < i3; i2++) {
                            premiseWordList = hmConcepts.get(ngramsList.get(i2));
                            for (int j2 = 0, j3 = premiseWordList.size(); j2 < j3; j2++) {
                                if (!premiseWordString.contains(premiseWordList.get(j2))) {
                                    premiseWordString += "<option class=\"level0\" value=\"" + premiseWordList.get(j2) + "\">" + premiseWordList.get(j2).replace("_Individual", "").replace("_", " ") + " (100"/*+df.format(distancePercentage)*/ + "%)</option>";
                                    allConceptsRelatedAL.add(premiseWordList.get(j2));
                                    allConceptsRelatedSimilarityHM.put(premiseWordList.get(j2), distancePercentage);
                                    // set content
																		optionElement.setAttribute("exactmatch", "YES");
                                    optionNameElement.setText(premiseWordList.get(j2).toString());
                                    optionDistanceElement.setText(df.format(distancePercentage));
                                    // add content
																		optionElement.addContent(optionNameElement);
                                    optionElement.addContent(optionDistanceElement);
                                    optionsElement.addContent(optionElement);
                                    // reset elements
																		optionElement = new Element(optionStr);
                                    optionNameElement = new Element(nameStr);
                                    optionDistanceElement = new Element("distance");
                                }
                            }
                        }
                        // Will discover next the not 100% exact matches 
                        if (ngramsWordCount == 1) {
                            ngramsListHM = cp.getOnegramCandidatesListV3(premise.getName());
                            if (!ngramsListHM.isEmpty()) {
                                //for (int m=1; m<=ngramsListHM.size(); m++) {
                                if (!ngramsListHM.get(premise.getName()).isEmpty()) {
                                    hmConcepts = cp.getConceptsRelated(ngramsListHM.get(premise.getName()));
                                    if (!hmConcepts.isEmpty()) {
                                        for (int n = 0; n < hmConcepts.size(); n++) {
                                            premiseWordList = hmConcepts.get(ngramsListHM.get(premise.getName()).get(n));
                                            log.printLnToFile("(" + (rule.id) + ")" + "Showing " + ngramsListHM.get(premise.getName()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(premise.getName()).get(n)) + ") premiseWordList - " + premiseWordList);
                                            for (int m2 = 0; m2 < premiseWordList.size(); m2++) {
                                                if (!premiseWordString.contains(premiseWordList.get(m2))) {
                                                    distancePercentage = cp.getCosineSimilarity(cp.processWord(premise.getName(), "_"), cp.processWord(ngramsListHM.get(premise.getName()).get(n), " ")) * 100;
                                                    premiseWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + premiseWordList.get(m2) + "\">" + premiseWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                    allConceptsRelatedAL.add(premiseWordList.get(m2));
                                                    allConceptsRelatedSimilarityHM.put(premiseWordList.get(m2), distancePercentage);
                                                    log.printLnToFile("(" + (rule.id) + ") Common words Cosine similarity:" + "<" + premise.getName() + "><" + cp.processWord(ngramsListHM.get(premise.getName()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                    // set content
																										optionElement.setAttribute("exactmatch", "NO");
                                                    optionNameElement.setText(premiseWordList.get(m2).toString());
                                                    optionDistanceElement.setText(df.format(distancePercentage));
                                                    // add content 
																										optionElement.addContent(optionNameElement);
                                                    optionElement.addContent(optionDistanceElement);
                                                    optionsElement.addContent(optionElement);
                                                    // reset elements
																										optionElement = new Element(optionStr);
                                                    optionNameElement = new Element(nameStr);
                                                    optionDistanceElement = new Element("distance");
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    log.printLnToFile("(" + (rule.id) + ")" + "No candidates found for ->" + ngramsListHM.get(premise.getName()));
                                }
                                //}
                                // premiseWordString += "</select>";
                            } else {
                                log.printLnToFile("(" + (rule.id) + ")" + "Only found 100% match for - " + premise.getName());
                                premiseWordString += "<option>(Empty)</option></select>";
                            }
                        } else if (ngramsWordCount == 2) {
                            ngramsListHM = new HashMap<String, ArrayList<String>>();
                            ArrayList<String> keywordList = cp.processWord(premise.getName(), "_");
                            ArrayList<String> tmpList = new ArrayList<String>();
                            for (int wordI = 0; wordI < ngramsWordCount; wordI++) {
                                switch (wordI) {
                                    case 0: {
                                        ngramsListHM.put(premise.getName(), cp.getOnegramCandidatesListV3(keywordList.get(wordI)).get(keywordList.get(wordI)));
                                        break;
                                    }
                                    case 1: {
                                        if (!ngramsListHM.isEmpty()) {
                                            tmpList = cp.getOnegramCandidatesListV3(keywordList.get(wordI)).get(keywordList.get(wordI));
                                            for (int iterator = 0, ngramsListHMSize = ngramsListHM.get(premise.getName()).size(); iterator < ngramsListHMSize ; iterator++ )
                                                if (!tmpList.contains(ngramsListHM.get(premise.getName()).get(iterator)))
                                                    tmpList.add(ngramsListHM.get(premise.getName()).get(iterator));
                                            ngramsListHM.put(premise.getName(), tmpList);
                                        } else {
                                            ngramsListHM.put(premise.getName(), cp.getOnegramCandidatesListV3(keywordList.get(wordI)).get(keywordList.get(wordI)));
                                        }
                                        break;
                                    }
                                    default: {
                                        for (int m = 1, m2 = ngramsListHM.size(); m <= m2; m++) {
                                            if (!ngramsListHM.get(premise.getName()).isEmpty()) {
                                                tmpList = ngramsListHM.get(premise.getName());
                                                tmpList.addAll(cp.getOnegramCandidatesListV3(keywordList.get(wordI)).get(premise.getName()));
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            log.printLnToFile("(" + (rule.id) + ")" + "Bigram (" + premise.getName() + ") ngramslist" + ngramsListHM);
                            if (!ngramsListHM.isEmpty()) {
                                //for (int m=1, n2=ngramsListHM.size(); m<=n2; m++) {
                                log.printLnToFile("(" + (rule.id) + ")" + "Gets arrayLists for keyword=" + premise.getName() + "/" + ngramsListHM.size());
                                if (!ngramsListHM.get(premise.getName()).isEmpty()) {
                                    // log.printLnToFile("("+(i+1)+")"+ "Gets level1 and level2 arrayLists. Passagem: m="+m+"/"+ngramsListHM.size());
                                    hmConcepts = cp.getConceptsRelated(ngramsListHM.get(premise.getName()));

                                    if (!hmConcepts.isEmpty()) {
                                        for (int n = 0, n1 = hmConcepts.size(); n < n1; n++) {
                                            premiseWordList = hmConcepts.get(ngramsListHM.get(premise.getName()).get(n));
                                            log.printLnToFile("(" + (rule.id) + ")" + "Showing " + ngramsListHM.get(premise.getName()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(premise.getName()).get(n)) + ") premiseWordList - " + premiseWordList);
                                            for (int m2 = 0, m3 = premiseWordList.size(); m2 < m3; m2++) {
                                                if (!premiseWordString.contains(premiseWordList.get(m2))) {
                                                    distancePercentage = cp.getCosineSimilarity(cp.processWord(premise.getName(), "_"), cp.processWord(ngramsListHM.get(premise.getName()).get(n), " ")) * 100;
                                                    allConceptsRelatedAL.add(premiseWordList.get(m2));
                                                    allConceptsRelatedSimilarityHM.put(premiseWordList.get(m2), distancePercentage);
                                                    premiseWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + premiseWordList.get(m2) + "\">" + premiseWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                    log.printLnToFile("(" + (rule.id) + ") Common words Cosine similarity:" + "<" + premise.getName() + "><" + cp.processWord(ngramsListHM.get(premise.getName()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                    // set content
																										optionElement.setAttribute("exactmatch", "NO");
                                                    optionNameElement.setText(premiseWordList.get(m2).toString());
                                                    optionDistanceElement.setText(df.format(distancePercentage));
                                                    // add content
																										optionElement.addContent(optionNameElement);
                                                    optionElement.addContent(optionDistanceElement);
                                                    optionsElement.addContent(optionElement);
                                                    // reset elements
																										optionElement = new Element(optionStr);
                                                    optionNameElement = new Element(nameStr);
                                                    optionDistanceElement = new Element("distance");
                                                }
                                            }

                                        }
                                    }
                                } else {
                                    log.printLnToFile("(" + (rule.id) + ")" + "No candidates found for ->" + ngramsListHM.get(premise.getName()));
                                }
                                //}
                                //premiseWordString += "</select>";
                            } else {
                                log.printLnToFile("(" + (rule.id) + ")" + "No Candidates Found for - " + premise.getName());
                                premiseWordString += "<option>(Empty)</option>";
                            }
                        }
                        premiseWordString += "</select>";
                    }
                    log.printLnToFile("(" + (rule.id) + ")premiseWordString: " + premiseWordString);
                    if ((!allConceptsRelatedAL.isEmpty()) && (!allConceptsRelatedSimilarityHM.isEmpty()) ){
                        allConceptsRelatedHM.put(premise.getName(), allConceptsRelatedAL);
                        allConceptsRelatedSimilarityKeywordHM.put(premise.getName(), allConceptsRelatedSimilarityHM);
                    }
                } else {
                    allConceptsRelatedAL = allConceptsRelatedHM.get(premise.getName());
                    allConceptsRelatedSimilarityHM = allConceptsRelatedSimilarityKeywordHM.get(premise.getName());
                    if (!allConceptsRelatedSimilarityHM.containsValue(100.0) ){
                        premiseWordString += " (exact match not found) Candidates:";
                        optionElement.setAttribute("exactmatch", "NO");
                    }
                    else {
                        optionElement.setAttribute("exactmatch", "YES");
                    }
                    premiseWordString += "</p><select name=\"premise" + (rule.id) + "\">";
                    for (int i3 = 0, k3 = allConceptsRelatedAL.size(); i3 < k3; i3++) {
                        Double similarity = allConceptsRelatedSimilarityHM.get(allConceptsRelatedAL.get(i3));
                        String similarityString = "100";

                        if (similarity != 100) {
                            similarityString = df.format(similarity);
                        } 

                        // set content
                        optionNameElement.setText(allConceptsRelatedAL.get(i3).toString());
                        optionDistanceElement.setText(df.format(similarity));
                        // add content
												optionElement.addContent(optionNameElement);
                        optionElement.addContent(optionDistanceElement);
                        optionsElement.addContent(optionElement);
                        // reset elements
												optionElement = new Element(optionStr);
                        optionNameElement = new Element(nameStr);
                        optionDistanceElement = new Element("distance");

                        premiseWordString += "<option class=\"" + cp.getSimilarityClass(similarity) + "\"value=\"" + allConceptsRelatedAL.get(i3) + "\">" + allConceptsRelatedAL.get(i3).replace("_Individual", "").replace("_", " ") + " (" + similarityString + "%)</option>";
                        log.printLnToFile("(" + (rule.id) + ") Keyword already processed:" + premise.getName() + "Inserting concept related - " + allConceptsRelatedAL.get(i3));
                    }
                    premiseWordString += "</select>";
                }
                optionsElement.setAttribute("options_count", Integer.toString(allConceptsRelatedAL.size()));
                conceptElement.addContent(optionsElement);
                
                ruleElement.addContent(conceptElement);
                
                conceptElement = new Element(conceptStr);
                optionsElement = new Element(optionsStr);
                keywordElement = new Element(keywordStr);
                optionElement = new Element(optionStr);
                optionNameElement = new Element(nameStr);
                optionDistanceElement = new Element("distance");

                allConceptsRelatedAL = new ArrayList<String>();
                allConceptsRelatedSimilarityHM = new HashMap<String, Double>();
                st2.stop();
                log.printLnToFile("(" + rule.id + ")" + " Premise <" + rule.getPremise() + "> Conclusion <" + rule.getConclusion() + "> Premise time: "+elapsedTimeInSec(st2)+"s");
// -------------------------------------  End of Premise words finding        
                

// -------------------------------------  Conclusion words finding
                st2.reset();
                st2.start();

                if (i == 4) {
                    log.printLnToFile("-------------------------------------- I'm in rule (" + (rule.id) + ") ---------------------------------------------");
                }

                // conclusion = rulesHM.get(rule.id).get(1);
                conclusion = rule.getConclusion();
                conclusionWordString = "<p id=\"conclusion_" + (rule.id) + "\">" + conclusion.getName();
                
                conceptElement.setAttribute("value", "conclusion");

                keywordElement.setText(conclusion.getName());
                conceptElement.addContent(keywordElement);
                
                
                if (!allConceptsRelatedHM.containsKey(conclusion.getName())) {
                    ngramsWordCount = cp.processWord(conclusion.getName(), "_").size();
                    ngramsList = cp.getNgramList(conclusion.getName());

                    if (ngramsList.isEmpty()) {  // If the conclusion word did not found any exact match within the ontology, tries others
                        conclusionWordString += " (exact match not found) Candidates:</p><select name=\"conclusion" + (rule.id) + "\" id=\"conclusion" + (rule.id) + "\">";
                        switch (ngramsWordCount) {
                            case 1: {
                                ngramsListHM = cp.getOnegramCandidatesListV3(conclusion.getName());
                                if (!ngramsListHM.isEmpty()) {
                                        if (!ngramsListHM.get(conclusion.getName()).isEmpty()) {
                                            hmConcepts = cp.getConceptsRelated(ngramsListHM.get(conclusion.getName()));

                                            for (int n = 0, n2 = hmConcepts.size(); n < n2; n++) {
                                                if (!hmConcepts.isEmpty()) {
                                                    conclusionWordList = hmConcepts.get(ngramsListHM.get(conclusion.getName()).get(n));
                                                    log.printLnToFile("(" + (rule.id) + ")" + "Showing " + ngramsListHM.get(conclusion.getName()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(conclusion.getName()).get(n)) + ") conclusionWordList - " + conclusionWordList);
                                                    for (int m2 = 0, k3 = conclusionWordList.size(); m2 < k3; m2++) {
                                                        if (!conclusionWordString.contains(conclusionWordList.get(m2))) {
                                                            distancePercentage = cp.getCosineSimilarity(cp.processWord(conclusion.getName(), "_"), cp.processWord(ngramsListHM.get(conclusion.getName()).get(n), " ")) * 100;
                                                            allConceptsRelatedAL.add(conclusionWordList.get(m2));
                                                            allConceptsRelatedSimilarityHM.put(conclusionWordList.get(m2), distancePercentage);
                                                            conclusionWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + conclusionWordList.get(m2) + "\">" + conclusionWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                            log.printLnToFile("(" + (rule.id) + ") Common words Cosine similarity:" + "<" + conclusion.getName() + "><" + cp.processWord(ngramsListHM.get(conclusion.getName()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                            // set content
																														optionElement.setAttribute("exactmatch", "NO");
                                                            optionNameElement.setText(conclusionWordList.get(m2).toString());
                                                            optionDistanceElement.setText(df.format(distancePercentage));
                                                            // add content
																														optionElement.addContent(optionNameElement);
                                                            optionElement.addContent(optionDistanceElement);
                                                            optionsElement.addContent(optionElement);
                                                            // reset Elements
																														optionElement = new Element(optionStr);
                                                            optionNameElement = new Element(nameStr);
                                                            optionDistanceElement = new Element("distance");
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            log.printLnToFile("(" + (rule.id) + ")" + "No candidates found for ->" + ngramsListHM.get(conclusion.getName()));
                                        }
                                    conclusionWordString += "</select>";
                                } else {
                                    log.printLnToFile("(" + (rule.id) + ")" + "No Candidates Found for - " + conclusion.getName());
                                    conclusionWordString += "<option>(Empty)</option></select>";
                                    conclusionWordString += "<a href=# onclick=\"javascript:conceptsTree('conclusion_" + (rule.id) + "');\">New...</a>";
                                }
                                break;
                            }
                            case 2: {
                                ngramsListHM = new HashMap<String, ArrayList<String>>();
                                ArrayList<String> keywordList = cp.processWord(conclusion.getName(), "_");
                                for (int wordI = 0; wordI < ngramsWordCount; wordI++) {
                                    ngramsListHM.putAll(cp.getOnegramCandidatesListV3(keywordList.get(wordI)));
                                }
                                log.printLnToFile("(" + (rule.id) + ")" + "Bigram (" + conclusion.getName() + ") ngramslist" + ngramsListHM);
                                if (!ngramsListHM.isEmpty()) {
                                    if (!ngramsListHM.get(conclusion.getName()).isEmpty()) {
                                        hmConcepts = cp.getConceptsRelated(ngramsListHM.get(conclusion.getName()));
                                        for (int n = 0, nn1 = hmConcepts.size(); n < nn1; n++) {
                                            if (!hmConcepts.isEmpty()) {
                                                conclusionWordList = hmConcepts.get(ngramsListHM.get(conclusion.getName()).get(n));
                                                log.printLnToFile("(" + (rule.id) + ")" + "Showing " + ngramsListHM.get(conclusion.getName()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(conclusion.getName()).get(n)) + ") conclusionWordList - " + conclusionWordList);
                                                for (int m2 = 0, mm2 = conclusionWordList.size(); m2 < mm2; m2++) {
                                                    if (!conclusionWordString.contains(conclusionWordList.get(m2))) {
                                                        distancePercentage = cp.getCosineSimilarity(cp.processWord(conclusion.getName(), "_"), cp.processWord(ngramsListHM.get(conclusion.getName()).get(n), " ")) * 100;
                                                        allConceptsRelatedAL.add(conclusionWordList.get(m2));
                                                        allConceptsRelatedSimilarityHM.put(conclusionWordList.get(m2), distancePercentage);
                                                        conclusionWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + conclusionWordList.get(m2) + "\">" + conclusionWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                        log.printLnToFile("(" + (rule.id) + ") Common words Cosine similarity:" + "<" + conclusion.getName() + "><" + cp.processWord(ngramsListHM.get(conclusion.getName()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                        // set content
																												optionElement.setAttribute("exactmatch", "NO");
                                                        optionNameElement.setText(conclusionWordList.get(m2).toString());
                                                        optionDistanceElement.setText(df.format(distancePercentage));
                                                        // add content
																												optionElement.addContent(optionNameElement);
                                                        optionElement.addContent(optionDistanceElement);
                                                        optionsElement.addContent(optionElement);
                                                        // reset elements
																												optionElement = new Element(optionStr);
                                                        optionNameElement = new Element(nameStr);
                                                        optionDistanceElement = new Element("distance");
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        log.printLnToFile("(" + (rule.id) + ")" + "No candidates found for ->" + ngramsListHM.get(conclusion.getName()));
                                    }
                                    conclusionWordString += "</select>";
                                } else {
                                    log.printLnToFile("(" + (rule.id) + ")" + "No Candidates Found for - " + conclusion.getName());
                                    conclusionWordString += "<option>(Empty)</option></select>";
                                }
                                break;
                            }
                            case 3: {
                                // ngramsList = cp.getTrigramCandidatesList (conclusion.getName());
                                break;
                            }
                        }
                    } else {
                        // 100% - Found exact matches - search for corresponding concepts
                        conclusionWordString += "</p><select name=\"conclusion" + (rule.id) + "\">";
                        hmConcepts = cp.getConceptsRelated(ngramsList);
                        distancePercentage = 100.00;
                        for (int i2 = 0, ii2 = hmConcepts.size(); i2 < ii2; i2++) {
                            conclusionWordList = hmConcepts.get(ngramsList.get(i2));
                            for (int j2 = 0, mm = conclusionWordList.size(); j2 < mm; j2++) {
                                if (!conclusionWordString.contains(conclusionWordList.get(j2))) {
                                    conclusionWordString += "<option class=\"level0\" value=\"" + conclusionWordList.get(j2) + "\">" + conclusionWordList.get(j2).replace("_Individual", "").replace("_", " ") + " (100%)</option>";
                                    allConceptsRelatedAL.add(conclusionWordList.get(j2));
                                    allConceptsRelatedSimilarityHM.put(conclusionWordList.get(j2), distancePercentage);
                                    //set content
																		optionElement.setAttribute("exactmatch", "YES");
                                    optionNameElement.setText(conclusionWordList.get(j2).toString());
                                    optionDistanceElement.setText(df.format(distancePercentage));
                                    //add content
																		optionElement.addContent(optionNameElement);
                                    optionElement.addContent(optionDistanceElement);
                                    optionsElement.addContent(optionElement);
                                    // reset elements
																		optionElement = new Element(optionStr);
                                    optionNameElement = new Element(nameStr);
                                    optionDistanceElement = new Element("distance");
                                }
                            }
                            // allConceptsRelatedHM.put(conclusion.getName(), allConceptsRelatedAL);
                            // allConceptsRelatedSimilarityKeywordHM.put(conclusion.getName(), allConceptsRelatedSimilarityHM);
                        }

                        // Gets similar matches (not 100%) and search corresponding 
                        if (ngramsWordCount == 1) {
                            ngramsListHM = cp.getOnegramCandidatesListV3(conclusion.getName());

                            if (!ngramsListHM.isEmpty()) {
                                    hmConcepts = cp.getConceptsRelated(ngramsListHM.get(conclusion.getName()));
                                    if (!hmConcepts.isEmpty()) {
                                        for (int n = 0, nn3 = hmConcepts.size(); n < nn3; n++) {
                                            conclusionWordList = hmConcepts.get(ngramsListHM.get(conclusion.getName()).get(n));
                                            log.printLnToFile("(" + (rule.id) + ")" + "Showing " + ngramsListHM.get(conclusion.getName()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(conclusion.getName()).get(n)) + ") conclusionWordList - " + conclusionWordList);
                                            for (int m2 = 0, mm4 = conclusionWordList.size(); m2 < mm4; m2++) {
                                                if (!conclusionWordString.contains(conclusionWordList.get(m2))) {
                                                    distancePercentage = cp.getCosineSimilarity(cp.processWord(conclusion.getName(), "_"), cp.processWord(ngramsListHM.get(conclusion.getName()).get(n), " ")) * 100;
                                                    allConceptsRelatedAL.add(conclusionWordList.get(m2));
                                                    allConceptsRelatedSimilarityHM.put(conclusionWordList.get(m2), distancePercentage);
                                                    conclusionWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + conclusionWordList.get(m2) + "\">" + conclusionWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                    log.printLnToFile("(" + (rule.id) + ") Common words Cosine similarity:" + "<" + conclusion.getName() + "><" + cp.processWord(ngramsListHM.get(conclusion.getName()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                    // set content
																										optionElement.setAttribute("exactmatch", "NO");
                                                    optionNameElement.setText(conclusionWordList.get(m2).toString());
                                                    optionDistanceElement.setText(df.format(distancePercentage));
                                                    // add content
																										optionElement.addContent(optionNameElement);
                                                    optionElement.addContent(optionDistanceElement);
                                                    optionsElement.addContent(optionElement);
                                                    // reset elements
																										optionElement = new Element(optionStr);
                                                    optionNameElement = new Element(nameStr);
                                                    optionDistanceElement = new Element("distance");
                                                }
                                            }
                                        }
                                    }
                                    //else {
                                    //log.printLnToFile("("+(i+1)+")"+"No candidates found for ->"+ngramsListHM.get(m));
                                    //}
                                // conclusionWordString += "</select>";
                            } else {
                                log.printLnToFile("(" + (rule.id) + ")" + "No Candidates Found for - " + conclusion.getName());
                                conclusionWordString += "<option>(Empty)</option></select>";
                            }
                        } else if (ngramsWordCount == 2) {
                            ngramsListHM = new HashMap<String, ArrayList<String>>();
                            ArrayList<String> keywordList = cp.processWord(conclusion.getName(), "_");
                            ArrayList<String> tmpList = new ArrayList<String>();
                            for (int wordI = 0; wordI < ngramsWordCount; wordI++) {
                                switch (wordI) {
                                    case 0: {
                                        ngramsListHM.put(conclusion.getName(), cp.getOnegramCandidatesListV3(keywordList.get(wordI)).get(keywordList.get(wordI)));
                                        break;
                                    }
                                    case 1: {
                                        if (!ngramsListHM.isEmpty()) {
                                            tmpList = cp.getOnegramCandidatesListV3(keywordList.get(wordI)).get(keywordList.get(wordI));
                                            for (int iterator = 0, ngramsListHMSize = ngramsListHM.get(conclusion.getName()).size(); iterator < ngramsListHMSize ; iterator++ )
                                                if (!tmpList.contains(ngramsListHM.get(conclusion.getName()).get(iterator)))
                                                    tmpList.add(ngramsListHM.get(conclusion.getName()).get(iterator));
                                            ngramsListHM.put(conclusion.getName(), tmpList);
                                        } else {
                                            ngramsListHM.put(conclusion.getName(), cp.getOnegramCandidatesListV3(keywordList.get(wordI)).get(keywordList.get(wordI)));
                                        }
                                        break;
                                    }
                                    default: {
                                        for (int m = 1, m2 = ngramsListHM.size(); m <= m2; m++) {
                                            if (!ngramsListHM.get(conclusion.getName()).isEmpty()) {
                                                tmpList = ngramsListHM.get(conclusion.getName());
                                                tmpList.addAll(cp.getOnegramCandidatesListV3(keywordList.get(wordI)).get(conclusion.getName()));
                                            }
                                        }
                                        break;
                                    }
                                }

                            }
                            log.printLnToFile("(" + (rule.id) + ")" + "Bigram (" + conclusion.getName() + ") ngramslist" + ngramsListHM);
                            if (!ngramsListHM.isEmpty()) {
                                //for (int m=1, n2=ngramsListHM.size(); m<=n2; m++) {
                                log.printLnToFile("(" + (rule.id) + ")" + "Gets arrayLists for keyword=" + conclusion.getName() + "/" + ngramsListHM.size());

                                if (!ngramsListHM.get(conclusion.getName()).isEmpty()) {
                                    // log.printLnToFile("("+(i+1)+")"+ "Gets level1 and level2 arrayLists. Passagem: m="+m+"/"+ngramsListHM.size());
                                    hmConcepts = cp.getConceptsRelated(ngramsListHM.get(conclusion.getName()));
                                        // TODO: Verify sizes of hmConcepts vs ngramsLinsHM.get(conclusion.getName())
                                    if (!hmConcepts.isEmpty()) {
                                        for (int n = 0, n1 = hmConcepts.size(); n < n1; n++) {
                                            conclusionWordList = hmConcepts.get(ngramsListHM.get(conclusion.getName()).get(n));
                                            log.printLnToFile("(" + (rule.id) + ")" + "Showing " + ngramsListHM.get(conclusion.getName()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(conclusion.getName()).get(n)) + ") conclusionWordList - " + conclusionWordList);
                                            for (int m2 = 0, m3 = conclusionWordList.size(); m2 < m3; m2++) {
                                                if (!conclusionWordString.contains(conclusionWordList.get(m2))) {
                                                    distancePercentage = cp.getCosineSimilarity(cp.processWord(conclusion.getName(), "_"), cp.processWord(ngramsListHM.get(conclusion.getName()).get(n), " ")) * 100;
                                                    allConceptsRelatedAL.add(conclusionWordList.get(m2));
                                                    allConceptsRelatedSimilarityHM.put(conclusionWordList.get(m2), distancePercentage);
                                                    conclusionWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + conclusionWordList.get(m2) + "\">" + conclusionWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                    log.printLnToFile("(" + (rule.id) + ") Common words Cosine similarity:" + "<" + conclusion.getName() + "><" + cp.processWord(ngramsListHM.get(conclusion.getName()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                    optionElement.setAttribute("exactmatch", "NO");
                                                    optionNameElement.setText(conclusionWordList.get(m2).toString());
                                                    optionDistanceElement.setText(df.format(distancePercentage));
                                                    optionElement.addContent(optionNameElement);
                                                    optionElement.addContent(optionDistanceElement);
                                                    optionsElement.addContent(optionElement);
                                                    optionElement = new Element(optionStr);
                                                    optionNameElement = new Element(nameStr);
                                                    optionDistanceElement = new Element("distance");
                                                }
                                            }

                                        }
                                    }
                                } else {
                                    log.printLnToFile("(" + (rule.id) + ")" + "No candidates found for ->" + ngramsListHM.get(conclusion.getName()));
                                }
                                //}
                                //conclusionWordString += "</select>";
                            } else {
                                log.printLnToFile("(" + (rule.id) + ")" + "No Candidates Found for - " + conclusion.getName());
                                conclusionWordString += "<option>(Empty)</option>";
                            }
                        }
                        conclusionWordString += "</select>";
                    }
                    log.printLnToFile("(" + (rule.id) + ")conclusionWordString: " + conclusionWordString);

                    if  ( (!allConceptsRelatedAL.isEmpty()) && (!allConceptsRelatedSimilarityHM.isEmpty()) ){
                        allConceptsRelatedHM.put(conclusion.getName(), allConceptsRelatedAL);
                        allConceptsRelatedSimilarityKeywordHM.put(conclusion.getName(), allConceptsRelatedSimilarityHM);
                    }
                } else {
                    
                    allConceptsRelatedAL = allConceptsRelatedHM.get(conclusion.getName());
                    allConceptsRelatedSimilarityHM = allConceptsRelatedSimilarityKeywordHM.get(conclusion.getName());
                    if (!allConceptsRelatedSimilarityHM.containsValue(100.0) )
                        conclusionWordString += " (exact match not found) Candidates:";
                    conclusionWordString += "</p><select name=\"conclusion" + (rule.id) + "\">";
                    
                    for (int i3 = 0, i4 = allConceptsRelatedSimilarityHM.size(); i3 < i4; i3++) {
                        Double similarity = allConceptsRelatedSimilarityHM.get(allConceptsRelatedAL.get(i3));
                        String similarityString = "100";

                        if (similarity != 100) {
                            similarityString = df.format(similarity);
                            optionElement.setAttribute("exactmatch", "NO");
                        }
                        else {
                            optionElement.setAttribute("exactmatch", "YES");
                        }
                            

                        conclusionWordString += "<option class=\"" + cp.getSimilarityClass(similarity) + "\"value=\"" + allConceptsRelatedAL.get(i3) + "\">" + allConceptsRelatedAL.get(i3).replace("_Individual", "").replace("_", " ") + " (" + similarityString + "%)</option>";
                        log.printLnToFile("(" + (rule.id) + ") Keyword already processed:" + conclusion.getName() + "Inserting concept related - " + allConceptsRelatedAL.get(i3));
                        
                        optionNameElement.setText(allConceptsRelatedAL.get(i3).toString());
                        optionDistanceElement.setText(df.format(similarity));
                        optionElement.addContent(optionNameElement);
                        optionElement.addContent(optionDistanceElement);
                        optionsElement.addContent(optionElement);
                        optionElement = new Element(optionStr);
                        optionNameElement = new Element(nameStr);
                        optionDistanceElement = new Element("distance");
                    }
                    conclusionWordString += "</select>";
                }
                
                optionsElement.setAttribute("options_count", Integer.toString(allConceptsRelatedAL.size()));
                conceptElement.addContent(optionsElement);
                ruleElement.addContent(conceptElement);
                
                allConceptsRelatedAL = new ArrayList<String>();
                allConceptsRelatedSimilarityHM = new HashMap<String, Double>();
                
                st2.stop();
                log.printLnToFile("(" + rule.id + ")" + " Premise <" + rule.getPremise().getName() + "> Conclusion <" + rule.getConclusion().getName() + "> Conclusion time: "+elapsedTimeInSec(st2)+"s");
// -------------------------------------  End of Conclusion words finding        

                
                conviction = rule.getConviction();
                gain = rule.getGain();
                lift = rule.getLift();
                laplace = rule.getLaplace();
                ps = rule.getPs();
                totalSupport = rule.getTotalsupport();
                confidence = rule.getConfidence();
                
/*
                gain = rulesHM.get(i + 1).get(3);
                lift = rulesHM.get(i + 1).get(4);
                laplace = rulesHM.get(i + 1).get(5);
                ps = rulesHM.get(i + 1).get(6);
                totalSupport = rulesHM.get(i + 1).get(7);
                confidence = rulesHM.get(i + 1).get(8);
*/
                convictionXML.addContent(conviction.toString());
                metricsElement.addContent(convictionXML);

                supportXML.addContent(totalSupport.toString());
                metricsElement.addContent(supportXML);

                confidenceXML.addContent(confidence.toString());
                metricsElement.addContent(confidenceXML);

                gainXML.addContent(gain.toString());
                metricsElement.addContent(gainXML);

                liftXML.addContent(lift.toString());
                metricsElement.addContent(liftXML);

                psXML.addContent(ps.toString());
                metricsElement.addContent(psXML);

                laplaceXML.addContent(laplace.toString());
                metricsElement.addContent(laplaceXML);

                ruleElement.addContent(metricsElement);
                rulesElement.setAttribute("rule_count", Integer.toString(i+1));
                rulesElement.addContent(ruleElement);

                // HTML Vector construction 
                log.printLnToFile("(" + (rule.id) + ")" + "conviction:" + conviction + "-gain:" + gain + "-lift:" + lift + "-laplace:" + laplace + "-ps:" + ps + "-total Support:" + totalSupport + "-confidence:" + confidence);

                resultsconcepts += "<div class=\"rulebox\" id=\"rulebox" + (rule.id) + "\"><form name=\"saveonerule\" action=\"rulessaved.jsp\" method=\"post\" target=\"_blank\">"
                        + "<p class=\"num_rules\">Rule #" + (rule.id) + "</p><ul>"
                        + "<li title=\"Premise\"><div class=\"premisetitle\">Premise</div><div class=\"premisevalue\">" + premiseWordString + "</div></li>"
                        + "<li title=\"Conclusion\"><div class=\"conclusiontitle\">Conclusion</div><div class=\"conclusionvalue\">" + conclusionWordString + "</div></li></ul>"
                        + "<ul><li title=\"Confidence\" class=\"metrics\"><div class=\"metrictitle\">Confidence</div><div class=\"metricvalue\" name=\"confidence\">" + confidence + "</div></li>"
                        + "<li title=\"Conviction\" class=\"metrics\"><div class=\"metrictitle\">Conviction</div><div class=\"metricvalue\" name=\"conviction\">";
                
                String convictiondata;
                if (conviction.toString().contains("8888")) {
                    resultsconcepts += "Infinity";
                    convictiondata = "Infinity";
                } else {
                    resultsconcepts += conviction;
                    convictiondata = conviction.toString();
                }

                resultsconcepts += "</div></li>"
                        + "<li title=\"Gain\" class=\"metrics\"><div class=\"metrictitle\">Gain</div><div class=\"metricvalue\">" + gain + "</div></li>"
                        + "<li title=\"Laplace\" class=\"metrics\"><div class=\"metrictitle\">Laplace</div><div class=\"metricvalue\">" + laplace + "</div></li>"
                        + "<li title=\"Lift\" class=\"metrics\"><div class=\"metrictitle\">Lift</div><div class=\"metricvalue\">" + lift + "</div></li>"
                        + "<li title=\"Ps\" class=\"metrics\"><div class=\"metrictitle\">PS</div><div class=\"metricvalue\">" + ps + "</div></li>"
                        + "<li title=\"Total Support\" class=\"metrics\"><div class=\"metrictitle\">Support</div><div class=\"metricvalue\">" + totalSupport + "</div></li>"
                        + "<li title=\"Insert rule\"><div class=\"metrictitle\"><input type=\"checkbox\" name=\"rule\" value=\"" + (rule.id) + "\"></div><div class=\"metricvalue\"><input type=\"submit\" value=\"Add rule to DB\" /></div></li>"
                        + "</ul></form></div>";
                st.stop();
                log.printLnToFile("(" + (rule.id) + ")" + " Finish: Premise <" + Rules.get(rule.id).getPremise().getName() 
                        + "> Conclusion <" + Rules.get(rule.id).getConclusion().getName() + "> Rule time:"+st.getTime());

                logData.printLnToFile((i+1)+","+premise.getName()+","+conclusion.getName()+","
                        +confidence.toString()+","+convictiondata.toString()+","+gain+","+laplace+","+lift+","+ps+","+totalSupport);

                
                // XML Cleaning xml elements for new rule
                
                ruleElement = new Element(ruleStr);

                conceptElement = new Element(conceptStr);
                optionsElement = new Element(optionsStr);
                keywordElement = new Element(keywordStr);
                optionsElement = new Element(optionsStr);
                optionElement = new Element(optionStr);
                optionNameElement = new Element(nameStr);
                optionDistanceElement = new Element("distance");
                
                metricsElement = new Element("metrics");
                supportXML = new Element("support");
                confidenceXML = new Element("confidence");
                convictionXML = new Element("conviction");
                gainXML = new Element("gain");
                liftXML = new Element(liftStr);
                psXML = new Element("ps");
                laplaceXML = new Element("laplace");
                
                
            }
            log.printLnToFile("----->>> all unique ConceptsTotal : " + allConceptsRelatedHM.size());
        } else {
            log.printLnToFile("There are no rules in database.");
        }
        
        // Create XML document and format it in a more user-friendly readable way

        Document doc = new Document(rulesElement);

        Format prettyFormat = Format.getPrettyFormat();
        prettyFormat.setExpandEmptyElements(false);
        XMLOutputter prettyXmlOut = new XMLOutputter(prettyFormat);

        ruleXmlFile.openFile(RULE_XML_FILE);
        ruleXmlFile.printToFile(prettyXmlOut.outputString(doc));
        ruleXmlFile.closeFile();
        
        logData.closeFile();
        log.closeLogFile();
    }
    
    private double elapsedTimeInSec(StopWatch st)
    {
        double elapsed = st.getTime() / 1000.0;
        return elapsed;
    }
    
    private double elapsedTimeInMin(StopWatch st)
    {
        double elapsed = st.getTime() / 1000 / 60.0;
        return elapsed;
    }
    
    private void getRulesListFromDB() {
        dbRules = new Database(this.databaseURLName, this.databaseUserName, this.databasePasswordName, this.databaseDriverName, log);
        
        Connection con = dbRules.databaseConnect(this.databaseURLName, this.databaseUserName, this.databasePasswordName, this.databaseDriverName);
        Rules = dbRules.databaseGetRules2(con, this.tablenames[1]);
        RulesList = dbRules.databaseGetRules3(con, this.tablenames[1]);
        dbRules.databaseDisconnect(con);
    }
    
    public void processGetRulesFromDb() {
        dbRules = new Database(this.databaseURLName, this.databaseUserName, this.databasePasswordName, this.databaseDriverName, log);
        Connection con = dbRules.databaseConnect();
        HashMap<Integer, ArrayList<String>> rulesHM = new HashMap<Integer, ArrayList<String>>();
        ArrayList<String> ruleValuesAL = new ArrayList<String>();
        ruletosave = "<ul class=\"associationrule\"><li class=\"associationrule\">"
                + "<div class=\"associationrulenumber\">Rules</div>"
                + "<div class=\"associationruleconcept\">Premise</div>"
                + "<div class=\"associationruleconcept\">Conclusion</div>"
                + "<div class=\"associationrulemetric\">Conf.</div>"
                + "<div class=\"associationrulemetric\">Conv.</div>"
                + "<div class=\"associationrulemetric\">Gain</div>"
                + "<div class=\"associationrulemetric\">Lapl.</div>"
                + "<div class=\"associationrulemetric\">Lift</div>"
                + "<div class=\"associationrulemetric\">PS</div>"
                + "<div class=\"associationrulemetric\">Sup.</div>"
                + "</li>";

        rulesHM = dbRules.databaseGetAllRules(con);
        if (!rulesHM.isEmpty()) {
            for (int i = 1; i <= rulesHM.size(); i++) {
                ruleValuesAL = rulesHM.get(i);
                ruletosave += "<li class=\"associationrule\"><div class=\"associationrulenumber\">" + i + "</div>";
                if (!ruleValuesAL.isEmpty()) {
										String style="";
                    if (associationruleID == i) {
												style = "style=\"color: red;\"";
                    } 
										ruletosave += "<div title=\"Rule Selected\" class=\"associationruleconcept\" "+style+">" + ruleValuesAL.get(0) + "</div>"
														+ "<div title=\"Rule Selected\" class=\"associationruleconcept\" "+style+">" + ruleValuesAL.get(1) + "</div>"
														+ "<div title=\"Rule Selected\" class=\"associationrulemetric\" "+style+">" + ruleValuesAL.get(8) + "</div>"
														+ "<div title=\"Rule Selected\" class=\"associationrulemetric\" "+style+">" + ruleValuesAL.get(2) + "</div>"
														+ "<div title=\"Rule Selected\" class=\"associationrulemetric\" "+style+">" + ruleValuesAL.get(3) + "</div>"
														+ "<div title=\"Rule Selected\" class=\"associationrulemetric\" "+style+">" + ruleValuesAL.get(4) + "</div>"
														+ "<div title=\"Rule Selected\" class=\"associationrulemetric\" "+style+">" + ruleValuesAL.get(5) + "</div>"
														+ "<div title=\"Rule Selected\" class=\"associationrulemetric\" "+style+">" + ruleValuesAL.get(6) + "</div>"
														+ "<div title=\"Rule Selected\" class=\"associationrulemetric\" style=\"color: red;\">" + ruleValuesAL.get(7) + "</div>";
                }
                ruletosave += "</li>";
            }
        }
        ruletosave = ruletosave.replace("_Individual", "").replace("_", " ").replace("8888.0000", "Infinity") + "</ul>";
        dbRules.databaseDisconnect(con);
    }

    public void processInsertOneChoosenRuleInDB(Integer rulenumber, String premise1, String conclusion1) {
        dbRules = new Database(this.databaseURLName, this.databaseUserName, this.databasePasswordName, this.databaseDriverName, log);
        Connection con = dbRules.databaseConnect();
        // 1 verificar se a regra ja existe, se não, insere 
        // 1a verificar se os conceitos ja existem na bd, se não insere
        // inserir regra referenciando as métricas respectivas todas, ou referência aos mesmos
        ArrayList<String> rule = dbRules.databaseGetOneRule(con, rulenumber);
        //dbRules.databaseDisconnect(con);
        String[] concepts = {premiseString, conclusionString};
        String[] values = {""};
        String[] ruleStr = {"", "", ""};
        String query = "";
        int lastID = 0;
        int[] conceptID = {0, 0};
        int idcontain;
        associationruleID = 0;
        for (int m = 0; m < concepts.length; m++) {
            con = dbRules.databaseConnect(this.databaseURLName, this.databaseUserName, this.databasePasswordName, this.databaseDriverName);
            idcontain = dbRules.databaseContainsConcept(con, this.tablenames[3], nameStr, concepts[m]);
            //dbRules.databaseDisconnect(con);
            if (idcontain == 0) {
                con = dbRules.databaseConnect(this.databaseURLName, this.databaseUserName, this.databasePasswordName, this.databaseDriverName);
                query = "INSERT INTO concepts(idconcepts, name) values(?, ?)";
                lastID = dbRules.databaseGetTableLastID(con, this.tablenames[3], "idconcepts");
                values[0] = concepts[m];

                dbRules.databaseInsertOneDataRecord(con, query, 2, values, lastID);
                log.printLnToFile(concepts[m] + " - inserted in db with id " + (lastID + 1));
                conceptID[m] = lastID + 1;
                //dbRules.databaseDisconnect(con);
            } else {
                log.printLnToFile(concepts[m] + " - already in database with id " + idcontain);
                conceptID[m] = idcontain;
            }
        }

        ruleStr[0] = Integer.toString(conceptID[0]);
        ruleStr[1] = Integer.toString(conceptID[1]);
        ruleStr[2] = Integer.toString(rulenumber);
        log.printLnToFile("rulenumber (idrules) (" + rulenumber + ") conceptA:" + ruleStr[0] + " conceptB:" + ruleStr[1] + ", rulenumber (stemmed)" + ruleStr[2]);
        idcontain = dbRules.databaseContainsRule(con, ruleStr[0], ruleStr[1], ruleStr[2]);
        if (idcontain == 0) {
            lastID = dbRules.databaseGetTableLastID(con, this.tablenames[0], "idrules");
            query = "INSERT INTO rules(idrules, idConceptA, idConceptB, idrules_stemmed) values(?, ?, ?, ?)";
            associationruleID = lastID + 1;
            if (dbRules.databaseInsertOneDataRecord(con, query, 4, ruleStr, lastID)) {
                log.printLnToFile("rulenumber (" + rulenumber + ") - inserted in database with id " + lastID + 1);
            } else {
                log.printLnToFile("rulenumber (" + rulenumber + ") - something went wrong inserting in database");
            }
        } else {
            associationruleID = idcontain;
            log.printLnToFile("rulenumber (" + rulenumber + ") - already in database with id " + idcontain);
        }

        log.printLnToFile("All ok to here");
        dbRules.databaseDisconnect(con);
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    
    /**
     * @return the results
     */
    public String getResults() {
        rapidminerInit();
        processResults();
        return results;
    }

    /**
     * @param aResults the results to set
     */
    public void setResults(String aResults) {
        this.results = aResults;
    }

    /**
     * @return the results
     */
    public String getRuletosave() {
        //rapidminerInit();
        //processResults();
        //rulessavedlist = "I have something";
        this.processGetRulesFromDb();
        return ruletosave;
    }

    /**
     * @param aResults the results to set
     */
    public void setRuletosave(String aResults) {
        Integer rulenumber = Integer.parseInt(aResults);
        this.processInsertOneChoosenRuleInDB(rulenumber, premiseString, conclusionString);
    }

    /**
     * @return the results
     */
    public String getResultsconcepts() {
        //rapidminerInit();
        //processResultsConcepts();
        processResultsFromDatabaseV4();
        return resultsconcepts;
    }

    /**
     * @param aResults the results to set
     */
    public void setResultsconcepts(String aResults) {
        this.resultsconcepts = aResults;
    }

    public void setResultsToDatabase(String aResults) {
        this.resultsToDatabase = aResults;
    }

    public String getResultsToDatabase() {
        rapidminerInit();
        //processResultsToDatabase();
        processResultsToDatabaseV2();
        return resultsToDatabase;
    }

    public String getPremise() {
        return premiseString;
    }

    public void setPremise(String premise) {
        this.premiseString = premise;
    }

    public String getConclusion() {
        return conclusionString;
    }

    public void setConclusion(String conclusion) {
        this.conclusionString = conclusion;
    }
    //</editor-fold>
    
    public static void main(String[] args) {
        DinOntAssociationRules DOAR = new DinOntAssociationRules();
        DOAR.processResultsFromDatabaseV4();
        
        
        
    }
    
}
