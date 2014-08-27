/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dinont.associationrules;

import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.XMLException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

import dinont.utilities.Database;
import dinont.utilities.LogHandlerClass;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import seks.basic.ontology.OntologyInteractionImpl;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author Luis
 */
public class Core {
    public static class CorePathsStruct {
        public String RAPID_MINER_PROCESS_XML = "F:\\Dissertacao\\FrontEnd\\processXML\\process5.XML";
        public String ROOT_PATH = "F:\\NetBeansProjects\\HelloWeb\\";
        public String LOG_FILE = ROOT_PATH+"DARprocessResultsFromDatabaseV2log.txt";
        public String RULE_XML_FILE = ROOT_PATH+"web\\xml\\rules.xml";
    } 
    public static class DataBaseDetails {
        public String databaseURLName = "jdbc:mysql://localhost:3306/associationrulesdbv2";
        public String databaseDriverName = "com.mysql.jdbc.Driver";
        public String databaseUserName = "root";
        public String databasePasswordName = "";
    }
    
    private static CorePathsStruct _corePath;
    private static DataBaseDetails _dbDetails;
    
    // private String RAPID_MINER_PROCESS_XML = _corePath.RAPID_MINER_PROCESS_XML;
    // private String ROOT_PATH = "F:\\NetBeansProjects\\HelloWeb\\";
    // private String LOG_FILE = ROOT_PATH+"DARprocessResultsFromDatabaseV2log.txt";
    // private String RULE_XML_FILE = _corePath.ROOT_PATH+"web\\xml\\rules.xml";
    
    private String results = "";
    private String resultsConcepts = "";
    private String ruleToSave = "";
    private String resultsToDatabase = "false";
    private String premise = "";
    private String conclusion = "";
    
//    private String databaseURLName = "jdbc:mysql://localhost:3306/associationrulesdbv2";
//    private String databaseDriverName = "com.mysql.jdbc.Driver";
    private String databaseUserName = "root";
    private String databasePasswordName = "";
    private String[] tableNames = {"rules", "rules_stemmed", "stemmed_word", "concepts"};
    
    private static Process rm5;
    
    private OntologyInteractionImpl oi = new OntologyInteractionImpl();
    private Database dbRules = new Database();
    private Concepts cp = new Concepts(oi);
    
    private Integer associationruleID = 0;

    public Core() {
        premise = "empty_premise";
        conclusion = "empty_conclusion";
        ruleToSave = "empty_rule";
        results = "";
        resultsConcepts = "";
        resultsToDatabase = "false";
    }

    public void rapidminerInit() {
        RapidMiner.setExecutionMode(ExecutionMode.COMMAND_LINE);
        RapidMiner.init();
    }

