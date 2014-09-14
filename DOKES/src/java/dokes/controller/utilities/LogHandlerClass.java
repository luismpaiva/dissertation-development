package dokes.controller.utilities;

//<editor-fold defaultstate="collapsed" desc="Imports">  

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import seks.basic.exceptions.MissingParamException;
import seks.basic.ontology.OntologyPersistenceImpl;
import org.jdom2.*;
import org.jdom2.output.*;

//</editor-fold>

/**
 *
 * @author Luis Paiva
 */
public class LogHandlerClass {
    
    //<editor-fold defaultstate="collapsed" desc="Variables">  
    
    private FileOutputStream fis = null;
    private PrintStream out = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
    private SimpleDateFormat sdfdate = new SimpleDateFormat("dd/MM/YYYY");
    private Calendar cal;
    
    // private File sourcePath = new File("");
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">  
    
    public void LogHandlerClass() {
        // this.sourcePath = new File (sourcePath.getAbsolutePath());
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Methods">  
    
    public void openFile(String fileName) {
        File file = new File(fileName);
    
        this.fis = null;
        try {
            this.fis = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogHandlerClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.out = new PrintStream(this.fis);
    };
    
    public void closeFile() {
       try {
            this.out.close();
            this.fis.close();
        } catch (IOException ex) {
            Logger.getLogger(LogHandlerClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    };
    
    public void openLogFile(String fileName) {
        this.openFile(fileName);

        System.setOut(this.out);  
        
        this.cal = Calendar.getInstance(); 
        this.cal.getTime(); 

        System.out.println("Log Start: ["+sdf.format(this.cal.getTime())+"]");
    };
    
    public void openXMLFile(String fileName) {
        String xmlstr ="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
        this.openFile(fileName);
        this.printLnToFile(xmlstr);
        this.printLnToFile("");
        
        this.cal = Calendar.getInstance(); 
        this.cal.getTime(); 
        String dataactual = sdfdate.format(this.cal.getTime())+"";
        this.printLnToFile("<creation date=\""+dataactual+"\" time=\""+sdf.format(this.cal.getTime())+"\" />");
        
    };
    
    public void closeLogFile() {
        cal = Calendar.getInstance(); 

        cal.getTime(); 
        System.out.println("Log Finish: ["+sdf.format(cal.getTime())+"]");

        this.closeFile();
    };
    
    public void printToFile(String str) {
        this.out.print(str);
    };
    
    public void printLnToFile(String str) {
        this.out.println(str);
    };
    
    public void exportOwlFile(String fileNamePath) {
        this.openFile(fileNamePath);

        try {
            OntologyPersistenceImpl oi = new OntologyPersistenceImpl();
            oi.writeOnt(this.out);
        } catch (MissingParamException ex) {
            Logger.getLogger(LogHandlerClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LogHandlerClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LogHandlerClass.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.closeFile();
    };

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Main">  
    public static void main(String[] args){
        String RULE_XML_FILE = "rules.xml";
          LogHandlerClass l = new LogHandlerClass();
         /* SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
         * SimpleDateFormat sdfdate = new SimpleDateFormat("dd/MM/YYYY");*/        
        File f = new File("");
        //f.getAbsolutePath();
        try {
            System.out.println("Absolute path: "+f.getAbsolutePath()+" Canonical: "+f.getCanonicalPath()+" Path: "+f.getPath());
        } catch (IOException ex) {
            Logger.getLogger(LogHandlerClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    /*        l.openXMLFile(RULE_XML_FILE);
        //l.openFile(RULE_XML_FILE);
        l.printLnToFile("<rules>");
        l.printLnToFile("&nbsp;<rule>");
        l.printLnToFile("&nbsp;</rule>");
        l.printLnToFile("</rules>");
        l.closeFile();*/
//        l.exportOwlFile("ontology.owl");
        /*        l.cal = Calendar.getInstance();
         * l.cal.getTime();
         * String dataactual = sdfdate.format(l.cal.getTime()).toString();
         * String horaactual = sdf.format(l.cal.getTime()).toString();
         * 
         * Namespace ns = Namespace.getNamespace("url");
         * //Attribute att = new Attribute("space", "preserve", Namespace.NO_NAMESPACE);
         * Element rules = new Element("rules", Namespace.NO_NAMESPACE);
         * Namespace ns2 = Namespace.getNamespace("info", "http://dinontassociationrules.me/rules/info");
         * Attribute att2 = new Attribute("space", "default", ns2);
         * 
         * 
         * //        att.setName("space");
         * //rules.setAttribute(att);
         * rules.addNamespaceDeclaration(ns2);
         * 
         * Element creationinfo = new Element("creationinfo");
         * //creationinfo.setAttribute(att2);
         * creationinfo.setAttribute("date", dataactual);
         * creationinfo.setAttribute("time", horaactual);
         * //creationinfo.setAttribute("space", "default", ns2);
         * 
         * Element rule = new Element("rule");
         * Element concept = new Element("concept");
         * 
         * concept.setText("Keyword A");
         * rule.addContent(concept);
         * 
         * concept = new Element("concept");
         * concept.setText(" ");
         * rule.addContent(concept);
         * 
         * rules.addContent(creationinfo);
         * rules.addContent(rule);
         * 
         * Document doc = new Document(rules);
         * 
         * //More pretty for the command prompt
         * Format prettyFormat = Format.getPrettyFormat();
         * prettyFormat.setExpandEmptyElements(false);
         * XMLOutputter prettyXmlOut = new XMLOutputter(prettyFormat);
         */ 
          l.openFile(RULE_XML_FILE);
          // l.printToFile(prettyXmlOut.outputString(doc));
          /*          l.printLnToFile("Nail it!");
           * l.printLnToFile("Nail it!");
           * l.printLnToFile("Nail it!");
           * l.printLnToFile("Nail it!");*/
          l.closeFile();
    }
    //</editor-fold>
    
}