    // Original name is processResults() - It's working, do not mess with it.
    public void processResults() {
        try {
            rm5 = new Process(new File(_corePath.RAPID_MINER_PROCESS_XML));
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
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
     
    public void processResultsToDatabaseV2() {
        int idstemmed_wordA, idstemmed_wordB, lastID_stemmed_word;

        String conclusionTMP, premiseTMP;
        Object premise, conclusion, confidence, conviction, laplace, gain, lift, ps, totalSupport;

        dbRules = new Database();
        Connection con = dbRules.databaseConnect(_dbDetails.databaseURLName, _dbDetails.databaseUserName, _dbDetails.databasePasswordName, _dbDetails.databaseDriverName);

        for (int idclean = 0, idclean2 = tableNames.length; idclean < idclean2; idclean++) {
            dbRules.databaseDeleteAllRecordsFromTable(con, tableNames[idclean]);
        }

        try {
            rm5 = new Process(new File(_corePath.RAPID_MINER_PROCESS_XML));

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

                        idstemmed_wordA = dbRules.databaseContainsConcept(con, this.tableNames[2], "stemmed_word", premise.toString());
                        if (idstemmed_wordA == 0) {
                            lastID_stemmed_word = dbRules.databaseGetTableLastID(con, this.tableNames[2], "idstemmed_word");
                            String[] values = {premise.toString()};
                            String query = "INSERT INTO stemmed_word(idstemmed_word, stemmed_word) values(?, ?)";
                            dbRules.databaseInsertOneDataRecord(con, query, 2, values, lastID_stemmed_word);
                            idstemmed_wordA = dbRules.databaseContainsConcept(con, this.tableNames[2], "stemmed_word", premise.toString());
                        }

                        conclusionTMP = (String) resultSet.getExample(i).get("Conclusion");
                        conclusionTMP = conclusionTMP.replace("[", "");
                        conclusion = conclusionTMP.replace("]", "");

                        idstemmed_wordB = dbRules.databaseContainsConcept(con, this.tableNames[2], "stemmed_word", conclusion.toString());
                        if (idstemmed_wordB == 0) {
                            lastID_stemmed_word = dbRules.databaseGetTableLastID(con, this.tableNames[2], "idstemmed_word");
                            String[] values = {conclusion.toString()};
                            String query = "INSERT INTO stemmed_word(idstemmed_word, stemmed_word) values(?, ?)";
                            dbRules.databaseInsertOneDataRecord(con, query, 2, values, lastID_stemmed_word);
                            idstemmed_wordB = dbRules.databaseContainsConcept(con, this.tableNames[2], "stemmed_word", conclusion.toString());
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
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        dbRules.databaseDisconnect(con);
    }


    public void processResultsFromDatabaseV4() {
        // Variable declaration section -----------------------
        Object premise, conclusion, confidence, conviction, laplace, gain, lift, ps, totalSupport;
        Connection con = dbRules.databaseConnect(this._dbDetails.databaseURLName, this._dbDetails.databaseUserName, this._dbDetails.databasePasswordName, this._dbDetails.databaseDriverName);

        HashMap<String, ArrayList<String>> hmConcepts = new HashMap<String, ArrayList<String>>();
        HashMap<String, ArrayList<String>> ngramsListHM = new HashMap<String, ArrayList<String>>();

        HashMap<Integer, ArrayList<String>> rulesHM = new HashMap<Integer, ArrayList<String>>();
        HashMap<String, ArrayList<String>> allConceptsRelatedHM = new HashMap<String, ArrayList<String>>();

        HashMap<String, Double> allConceptsRelatedSimilarityHM = new HashMap<String, Double>();

        HashMap<String, HashMap<String, Double>> allConceptsRelatedSimilarityKeywordHM = new HashMap<String, HashMap<String, Double>>();

        ArrayList<String> allConceptsRelatedAL = new ArrayList<String>();
        ArrayList<String> ngramsList, premiseWordList, conclusionWordList;

        LogHandlerClass log = new LogHandlerClass();
        LogHandlerClass ruleXmlFile = new LogHandlerClass();
        DecimalFormat df = new DecimalFormat("##0.00");

        Double distancePercentage;
        String premiseWordString, conclusionWordString;
        String xml = "";
        
        Integer ngramsWordCount;

        log.openLogFile(_corePath.LOG_FILE);
        
        
        // XML creation - creates the elements necessary for the XML Rules file
        
        Element rules = new Element("rules");

        Element creationinfo = new Element("creationinfo");
        
        Element rule = new Element("rule");
        Element concept = new Element("concept");
        Element keyword = new Element("keyword");
        Element options = new Element("options");
        Element option = new Element("option");
        Element optionName = new Element("name");
        Element optionDistance = new Element("distance");
        
        Element metrics = new Element("metrics");
        Element supportXML = new Element("support");
        Element confidenceXML = new Element("confidence");
        Element convictionXML = new Element("conviction");
        Element gainXML = new Element("gain");
        Element liftXML = new Element("lift");
        Element psXML = new Element("ps");
        Element laplaceXML = new Element("laplace");
        
        // XML creation information initializing - gets date and hour of the creation for the XML Rules file

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfdate = new SimpleDateFormat("dd/MM/YYYY");        
        String dataactual = sdfdate.format(Calendar.getInstance().getTime()).toString();
        String horaactual = sdf.format(Calendar.getInstance().getTime()).toString();
        creationinfo.setAttribute("date", dataactual);
        creationinfo.setAttribute("time", horaactual);
        rules.addContent(creationinfo);
        
        // End of XML initializing
        
        dbRules = new Database();
        rulesHM = dbRules.databaseGetRules(con, this.tableNames[1]);
        dbRules.databaseDisconnect(con);

        if (!rulesHM.isEmpty()) {
            for (int i = 0, j = rulesHM.size(); i <= j - 1; i++) {
                System.out.println("(" + (i + 1) + ")" + " Start: Premise <" + rulesHM.get(i + 1).get(0) + "> Conclusion <" + rulesHM.get(i + 1).get(1) + ">");
                
                rule.setAttribute("id", Integer.toString(i+1));
                
                if (i == 4) {
                    System.out.println("-------------------------------------- Rule (" + (i + 1) + ") ---------------------------------------------");
                }
// ------------------------------------- Premise words finding

                premise = rulesHM.get(i + 1).get(0);
                premiseWordString = "<p id=\"premise_" + (i + 1) + "\">" + premise.toString();
                
                concept.setAttribute("value", "premise");
                keyword.setText(premise.toString());
                concept.addContent(keyword);
                
                if (!allConceptsRelatedHM.containsKey(premise.toString())) {
                    ngramsWordCount = cp.processWord(premise.toString(), "_").size();
                    System.out.println("(" + (i + 1) + ")" + "Number of words of Ngram" + ngramsWordCount);
                    ngramsList = cp.getNgramList(premise.toString(), oi);

                    // --------------------

                    if (ngramsList.isEmpty()) {  // If the premise word did not found any exact match within the ontology, tries others
                        premiseWordString += " (exact match not found) Candidates:</p><select name=\"premise" + (i + 1) + "\" id=\"premise" + (i + 1) + "\">";
                        
                        switch (ngramsWordCount) {
                            case 1: {
                                ngramsListHM = cp.getOnegramCandidatesListV3(premise.toString(), oi);

                                if (!ngramsListHM.isEmpty()) {
                                        if (!ngramsListHM.get(premise.toString()).isEmpty()) {
                                            hmConcepts = cp.getConceptsRelated(ngramsListHM.get(premise.toString()), oi);
                                            for (int n = 0; n < hmConcepts.size(); n++) {
                                                if (!hmConcepts.isEmpty()) {
                                                    premiseWordList = hmConcepts.get(ngramsListHM.get(premise.toString()).get(n));
                                                    System.out.println("(" + (i + 1) + ")" + "Showing " + ngramsListHM.get(premise.toString()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(premise.toString()).get(n)) + ") premiseWordList - " + premiseWordList);
                                                    for (int m2 = 0; m2 < premiseWordList.size(); m2++) {
                                                        if (!premiseWordString.contains(premiseWordList.get(m2))) {
                                                            distancePercentage = cp.getCosineSimilarity(cp.processWord(premise.toString(), "_"), cp.processWord(ngramsListHM.get(premise.toString()).get(n), " ")) * 100;
                                                            premiseWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + premiseWordList.get(m2) + "\">" + premiseWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                            allConceptsRelatedAL.add(premiseWordList.get(m2));
                                                            allConceptsRelatedSimilarityHM.put(premiseWordList.get(m2), distancePercentage);
                                                            System.out.println("(" + (i + 1) + ") Common words Cosine similarity:" + "<" + premise.toString() + "><" + cp.processWord(ngramsListHM.get(premise.toString()).get(m2), " ").toString() + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                            option.setAttribute("exactmatch", "NO");
                                                            optionName.setText(premiseWordList.get(m2).toString());
                                                            optionDistance.setText(df.format(distancePercentage));
                                                            option.addContent(optionName);
                                                            option.addContent(optionDistance);
                                                            options.addContent(option);
                                                            option = new Element("option");
                                                            optionName = new Element("name");
                                                            optionDistance = new Element("distance");
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            System.out.println("(" + (i + 1) + ")" + "No candidates found for ->" + ngramsListHM.get(premise.toString()));
                                        }
                                    premiseWordString += "</select>";
                                } else {
                                    System.out.println("(" + (i + 1) + ")" + "No Candidates Found for - " + premise.toString());
                                    premiseWordString += "<option>(Empty)</option></select>";
                                    premiseWordString += "<a href=# onclick=\"javascript:conceptsTree('premise_" + (i + 1) + "');\">New...</a>";
                                }
                                break;
                            }
                            case 2: {
                                ngramsListHM = new HashMap<String, ArrayList<String>>();
                                ArrayList<String> keywordList = cp.processWord(premise.toString(), "_");
                                for (int wordI = 0; wordI < ngramsWordCount; wordI++) {
                                    ngramsListHM.putAll(cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi));
                                }
                                System.out.println("(" + (i + 1) + ")" + "Bigram (" + premise.toString() + ") ngramslist" + ngramsListHM);

                                if (!ngramsListHM.isEmpty()) {
                                    for (int m = 1; m <= ngramsListHM.size(); m++) {
                                        if (!ngramsListHM.get(premise.toString()).isEmpty()) {
                                            hmConcepts = cp.getConceptsRelated(ngramsListHM.get(premise.toString()), oi);

                                            for (int n = 0; n < hmConcepts.size(); n++) {
                                                if (!hmConcepts.isEmpty()) {
                                                    premiseWordList = hmConcepts.get(ngramsListHM.get(premise.toString()).get(n));
                                                    System.out.println("(" + (i + 1) + ")" + "Showing " + ngramsListHM.get(premise.toString()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(premise.toString()).get(n)) + ") premiseWordList - " + premiseWordList);
                                                    for (int m2 = 0; m2 < premiseWordList.size(); m2++) {
                                                        if (!premiseWordString.contains(premiseWordList.get(m2))) {
                                                            distancePercentage = cp.getCosineSimilarity(cp.processWord(premise.toString(), "_"), cp.processWord(ngramsListHM.get(premise.toString()).get(n), " ")) * 100;
                                                            premiseWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + premiseWordList.get(m2) + "\">" + premiseWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                            allConceptsRelatedAL.add(premiseWordList.get(m2));
                                                            allConceptsRelatedSimilarityHM.put(premiseWordList.get(m2), distancePercentage);
                                                            System.out.println("(" + (i + 1) + ") Common words Cosine similarity:" + "<" + premise.toString() + "><" + cp.processWord(ngramsListHM.get(premise.toString()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                            option.setAttribute("exactmatch", "NO");
                                                            optionName.setText(premiseWordList.get(m2).toString());
                                                            optionDistance.setText(df.format(distancePercentage));
                                                            option.addContent(optionName);
                                                            option.addContent(optionDistance);
                                                            options.addContent(option);
                                                            option = new Element("option");
                                                            optionName = new Element("name");
                                                            optionDistance = new Element("distance");
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            System.out.println("(" + (i + 1) + ")" + "No candidates found for ->" + ngramsListHM.get(m));
                                        }
                                    }
                                    premiseWordString += "</select>";
                                } else {
                                    System.out.println("(" + (i + 1) + ")" + "No Candidates Found for - " + premise.toString());
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
                        premiseWordString += "</p><select name=\"premise" + (i + 1) + "\">";
                        hmConcepts = cp.getConceptsRelated(ngramsList, oi);
                        distancePercentage = 100.00;
                        for (int i2 = 0, i3 = hmConcepts.size(); i2 < i3; i2++) {
                            premiseWordList = hmConcepts.get(ngramsList.get(i2));
                            for (int j2 = 0, j3 = premiseWordList.size(); j2 < j3; j2++) {
                                if (!premiseWordString.contains(premiseWordList.get(j2))) {
                                    premiseWordString += "<option class=\"level0\" value=\"" + premiseWordList.get(j2) + "\">" + premiseWordList.get(j2).replace("_Individual", "").replace("_", " ") + " (100"/*+df.format(distancePercentage)*/ + "%)</option>";
                                    allConceptsRelatedAL.add(premiseWordList.get(j2));
                                    allConceptsRelatedSimilarityHM.put(premiseWordList.get(j2), distancePercentage);
                                    option.setAttribute("exactmatch", "YES");
                                    optionName.setText(premiseWordList.get(j2).toString());
                                    optionDistance.setText(df.format(distancePercentage));
                                    option.addContent(optionName);
                                    option.addContent(optionDistance);
                                    options.addContent(option);
                                    option = new Element("option");
                                    optionName = new Element("name");
                                    optionDistance = new Element("distance");
                                }
                            }
                        }
                        // Will discover next the not 100% exact matches 
                        if (ngramsWordCount == 1) {
                            ngramsListHM = cp.getOnegramCandidatesListV3(premise.toString(), oi);
                            if (!ngramsListHM.isEmpty()) {
                                //for (int m=1; m<=ngramsListHM.size(); m++) {
                                if (!ngramsListHM.get(premise.toString()).isEmpty()) {
                                    hmConcepts = cp.getConceptsRelated(ngramsListHM.get(premise.toString()), oi);
                                    if (!hmConcepts.isEmpty()) {
                                        for (int n = 0; n < hmConcepts.size(); n++) {
                                            premiseWordList = hmConcepts.get(ngramsListHM.get(premise.toString()).get(n));
                                            System.out.println("(" + (i + 1) + ")" + "Showing " + ngramsListHM.get(premise.toString()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(premise.toString()).get(n)) + ") premiseWordList - " + premiseWordList);
                                            for (int m2 = 0; m2 < premiseWordList.size(); m2++) {
                                                if (!premiseWordString.contains(premiseWordList.get(m2))) {
                                                    distancePercentage = cp.getCosineSimilarity(cp.processWord(premise.toString(), "_"), cp.processWord(ngramsListHM.get(premise.toString()).get(n), " ")) * 100;
                                                    premiseWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + premiseWordList.get(m2) + "\">" + premiseWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                    allConceptsRelatedAL.add(premiseWordList.get(m2));
                                                    allConceptsRelatedSimilarityHM.put(premiseWordList.get(m2), distancePercentage);
                                                    System.out.println("(" + (i + 1) + ") Common words Cosine similarity:" + "<" + premise.toString() + "><" + cp.processWord(ngramsListHM.get(premise.toString()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                    option.setAttribute("exactmatch", "NO");
                                                    optionName.setText(premiseWordList.get(m2).toString());
                                                    optionDistance.setText(df.format(distancePercentage));
                                                    option.addContent(optionName);
                                                    option.addContent(optionDistance);
                                                    options.addContent(option);
                                                    option = new Element("option");
                                                    optionName = new Element("name");
                                                    optionDistance = new Element("distance");
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    System.out.println("(" + (i + 1) + ")" + "No candidates found for ->" + ngramsListHM.get(premise.toString()));
                                }
                                //}
                                // premiseWordString += "</select>";
                            } else {
                                System.out.println("(" + (i + 1) + ")" + "Only found 100% match for - " + premise.toString());
                                premiseWordString += "<option>(Empty)</option></select>";
                            }
                        } else if (ngramsWordCount == 2) {
                            ngramsListHM = new HashMap<String, ArrayList<String>>();
                            ArrayList<String> keywordList = cp.processWord(premise.toString(), "_");
                            ArrayList<String> tmpList = new ArrayList<String>();
                            for (int wordI = 0; wordI < ngramsWordCount; wordI++) {
                                switch (wordI) {
                                    case 0: {
                                        ngramsListHM.put(premise.toString(), cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi).get(keywordList.get(wordI)));
                                        break;
                                    }
                                    case 1: {
                                        if (!ngramsListHM.isEmpty()) {
                                            tmpList = cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi).get(keywordList.get(wordI));
                                            for (int iterator = 0, ngramsListHMSize = ngramsListHM.get(premise.toString()).size(); iterator < ngramsListHMSize ; iterator++ )
                                                if (!tmpList.contains(ngramsListHM.get(premise.toString()).get(iterator)))
                                                    tmpList.add(ngramsListHM.get(premise.toString()).get(iterator));
                                            ngramsListHM.put(premise.toString(), tmpList);
                                        } else {
                                            ngramsListHM.put(premise.toString(), cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi).get(keywordList.get(wordI)));
                                        }
                                        break;
                                    }
                                    default: {
                                        for (int m = 1, m2 = ngramsListHM.size(); m <= m2; m++) {
                                            if (!ngramsListHM.get(premise.toString()).isEmpty()) {
                                                tmpList = ngramsListHM.get(premise.toString());
                                                tmpList.addAll(cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi).get(premise.toString()));
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            System.out.println("(" + (i + 1) + ")" + "Bigram (" + premise.toString() + ") ngramslist" + ngramsListHM);
                            if (!ngramsListHM.isEmpty()) {
                                //for (int m=1, n2=ngramsListHM.size(); m<=n2; m++) {
                                System.out.println("(" + (i + 1) + ")" + "Gets arrayLists for keyword=" + premise.toString() + "/" + ngramsListHM.size());
                                if (!ngramsListHM.get(premise.toString()).isEmpty()) {
                                    // System.out.println("("+(i+1)+")"+ "Gets level1 and level2 arrayLists. Passagem: m="+m+"/"+ngramsListHM.size());
                                    hmConcepts = cp.getConceptsRelated(ngramsListHM.get(premise.toString()), oi);

                                    if (!hmConcepts.isEmpty()) {
                                        for (int n = 0, n1 = hmConcepts.size(); n < n1; n++) {
                                            premiseWordList = hmConcepts.get(ngramsListHM.get(premise.toString()).get(n));
                                            System.out.println("(" + (i + 1) + ")" + "Showing " + ngramsListHM.get(premise.toString()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(premise.toString()).get(n)) + ") premiseWordList - " + premiseWordList);
                                            for (int m2 = 0, m3 = premiseWordList.size(); m2 < m3; m2++) {
                                                if (!premiseWordString.contains(premiseWordList.get(m2))) {
                                                    distancePercentage = cp.getCosineSimilarity(cp.processWord(premise.toString(), "_"), cp.processWord(ngramsListHM.get(premise.toString()).get(n), " ")) * 100;
                                                    allConceptsRelatedAL.add(premiseWordList.get(m2));
                                                    allConceptsRelatedSimilarityHM.put(premiseWordList.get(m2), distancePercentage);
                                                    premiseWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + premiseWordList.get(m2) + "\">" + premiseWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                    System.out.println("(" + (i + 1) + ") Common words Cosine similarity:" + "<" + premise.toString() + "><" + cp.processWord(ngramsListHM.get(premise.toString()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                    option.setAttribute("exactmatch", "NO");
                                                    optionName.setText(premiseWordList.get(m2).toString());
                                                    optionDistance.setText(df.format(distancePercentage));
                                                    option.addContent(optionName);
                                                    option.addContent(optionDistance);
                                                    options.addContent(option);
                                                    option = new Element("option");
                                                    optionName = new Element("name");
                                                    optionDistance = new Element("distance");
                                                }
                                            }

                                        }
                                    }
                                } else {
                                    System.out.println("(" + (i + 1) + ")" + "No candidates found for ->" + ngramsListHM.get(premise.toString()));
                                }
                                //}
                                //premiseWordString += "</select>";
                            } else {
                                System.out.println("(" + (i + 1) + ")" + "No Candidates Found for - " + premise.toString());
                                premiseWordString += "<option>(Empty)</option>";
                            }
                        }
                        premiseWordString += "</select>";
                    }
                    System.out.println("(" + (i + 1) + ")premiseWordString: " + premiseWordString);
                    if ((!allConceptsRelatedAL.isEmpty()) && (!allConceptsRelatedSimilarityHM.isEmpty()) ){
                        allConceptsRelatedHM.put(premise.toString(), allConceptsRelatedAL);
                        allConceptsRelatedSimilarityKeywordHM.put(premise.toString(), allConceptsRelatedSimilarityHM);
                    }
                } else {
                    allConceptsRelatedAL = allConceptsRelatedHM.get(premise.toString());
                    allConceptsRelatedSimilarityHM = allConceptsRelatedSimilarityKeywordHM.get(premise.toString());
                    if (!allConceptsRelatedSimilarityHM.containsValue(100.0) ){
                        premiseWordString += " (exact match not found) Candidates:";
                        option.setAttribute("exactmatch", "NO");
                    }
                    else {
                        option.setAttribute("exactmatch", "YES");
                    }
                    premiseWordString += "</p><select name=\"premise" + (i + 1) + "\">";
                    for (int i3 = 0, k3 = allConceptsRelatedAL.size(); i3 < k3; i3++) {
                        Double similarity = allConceptsRelatedSimilarityHM.get(allConceptsRelatedAL.get(i3));
                        String similarityString = "100";

                        if (similarity != 100) {
                            similarityString = df.format(similarity);
                        } 

                        
                        optionName.setText(allConceptsRelatedAL.get(i3).toString());
                        optionDistance.setText(df.format(similarity));
                        option.addContent(optionName);
                        option.addContent(optionDistance);
                        options.addContent(option);
                        option = new Element("option");
                        optionName = new Element("name");
                        optionDistance = new Element("distance");

                        premiseWordString += "<option class=\"" + cp.getSimilarityClass(similarity) + "\"value=\"" + allConceptsRelatedAL.get(i3) + "\">" + allConceptsRelatedAL.get(i3).replace("_Individual", "").replace("_", " ") + " (" + similarityString + "%)</option>";
                        System.out.println("(" + (i + 1) + ") Keyword already processed:" + premise.toString() + "Inserting concept related - " + allConceptsRelatedAL.get(i3));
                    }
                    premiseWordString += "</select>";
                }
                options.setAttribute("options_count", Integer.toString(allConceptsRelatedAL.size()));
                concept.addContent(options);
                
                rule.addContent(concept);
                
                concept = new Element("concept");
                options = new Element("options");
                keyword = new Element("keyword");
                option = new Element("option");
                optionName = new Element("name");
                optionDistance = new Element("distance");

                allConceptsRelatedAL = new ArrayList<String>();
                allConceptsRelatedSimilarityHM = new HashMap<String, Double>();

// -------------------------------------  End of Premise words finding        


// -------------------------------------  Conclusion words finding        
                if (i == 4) {
                    System.out.println("-------------------------------------- I'm in rule (" + (i + 1) + ") ---------------------------------------------");
                }

                conclusion = rulesHM.get(i + 1).get(1);
                conclusionWordString = "<p id=\"conclusion_" + (i + 1) + "\">" + conclusion.toString();
                
                concept.setAttribute("value", "conclusion");

                keyword.setText(conclusion.toString());
                concept.addContent(keyword);
                
                
                if (!allConceptsRelatedHM.containsKey(conclusion.toString())) {
                    ngramsWordCount = cp.processWord(conclusion.toString(), "_").size();
                    ngramsList = cp.getNgramList(conclusion.toString(), oi);

                    if (ngramsList.isEmpty()) {  // If the conclusion word did not found any exact match within the ontology, tries others
                        conclusionWordString += " (exact match not found) Candidates:</p><select name=\"conclusion" + (i + 1) + "\" id=\"conclusion" + (i + 1) + "\">";
                        switch (ngramsWordCount) {
                            case 1: {
                                ngramsListHM = cp.getOnegramCandidatesListV3(conclusion.toString(), oi);
                                if (!ngramsListHM.isEmpty()) {
                                        if (!ngramsListHM.get(conclusion.toString()).isEmpty()) {
                                            hmConcepts = cp.getConceptsRelated(ngramsListHM.get(conclusion.toString()), oi);

                                            for (int n = 0, n2 = hmConcepts.size(); n < n2; n++) {
                                                if (!hmConcepts.isEmpty()) {
                                                    conclusionWordList = hmConcepts.get(ngramsListHM.get(conclusion.toString()).get(n));
                                                    System.out.println("(" + (i + 1) + ")" + "Showing " + ngramsListHM.get(conclusion.toString()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(conclusion.toString()).get(n)) + ") conclusionWordList - " + conclusionWordList);
                                                    for (int m2 = 0, k3 = conclusionWordList.size(); m2 < k3; m2++) {
                                                        if (!conclusionWordString.contains(conclusionWordList.get(m2))) {
                                                            distancePercentage = cp.getCosineSimilarity(cp.processWord(conclusion.toString(), "_"), cp.processWord(ngramsListHM.get(conclusion.toString()).get(n), " ")) * 100;
                                                            allConceptsRelatedAL.add(conclusionWordList.get(m2));
                                                            allConceptsRelatedSimilarityHM.put(conclusionWordList.get(m2), distancePercentage);
                                                            conclusionWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + conclusionWordList.get(m2) + "\">" + conclusionWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                            System.out.println("(" + (i + 1) + ") Common words Cosine similarity:" + "<" + conclusion.toString() + "><" + cp.processWord(ngramsListHM.get(conclusion.toString()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                            option.setAttribute("exactmatch", "NO");
                                                            optionName.setText(conclusionWordList.get(m2).toString());
                                                            optionDistance.setText(df.format(distancePercentage));
                                                            option.addContent(optionName);
                                                            option.addContent(optionDistance);
                                                            options.addContent(option);
                                                            option = new Element("option");
                                                            optionName = new Element("name");
                                                            optionDistance = new Element("distance");
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            System.out.println("(" + (i + 1) + ")" + "No candidates found for ->" + ngramsListHM.get(conclusion.toString()));
                                        }
                                    conclusionWordString += "</select>";
                                } else {
                                    System.out.println("(" + (i + 1) + ")" + "No Candidates Found for - " + conclusion.toString());
                                    conclusionWordString += "<option>(Empty)</option></select>";
                                    conclusionWordString += "<a href=# onclick=\"javascript:conceptsTree('conclusion_" + (i + 1) + "');\">New...</a>";
                                }
                                break;
                            }
                            case 2: {
                                ngramsListHM = new HashMap<String, ArrayList<String>>();
                                ArrayList<String> keywordList = cp.processWord(conclusion.toString(), "_");
                                for (int wordI = 0; wordI < ngramsWordCount; wordI++) {
                                    ngramsListHM.putAll(cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi));
                                }
                                System.out.println("(" + (i + 1) + ")" + "Bigram (" + conclusion.toString() + ") ngramslist" + ngramsListHM);
                                if (!ngramsListHM.isEmpty()) {
                                    if (!ngramsListHM.get(conclusion.toString()).isEmpty()) {
                                        hmConcepts = cp.getConceptsRelated(ngramsListHM.get(conclusion.toString()), oi);
                                        for (int n = 0, nn1 = hmConcepts.size(); n < nn1; n++) {
                                            if (!hmConcepts.isEmpty()) {
                                                conclusionWordList = hmConcepts.get(ngramsListHM.get(conclusion.toString()).get(n));
                                                System.out.println("(" + (i + 1) + ")" + "Showing " + ngramsListHM.get(conclusion.toString()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(conclusion.toString()).get(n)) + ") conclusionWordList - " + conclusionWordList);
                                                for (int m2 = 0, mm2 = conclusionWordList.size(); m2 < mm2; m2++) {
                                                    if (!conclusionWordString.contains(conclusionWordList.get(m2))) {
                                                        distancePercentage = cp.getCosineSimilarity(cp.processWord(conclusion.toString(), "_"), cp.processWord(ngramsListHM.get(conclusion.toString()).get(n), " ")) * 100;
                                                        allConceptsRelatedAL.add(conclusionWordList.get(m2));
                                                        allConceptsRelatedSimilarityHM.put(conclusionWordList.get(m2), distancePercentage);
                                                        conclusionWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + conclusionWordList.get(m2) + "\">" + conclusionWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                        System.out.println("(" + (i + 1) + ") Common words Cosine similarity:" + "<" + conclusion.toString() + "><" + cp.processWord(ngramsListHM.get(conclusion.toString()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                        option.setAttribute("exactmatch", "NO");
                                                        optionName.setText(conclusionWordList.get(m2).toString());
                                                        optionDistance.setText(df.format(distancePercentage));
                                                        option.addContent(optionName);
                                                        option.addContent(optionDistance);
                                                        options.addContent(option);
                                                        option = new Element("option");
                                                        optionName = new Element("name");
                                                        optionDistance = new Element("distance");
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        System.out.println("(" + (i + 1) + ")" + "No candidates found for ->" + ngramsListHM.get(conclusion.toString()));
                                    }
                                    conclusionWordString += "</select>";
                                } else {
                                    System.out.println("(" + (i + 1) + ")" + "No Candidates Found for - " + conclusion.toString());
                                    conclusionWordString += "<option>(Empty)</option></select>";
                                }
                                break;
                            }
                            case 3: {
                                // ngramsList = cp.getTrigramCandidatesList (conclusion.toString());
                                break;
                            }
                        }
                    } else {
                        // 100% - Found exact matches - search for corresponding concepts
                        conclusionWordString += "</p><select name=\"conclusion" + (i + 1) + "\">";
                        hmConcepts = cp.getConceptsRelated(ngramsList, oi);
                        distancePercentage = 100.00;
                        for (int i2 = 0, ii2 = hmConcepts.size(); i2 < ii2; i2++) {
                            conclusionWordList = hmConcepts.get(ngramsList.get(i2));
                            for (int j2 = 0, mm = conclusionWordList.size(); j2 < mm; j2++) {
                                if (!conclusionWordString.contains(conclusionWordList.get(j2))) {
                                    conclusionWordString += "<option class=\"level0\" value=\"" + conclusionWordList.get(j2) + "\">" + conclusionWordList.get(j2).replace("_Individual", "").replace("_", " ") + " (100%)</option>";
                                    allConceptsRelatedAL.add(conclusionWordList.get(j2));
                                    allConceptsRelatedSimilarityHM.put(conclusionWordList.get(j2), distancePercentage);
                                    option.setAttribute("exactmatch", "YES");
                                    optionName.setText(conclusionWordList.get(j2).toString());
                                    optionDistance.setText(df.format(distancePercentage));
                                    option.addContent(optionName);
                                    option.addContent(optionDistance);
                                    options.addContent(option);
                                    option = new Element("option");
                                    optionName = new Element("name");
                                    optionDistance = new Element("distance");
                                }
                            }
                            // allConceptsRelatedHM.put(conclusion.toString(), allConceptsRelatedAL);
                            // allConceptsRelatedSimilarityKeywordHM.put(conclusion.toString(), allConceptsRelatedSimilarityHM);
                        }

                        // Gets similar matches (not 100%) and search corresponding 
                        if (ngramsWordCount == 1) {
                            ngramsListHM = cp.getOnegramCandidatesListV3(conclusion.toString(), oi);

                            if (!ngramsListHM.isEmpty()) {
                                    hmConcepts = cp.getConceptsRelated(ngramsListHM.get(conclusion.toString()), oi);
                                    if (!hmConcepts.isEmpty()) {
                                        for (int n = 0, nn3 = hmConcepts.size(); n < nn3; n++) {
                                            conclusionWordList = hmConcepts.get(ngramsListHM.get(conclusion.toString()).get(n));
                                            System.out.println("(" + (i + 1) + ")" + "Showing " + ngramsListHM.get(conclusion.toString()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(conclusion.toString()).get(n)) + ") conclusionWordList - " + conclusionWordList);
                                            for (int m2 = 0, mm4 = conclusionWordList.size(); m2 < mm4; m2++) {
                                                if (!conclusionWordString.contains(conclusionWordList.get(m2))) {
                                                    distancePercentage = cp.getCosineSimilarity(cp.processWord(conclusion.toString(), "_"), cp.processWord(ngramsListHM.get(conclusion.toString()).get(n), " ")) * 100;
                                                    allConceptsRelatedAL.add(conclusionWordList.get(m2));
                                                    allConceptsRelatedSimilarityHM.put(conclusionWordList.get(m2), distancePercentage);
                                                    conclusionWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + conclusionWordList.get(m2) + "\">" + conclusionWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                    System.out.println("(" + (i + 1) + ") Common words Cosine similarity:" + "<" + conclusion.toString() + "><" + cp.processWord(ngramsListHM.get(conclusion.toString()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                    option.setAttribute("exactmatch", "NO");
                                                    optionName.setText(conclusionWordList.get(m2).toString());
                                                    optionDistance.setText(df.format(distancePercentage));
                                                    option.addContent(optionName);
                                                    option.addContent(optionDistance);
                                                    options.addContent(option);
                                                    option = new Element("option");
                                                    optionName = new Element("name");
                                                    optionDistance = new Element("distance");
                                                }
                                            }
                                        }
                                    }
                                    //else {
                                    //System.out.println("("+(i+1)+")"+"No candidates found for ->"+ngramsListHM.get(m));
                                    //}
                                // conclusionWordString += "</select>";
                            } else {
                                System.out.println("(" + (i + 1) + ")" + "No Candidates Found for - " + conclusion.toString());
                                conclusionWordString += "<option>(Empty)</option></select>";
                            }
                        } else if (ngramsWordCount == 2) {
                            ngramsListHM = new HashMap<String, ArrayList<String>>();
                            ArrayList<String> keywordList = cp.processWord(conclusion.toString(), "_");
                            ArrayList<String> tmpList = new ArrayList<String>();
                            for (int wordI = 0; wordI < ngramsWordCount; wordI++) {
                                switch (wordI) {
                                    case 0: {
                                        ngramsListHM.put(conclusion.toString(), cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi).get(keywordList.get(wordI)));
                                        break;
                                    }
                                    case 1: {
                                        if (!ngramsListHM.isEmpty()) {
                                            tmpList = cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi).get(keywordList.get(wordI));
                                            for (int iterator = 0, ngramsListHMSize = ngramsListHM.get(conclusion.toString()).size(); iterator < ngramsListHMSize ; iterator++ )
                                                if (!tmpList.contains(ngramsListHM.get(conclusion.toString()).get(iterator)))
                                                    tmpList.add(ngramsListHM.get(conclusion.toString()).get(iterator));
                                            ngramsListHM.put(conclusion.toString(), tmpList);
                                        } else {
                                            ngramsListHM.put(conclusion.toString(), cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi).get(keywordList.get(wordI)));
                                        }
                                        break;
                                    }
                                    default: {
                                        for (int m = 1, m2 = ngramsListHM.size(); m <= m2; m++) {
                                            if (!ngramsListHM.get(conclusion.toString()).isEmpty()) {
                                                tmpList = ngramsListHM.get(conclusion.toString());
                                                tmpList.addAll(cp.getOnegramCandidatesListV3(keywordList.get(wordI), oi).get(conclusion.toString()));
                                            }
                                        }
                                        break;
                                    }
                                }

                            }
                            System.out.println("(" + (i + 1) + ")" + "Bigram (" + conclusion.toString() + ") ngramslist" + ngramsListHM);
                            if (!ngramsListHM.isEmpty()) {
                                //for (int m=1, n2=ngramsListHM.size(); m<=n2; m++) {
                                System.out.println("(" + (i + 1) + ")" + "Gets arrayLists for keyword=" + conclusion.toString() + "/" + ngramsListHM.size());

                                if (!ngramsListHM.get(conclusion.toString()).isEmpty()) {
                                    // System.out.println("("+(i+1)+")"+ "Gets level1 and level2 arrayLists. Passagem: m="+m+"/"+ngramsListHM.size());
                                    hmConcepts = cp.getConceptsRelated(ngramsListHM.get(conclusion.toString()), oi);
                                        // TODO: Verify sizes of hmConcepts vs ngramsLinsHM.get(conclusion.toString())
                                    if (!hmConcepts.isEmpty()) {
                                        for (int n = 0, n1 = hmConcepts.size(); n < n1; n++) {
                                            conclusionWordList = hmConcepts.get(ngramsListHM.get(conclusion.toString()).get(n));
                                            System.out.println("(" + (i + 1) + ")" + "Showing " + ngramsListHM.get(conclusion.toString()).size() + " candidates (" + hmConcepts.get(ngramsListHM.get(conclusion.toString()).get(n)) + ") conclusionWordList - " + conclusionWordList);
                                            for (int m2 = 0, m3 = conclusionWordList.size(); m2 < m3; m2++) {
                                                if (!conclusionWordString.contains(conclusionWordList.get(m2))) {
                                                    distancePercentage = cp.getCosineSimilarity(cp.processWord(conclusion.toString(), "_"), cp.processWord(ngramsListHM.get(conclusion.toString()).get(n), " ")) * 100;
                                                    allConceptsRelatedAL.add(conclusionWordList.get(m2));
                                                    allConceptsRelatedSimilarityHM.put(conclusionWordList.get(m2), distancePercentage);
                                                    conclusionWordString += "<option class=\"" + cp.getSimilarityClass(distancePercentage) + "\" value=\"" + conclusionWordList.get(m2) + "\">" + conclusionWordList.get(m2).replace("_Individual", "").replace("_", " ") + " (" + df.format(distancePercentage) + "%)</option>";
                                                    System.out.println("(" + (i + 1) + ") Common words Cosine similarity:" + "<" + conclusion.toString() + "><" + cp.processWord(ngramsListHM.get(conclusion.toString()).get(m2), " ") + "> = " + "«" + df.format(distancePercentage) + "%»");
                                                    option.setAttribute("exactmatch", "NO");
                                                    optionName.setText(conclusionWordList.get(m2).toString());
                                                    optionDistance.setText(df.format(distancePercentage));
                                                    option.addContent(optionName);
                                                    option.addContent(optionDistance);
                                                    options.addContent(option);
                                                    option = new Element("option");
                                                    optionName = new Element("name");
                                                    optionDistance = new Element("distance");
                                                }
                                            }

                                        }
                                    }
                                } else {
                                    System.out.println("(" + (i + 1) + ")" + "No candidates found for ->" + ngramsListHM.get(conclusion.toString()));
                                }
                                //}
                                //conclusionWordString += "</select>";
                            } else {
                                System.out.println("(" + (i + 1) + ")" + "No Candidates Found for - " + conclusion.toString());
                                conclusionWordString += "<option>(Empty)</option>";
                            }
                        }
                        conclusionWordString += "</select>";
                    }
                    System.out.println("(" + (i + 1) + ")conclusionWordString: " + conclusionWordString);

                    if  ( (!allConceptsRelatedAL.isEmpty()) && (!allConceptsRelatedSimilarityHM.isEmpty()) ){
                        allConceptsRelatedHM.put(conclusion.toString(), allConceptsRelatedAL);
                        allConceptsRelatedSimilarityKeywordHM.put(conclusion.toString(), allConceptsRelatedSimilarityHM);
                    }
                } else {
                    
                    allConceptsRelatedAL = allConceptsRelatedHM.get(conclusion.toString());
                    allConceptsRelatedSimilarityHM = allConceptsRelatedSimilarityKeywordHM.get(conclusion.toString());
                    if (!allConceptsRelatedSimilarityHM.containsValue(100.0) )
                        conclusionWordString += " (exact match not found) Candidates:";
                    conclusionWordString += "</p><select name=\"conclusion" + (i + 1) + "\">";
                    
                    for (int i3 = 0, i4 = allConceptsRelatedSimilarityHM.size(); i3 < i4; i3++) {
                        Double similarity = allConceptsRelatedSimilarityHM.get(allConceptsRelatedAL.get(i3));
                        String similarityString = "100";

                        if (similarity != 100) {
                            similarityString = df.format(similarity);
                            option.setAttribute("exactmatch", "NO");
                        }
                        else {
                            option.setAttribute("exactmatch", "YES");
                        }
                            

                        conclusionWordString += "<option class=\"" + cp.getSimilarityClass(similarity) + "\"value=\"" + allConceptsRelatedAL.get(i3) + "\">" + allConceptsRelatedAL.get(i3).replace("_Individual", "").replace("_", " ") + " (" + similarityString + "%)</option>";
                        System.out.println("(" + (i + 1) + ") Keyword already processed:" + conclusion.toString() + "Inserting concept related - " + allConceptsRelatedAL.get(i3));
                        
                        optionName.setText(allConceptsRelatedAL.get(i3).toString());
                        optionDistance.setText(df.format(similarity));
                        option.addContent(optionName);
                        option.addContent(optionDistance);
                        options.addContent(option);
                        option = new Element("option");
                        optionName = new Element("name");
                        optionDistance = new Element("distance");
                    }
                    conclusionWordString += "</select>";
                }
                
                options.setAttribute("options_count", Integer.toString(allConceptsRelatedAL.size()));
                concept.addContent(options);
                rule.addContent(concept);
                
                allConceptsRelatedAL = new ArrayList<String>();
                allConceptsRelatedSimilarityHM = new HashMap<String, Double>();
            
// -------------------------------------  End of Conclusion words finding        


                conviction = rulesHM.get(i + 1).get(2);
                gain = rulesHM.get(i + 1).get(3);
                lift = rulesHM.get(i + 1).get(4);
                laplace = rulesHM.get(i + 1).get(5);
                ps = rulesHM.get(i + 1).get(6);
                totalSupport = rulesHM.get(i + 1).get(7);
                confidence = rulesHM.get(i + 1).get(8);

                convictionXML.addContent(conviction.toString());
                metrics.addContent(convictionXML);

                supportXML.addContent(totalSupport.toString());
                metrics.addContent(supportXML);

                confidenceXML.addContent(confidence.toString());
                metrics.addContent(confidenceXML);

                gainXML.addContent(gain.toString());
                metrics.addContent(gainXML);

                liftXML.addContent(lift.toString());
                metrics.addContent(liftXML);

                psXML.addContent(ps.toString());
                metrics.addContent(psXML);

                laplaceXML.addContent(laplace.toString());
                metrics.addContent(laplaceXML);

                rule.addContent(metrics);
                rules.setAttribute("rule_count", Integer.toString(i+1));
                rules.addContent(rule);

                // HTML Vector construction 
                System.out.println("(" + (i + 1) + ")" + "conviction:" + conviction + "-gain:" + gain + "-lift:" + lift + "-laplace:" + laplace + "-ps:" + ps + "-total Support:" + totalSupport + "-confidence:" + confidence);

                resultsConcepts += "<div class=\"rulebox\" id=\"rulebox" + (i + 1) + "\"><form name=\"saveonerule\" action=\"rulessaved.jsp\" method=\"post\" target=\"_blank\">"
                        + "<p class=\"num_rules\">Rule #" + (i + 1) + "</p><ul>"
                        + "<li title=\"Premise\"><div class=\"premisetitle\">Premise</div><div class=\"premisevalue\">" + premiseWordString + "</div></li>"
                        + "<li title=\"Conclusion\"><div class=\"conclusiontitle\">Conclusion</div><div class=\"conclusionvalue\">" + conclusionWordString + "</div></li></ul>"
                        + "<ul><li title=\"Confidence\" class=\"metrics\"><div class=\"metrictitle\">Confidence</div><div class=\"metricvalue\" name=\"confidence\">" + confidence + "</div></li>"
                        + "<li title=\"Conviction\" class=\"metrics\"><div class=\"metrictitle\">Conviction</div><div class=\"metricvalue\" name=\"conviction\">";

                if (conviction.toString().contains("8888")) {
                    resultsConcepts += "Infinity";
                } else {
                    resultsConcepts += conviction;
                }

                resultsConcepts += "</div></li>"
                        + "<li title=\"Gain\" class=\"metrics\"><div class=\"metrictitle\">Gain</div><div class=\"metricvalue\">" + gain + "</div></li>"
                        + "<li title=\"Laplace\" class=\"metrics\"><div class=\"metrictitle\">Laplace</div><div class=\"metricvalue\">" + laplace + "</div></li>"
                        + "<li title=\"Lift\" class=\"metrics\"><div class=\"metrictitle\">Lift</div><div class=\"metricvalue\">" + lift + "</div></li>"
                        + "<li title=\"Ps\" class=\"metrics\"><div class=\"metrictitle\">PS</div><div class=\"metricvalue\">" + ps + "</div></li>"
                        + "<li title=\"Total Support\" class=\"metrics\"><div class=\"metrictitle\">Support</div><div class=\"metricvalue\">" + totalSupport + "</div></li>"
                        + "<li title=\"Insert rule\"><div class=\"metrictitle\"><input type=\"checkbox\" name=\"rule\" value=\"" + (i + 1) + "\"></div><div class=\"metricvalue\"><input type=\"submit\" value=\"Add rule to DB\" /></div></li>"
                        + "</ul></form></div>";

                System.out.println("(" + (i + 1) + ")" + " Finish: Premise <" + rulesHM.get(i + 1).get(0) + "> Conclusion <" + rulesHM.get(i + 1).get(1) + ">");

                // XML Cleaning xml elements for new rule
                
                rule = new Element("rule");

                concept = new Element("concept");
                options = new Element("options");
                keyword = new Element("keyword");
                options = new Element("options");
                option = new Element("option");
                optionName = new Element("name");
                optionDistance = new Element("distance");
                
                metrics = new Element("metrics");
                supportXML = new Element("support");
                confidenceXML = new Element("confidence");
                convictionXML = new Element("conviction");
                gainXML = new Element("gain");
                liftXML = new Element("lift");
                psXML = new Element("ps");
                laplaceXML = new Element("laplace");
                
            }
            System.out.println("----->>> all unique ConceptsTotal : " + allConceptsRelatedHM.size());
        } else {
            System.out.println("There are no rules in database.");
        }
        
        // Create XML document and format it in a more user-friendly readable way

        Document doc = new Document(rules);

        Format prettyFormat = Format.getPrettyFormat();
        prettyFormat.setExpandEmptyElements(false);
        XMLOutputter prettyXmlOut = new XMLOutputter(prettyFormat);

        ruleXmlFile.openFile(_corePath.RULE_XML_FILE);
        ruleXmlFile.printToFile(prettyXmlOut.outputString(doc));
        ruleXmlFile.closeFile();

        log.closeLogFile();
    }

    public void processGetRulesFromDb() {
        dbRules = new Database();
        Connection con = dbRules.databaseConnect(this._dbDetails.databaseURLName, this._dbDetails.databaseUserName, this._dbDetails.databasePasswordName, this._dbDetails.databaseDriverName);
        HashMap<Integer, ArrayList<String>> rulesHM = new HashMap<Integer, ArrayList<String>>();
        ArrayList<String> ruleValuesAL = new ArrayList<String>();
        ruleToSave = "<ul class=\"associationrule\"><li class=\"associationrule\">"
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
                ruleToSave += "<li class=\"associationrule\"><div class=\"associationrulenumber\">" + i + "</div>";
                if (!ruleValuesAL.isEmpty()) {
                    if (associationruleID == i) {
                        ruleToSave += "<div title=\"Rule Selected\" class=\"associationruleconcept\" style=\"color: red;\">" + ruleValuesAL.get(0) + "</div>"
                                + "<div title=\"Rule Selected\" class=\"associationruleconcept\" style=\"color: red;\">" + ruleValuesAL.get(1) + "</div>"
                                + "<div title=\"Rule Selected\" class=\"associationrulemetric\" style=\"color: red;\">" + ruleValuesAL.get(8) + "</div>"
                                + "<div title=\"Rule Selected\" class=\"associationrulemetric\" style=\"color: red;\">" + ruleValuesAL.get(2) + "</div>"
                                + "<div title=\"Rule Selected\" class=\"associationrulemetric\" style=\"color: red;\">" + ruleValuesAL.get(3) + "</div>"
                                + "<div title=\"Rule Selected\" class=\"associationrulemetric\" style=\"color: red;\">" + ruleValuesAL.get(4) + "</div>"
                                + "<div title=\"Rule Selected\" class=\"associationrulemetric\" style=\"color: red;\">" + ruleValuesAL.get(5) + "</div>"
                                + "<div title=\"Rule Selected\" class=\"associationrulemetric\" style=\"color: red;\">" + ruleValuesAL.get(6) + "</div>"
                                + "<div title=\"Rule Selected\" class=\"associationrulemetric\" style=\"color: red;\">" + ruleValuesAL.get(7) + "</div>";
                    } else {
                        ruleToSave += "<div class=\"associationruleconcept\">" + ruleValuesAL.get(0) + "</div>"
                                + "<div class=\"associationruleconcept\">" + ruleValuesAL.get(1) + "</div>"
                                + "<div class=\"associationrulemetric\">" + ruleValuesAL.get(8) + "</div>"
                                + "<div class=\"associationrulemetric\">" + ruleValuesAL.get(2) + "</div>"
                                + "<div class=\"associationrulemetric\">" + ruleValuesAL.get(3) + "</div>"
                                + "<div class=\"associationrulemetric\">" + ruleValuesAL.get(4) + "</div>"
                                + "<div class=\"associationrulemetric\">" + ruleValuesAL.get(5) + "</div>"
                                + "<div class=\"associationrulemetric\">" + ruleValuesAL.get(6) + "</div>"
                                + "<div class=\"associationrulemetric\">" + ruleValuesAL.get(7) + "</div>";
                    }
                }
                ruleToSave += "</li>";
            }
        }
        ruleToSave = ruleToSave.replace("_Individual", "").replace("_", " ").replace("8888.0000", "Infinity") + "</ul>";
        dbRules.databaseDisconnect(con);
    }

    public void processInsertOneChoosenRuleInDB(Integer rulenumber, String premise1, String conclusion1) {
        dbRules = new Database();
        Connection con = dbRules.databaseConnect(this._dbDetails.databaseURLName, this._dbDetails.databaseUserName, this._dbDetails.databasePasswordName, this._dbDetails.databaseDriverName);
        // 1 verificar se a regra ja existe, se não, insere 
        // 1a verificar se os conceitos ja existem na bd, se não insere
        // inserir regra referenciando as métricas respectivas todas, ou referência aos mesmos
        ArrayList<String> rule = dbRules.databaseGetOneRule(con, rulenumber);
        //dbRules.databaseDisconnect(con);
        String[] concepts = {premise, conclusion};
        String[] values = {""};
        String[] ruleStr = {"", "", ""};
        String query = "";
        int lastID = 0;
        int[] conceptID = {0, 0};
        int idcontain;
        associationruleID = 0;
        for (int m = 0; m < concepts.length; m++) {
            con = dbRules.databaseConnect(this._dbDetails.databaseURLName, this._dbDetails.databaseUserName, this._dbDetails.databasePasswordName, this._dbDetails.databaseDriverName);
            idcontain = dbRules.databaseContainsConcept(con, this.tableNames[3], "name", concepts[m]);
            //dbRules.databaseDisconnect(con);
            if (idcontain == 0) {
                con = dbRules.databaseConnect(this._dbDetails.databaseURLName, this._dbDetails.databaseUserName, this._dbDetails.databasePasswordName, this._dbDetails.databaseDriverName);
                query = "INSERT INTO concepts(idconcepts, name) values(?, ?)";
                lastID = dbRules.databaseGetTableLastID(con, this.tableNames[3], "idconcepts");
                values[0] = concepts[m];

                dbRules.databaseInsertOneDataRecord(con, query, 2, values, lastID);
                System.out.println(concepts[m] + " - inserted in db with id " + (lastID + 1));
                conceptID[m] = lastID + 1;
                //dbRules.databaseDisconnect(con);
            } else {
                System.out.println(concepts[m] + " - already in database with id " + idcontain);
                conceptID[m] = idcontain;
            }
        }

        ruleStr[0] = Integer.toString(conceptID[0]);
        ruleStr[1] = Integer.toString(conceptID[1]);
        ruleStr[2] = Integer.toString(rulenumber);
        System.out.println("rulenumber (idrules) (" + rulenumber + ") conceptA:" + ruleStr[0] + " conceptB:" + ruleStr[1] + ", rulenumber (stemmed)" + ruleStr[2]);
        idcontain = dbRules.databaseContainsRule(con, ruleStr[0], ruleStr[1], ruleStr[2]);
        if (idcontain == 0) {
            lastID = dbRules.databaseGetTableLastID(con, this.tableNames[0], "idrules");
            query = "INSERT INTO rules(idrules, idConceptA, idConceptB, idrules_stemmed) values(?, ?, ?, ?)";
            associationruleID = lastID + 1;
            if (dbRules.databaseInsertOneDataRecord(con, query, 4, ruleStr, lastID)) {
                System.out.println("rulenumber (" + rulenumber + ") - inserted in database with id " + lastID + 1);
            } else {
                System.out.println("rulenumber (" + rulenumber + ") - something went wrong inserting in database");
            }
        } else {
            associationruleID = idcontain;
            System.out.println("rulenumber (" + rulenumber + ") - already in database with id " + idcontain);
        }

        System.out.println("All ok to here");
        dbRules.databaseDisconnect(con);
    }

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
    public String getRuleToSave() {
        //rapidminerInit();
        //processResults();
        //rulessavedlist = "I have something";
        this.processGetRulesFromDb();
        return ruleToSave;
    }

    /**
     * @param aResults the results to set
     */
    public void setRuleToSave(String aResults) {
        Integer rulenumber = Integer.parseInt(aResults);
        this.processInsertOneChoosenRuleInDB(rulenumber, premise, conclusion);
    }

    /**
     * @return the results
     */
    public String getResultsConcepts() {
        //rapidminerInit();
        //processResultsConcepts();
        //processResultsFromDatabaseV2();
        //processResultsFromDatabaseV3();
        processResultsFromDatabaseV4();
        return resultsConcepts;
    }

    /**
     * @param aResults the results to set
     */
    public void setResultsConcepts(String aResults) {
        this.resultsConcepts = aResults;
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
        return premise;
    }

    public void setPremise(String premise) {
        this.premise = premise;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }
}