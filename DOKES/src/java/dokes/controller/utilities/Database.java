/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dokes.controller.utilities;

import dokes.controller.Rule;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

  
/**
 *
 * @author Luis
 */
public class Database {
  
  private String dbURL = "";
  private String dbUser = "";
  private String dbPass = "";
  private String dbDriver = "";
  
  
  public Database(){
    
  }
  
  public Connection databaseConnect(String uRL, String user, String pass, String driver){
    
    this.dbURL = uRL;
    this.dbUser = user;
    this.dbPass = pass;
    this.dbDriver = driver;
    
    try {
      Class.forName(driver);

      Connection con;
      con = DriverManager.getConnection(uRL, user, pass);

      return con;
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public boolean databaseDisconnect(Connection con){
    try {
      con.close();
    } catch (SQLException ex) {
      System.out.println("Failed to Disconnect from DB: " +ex);
    }
    return true;
  }
  
  public boolean databaseSelectData(Connection con, String dbQuery) {
    try {
      Statement select = con.createStatement();
      ResultSet result = select.executeQuery(dbQuery);
 
      while(result.next()) { 
        int key = result.getInt(1);
        String val = result.getString(2);
        
        System.out.print("Result: ");
        System.out.print("key = " + key);
        System.out.println(" val = " + val);
      }
      result.close();
      select.close();
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }
  
  public Integer databaseContainsConcept(Connection con, String table, String column, String element) {
      // Return 0 if element not in table or return element id if otherwise
    String dbQuery = "SELECT * FROM "+table+" WHERE "+column+"='"+element+"'";
    int id = 0;
    try {
      Statement select = con.createStatement();
      ResultSet result = select.executeQuery(dbQuery);
 
      while(result.next()) { 
        id = result.getInt(1);
        String val = result.getString(2);
      }
      result.close();
      select.close();
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return id;
  }

  public Integer databaseContainsRule(Connection con, String conceptA, String conceptB, String rulenumber) {
      // Return 0 if element not in table or return element id if otherwise
    String dbQuery = "SELECT * FROM rules "
            + "WHERE idConceptA='"+conceptA+"'"
            + "AND idConceptB='"+conceptB+"'"
            + "AND idrules_stemmed='"+rulenumber+"'";
    int id = 0;
    try {
      Statement select = con.createStatement();
      ResultSet result = select.executeQuery(dbQuery);
 
      while(result.next()) { 
        id = result.getInt(1);
        String val = result.getString(2);
      }
      result.close();
      select.close();
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return id;
  }

  
  
  public HashMap<Integer, ArrayList<String>> databaseGetRules(Connection con, String table) {
      HashMap<Integer, ArrayList<String>> rulesHM = new HashMap<Integer, ArrayList<String>>();
      ArrayList<String> ruleValuesAL = new ArrayList<String>();
      try {
        Statement select = con.createStatement();
        Statement select2 = con.createStatement();
        Statement select3 = con.createStatement();
        String dbQuery = "SELECT * FROM "+table;
        ResultSet result = select.executeQuery(dbQuery);
        
        while(result.next()) {
          
          int ruleNum = result.getInt(1);

          String dbQuery2 = "SELECT * FROM stemmed_word WHERE idstemmed_word='"+result.getString(2)+"'";
          ResultSet result2 = select2.executeQuery(dbQuery2);
          result2.next();
          ruleValuesAL.add(result2.getString(2)); // Get ConceptA
          
          String dbQuery3 = "SELECT * FROM stemmed_word WHERE idstemmed_word='"+result.getString(3)+"'";
          ResultSet result3 = select3.executeQuery(dbQuery3);
          result3.next();
          ruleValuesAL.add(result3.getString(2)); // Get ConceptB

          System.out.println("Rule ("+ruleNum+"): "+"«"+result2.getString(2)+"»«"+result3.getString(2)+"»");
          
          ruleValuesAL.add(result.getString(4)); // Get conviction
          ruleValuesAL.add(result.getString(5)); // Get gain
          ruleValuesAL.add(result.getString(6)); // Get lift
          ruleValuesAL.add(result.getString(7)); // Get laplace
          ruleValuesAL.add(result.getString(8)); // Get ps
          ruleValuesAL.add(result.getString(9)); // Get support
          ruleValuesAL.add(result.getString(10)); // Get confidence
          
          rulesHM.put(ruleNum, ruleValuesAL);
          System.out.println("Rule ("+ruleNum+"): "+"«"+result2.getString(2)+"»«"+result3.getString(2)+"»«"+result.getString(4)+"»«"+result.getString(5)+"»«"+result.getString(6)+"»«"+result.getString(7)+"»«"+result.getString(8)+"»«"+result.getString(9)+"»«"+result.getString(10)+"»");
          
          ruleValuesAL = new ArrayList<String>();
          result2.close();
          result3.close();
        }
        result.close();
        select.close();
      } catch (SQLException ex) {
        Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      return rulesHM;
  }
  
  public HashMap<Integer, Rule> databaseGetRules2(Connection con, String table) {
      HashMap<Integer, Rule> rulesHM = new HashMap<Integer, Rule>();
      Rule ruleValuesAL = new Rule();
      try {
        Statement select = con.createStatement();
        Statement select2 = con.createStatement();
        Statement select3 = con.createStatement();
        String dbQuery = "SELECT * FROM "+table;
        ResultSet result = select.executeQuery(dbQuery);
        
        
        
        while(result.next()) {
          
          int ruleNum = result.getInt(1);

          String dbQuery2 = "SELECT * FROM stemmed_word WHERE idstemmed_word='"+result.getString(2)+"'";
          ResultSet result2 = select2.executeQuery(dbQuery2);
          result2.next();
          ruleValuesAL.setPremise(result2.getString(2)); // Get ConceptA
          
          String dbQuery3 = "SELECT * FROM stemmed_word WHERE idstemmed_word='"+result.getString(3)+"'";
          ResultSet result3 = select3.executeQuery(dbQuery3);
          result3.next();
          ruleValuesAL.setConclusion(result3.getString(2)); // Get ConceptB

          System.out.println("Rule ("+ruleNum+"): "+"«"+result2.getString(2)+"»«"+result3.getString(2)+"»");
          
          ruleValuesAL.setConviction(result.getString(4)); // Get conviction
          ruleValuesAL.setGain(result.getString(5)); // Get gain
          ruleValuesAL.setLift(result.getString(6)); // Get lift
          ruleValuesAL.setLaplace(result.getString(7)); // Get laplace
          ruleValuesAL.setPs(result.getString(8)); // Get ps
          ruleValuesAL.setTotalsupport(result.getString(9)); // Get support
          ruleValuesAL.setConfidence(result.getString(10)); // Get confidence
          
          rulesHM.put(ruleNum, ruleValuesAL);
          System.out.println("Rule ("+ruleNum+"): "+"«"+result2.getString(2)+"»«"+result3.getString(2)+"»«"+result.getString(4)+"»«"+result.getString(5)+"»«"+result.getString(6)+"»«"+result.getString(7)+"»«"+result.getString(8)+"»«"+result.getString(9)+"»«"+result.getString(10)+"»");
          
          ruleValuesAL = new Rule();
          result2.close();
          result3.close();
        }
        result.close();
        select.close();
      } catch (SQLException ex) {
        Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      return rulesHM;
  }
  
  public HashMap<Integer, ArrayList<String>> databaseGetAllRules(Connection con) {
      HashMap<Integer, ArrayList<String>> rulesHM = new HashMap<Integer, ArrayList<String>>();
      ArrayList<String> ruleValuesAL = new ArrayList<String>();
      try {
        Statement select = con.createStatement();
        Statement select2 = con.createStatement();
        Statement select3 = con.createStatement();
        String dbQuery = "SELECT idrules, `idConceptA`, `idConceptB`, conviction, gain, lift, laplace, ps, support, confidence FROM rules, rules_stemmed WHERE rules.idrules_stemmed=rules_stemmed.idrules_stemmed";
        ResultSet result = select.executeQuery(dbQuery);
        
        while(result.next()) {
          
          int ruleNum = result.getInt(1);
          
          int idConceptA = result.getInt(2);
          String dbQuery2 = "SELECT name FROM concepts WHERE idconcepts='"+idConceptA+"'";
          ResultSet result2 = select2.executeQuery(dbQuery2);
          result2.next();
          ruleValuesAL.add(result2.getString(1)); // Get ConceptA
          
          int idConceptB = result.getInt(3);
          String dbQuery3 = "SELECT name FROM concepts WHERE idconcepts='"+idConceptB+"'";
          ResultSet result3 = select3.executeQuery(dbQuery3);
          result3.next();
          ruleValuesAL.add(result3.getString(1)); // Get ConceptB

          ruleValuesAL.add(result.getString(4)); // Get conviction
          ruleValuesAL.add(result.getString(5)); // Get gain
          ruleValuesAL.add(result.getString(6)); // Get lift
          ruleValuesAL.add(result.getString(7)); // Get laplace
          ruleValuesAL.add(result.getString(8)); // Get ps
          ruleValuesAL.add(result.getString(9)); // Get support
          ruleValuesAL.add(result.getString(10)); // Get confidence
          
          rulesHM.put(ruleNum, ruleValuesAL);
          System.out.println("Rule ("+ruleNum+"): "+"«"+result2.getString(1)+"»«"+result3.getString(1)+"»«"+result.getString(4)+"»«"+result.getString(5)+"»«"+result.getString(6)+"»«"+result.getString(7)+"»«"+result.getString(8)+"»«"+result.getString(9)+"»«"+result.getString(10)+"»");
          
          ruleValuesAL = new ArrayList<String>();
          result2.close();
          result3.close();
        }
        result.close();
        select.close();
      } catch (SQLException ex) {
        Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      return rulesHM;
  }
  
  
    public ArrayList<String> databaseGetOneRule(Connection con, int rulenumber) {
      ArrayList<String> ruleValuesAL = new ArrayList<String>();
      try {
        Statement select = con.createStatement();
        Statement select2 = con.createStatement();
        Statement select3 = con.createStatement();
        String dbQuery = "SELECT * FROM rules_stemmed WHERE idrules_stemmed="+rulenumber;
        ResultSet result = select.executeQuery(dbQuery);
        
        while(result.next()) {
          
          ruleValuesAL.add(result.getString(2)); // Get StemmedConceptA
          ruleValuesAL.add(result.getString(3)); // Get StemmedConceptB
 
          String dbQuery2 = "SELECT * FROM stemmed_word WHERE idstemmed_word="+result.getString(2);
          ResultSet result2 = select2.executeQuery(dbQuery2);
          result2.next();
          ruleValuesAL.add(result2.getString(2)); // Get StemmedConceptA
          
          String dbQuery3 = "SELECT * FROM stemmed_word WHERE idstemmed_word="+result.getString(3);
          ResultSet result3 = select3.executeQuery(dbQuery3);
          result3.next();
          ruleValuesAL.add(result3.getString(2)); // Get ConceptB
          
          
          
          ruleValuesAL.add(result.getString(4)); // Get conviction
          ruleValuesAL.add(result.getString(5)); // Get gain
          ruleValuesAL.add(result.getString(6)); // Get lift
          ruleValuesAL.add(result.getString(7)); // Get laplace
          ruleValuesAL.add(result.getString(8)); // Get ps
          ruleValuesAL.add(result.getString(9)); // Get support
          ruleValuesAL.add(result.getString(10)); // Get confidence
          
          System.out.println("Rule ("+rulenumber+"): "+"«"+result.getString(2)+"»«"+result.getString(3)+"»«"+result.getString(4)+"»«"+result.getString(5)+"»«"+result.getString(6)+"»«"+result.getString(7)+"»«"+result.getString(8)+"»«"+result.getString(9)+"»«"+result.getString(10)+"»");
          
        }
        result.close();
        select.close();
      } catch (SQLException ex) {
        Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      return ruleValuesAL;
  }
  
  public boolean databaseDeleteAllRecordsFromTable(Connection con, String table) {
    try {
      Statement select = con.createStatement();
      String dbQuery = "DELETE FROM "+table;
      int result = select.executeUpdate(dbQuery);
      
      if (result > 0)
        System.out.println("All records deleted from table "+table+".");
      else 
        System.out.println("No records deleted from table "+table+".");
      dbQuery = "ALTER TABLE "+table+" AUTO_INCREMENT = 1";
      result = select.executeUpdate(dbQuery);
      
      select.close();
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }
  
  public boolean databaseInsertOneDataRecord(Connection con, String dbQuery, int numColumns, String[] values, int id) {
    try {
      PreparedStatement pstmt = con.prepareStatement(dbQuery);
      
      pstmt.setInt(1, id+1); // Adds +1 to last index in database

      for (int i=0; i<numColumns-1; i++){
        System.out.print("«i:"+(i)+"» ");
        pstmt.setString(i+2, values[i]);
        System.out.println("«"+(i+2)+"» "+values[i]+" --");
      }
      
      pstmt.executeUpdate(); // execute insert statement
      pstmt.close();
      
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public int databaseGetTableLastID(Connection con, String table, String columnname){
    String sqlQuery = "SELECT MAX("+columnname+") FROM "+table;
    int key = 0;
 
    try {
      Statement select = con.createStatement();
      ResultSet result = select.executeQuery(sqlQuery);
      while(result.next()) { 
        key = result.getInt(1);
      }
      result.close();
      select.close();
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return key;
  }
  
  public int databaseRecordID(Connection con, String table, String idColumnName, String record, String recordColumnName){
    String sqlQuery = "SELECT "+idColumnName+" FROM "+table+" WHERE "+recordColumnName+" = '"+record+"'";
    int key = 0;
 
    try {
      Statement select = con.createStatement();
      ResultSet result = select.executeQuery(sqlQuery);
      while(result.next()) { 
        key = result.getInt(1);
      }
      result.close();
      select.close();
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return key;
  }
  
  public boolean databaseInsertRule(Connection con, ArrayList<String> valuesAL) {
    int id = this.databaseGetTableLastID(con, "rules_stemmed", "idrules_stemmed");
    System.out.print("«last id:"+id+"»");

    //String sqlQuery = "SELECT * FROM rules_stemmed";
    String[] values = {valuesAL.get(1),valuesAL.get(2),valuesAL.get(3),valuesAL.get(4),valuesAL.get(5),valuesAL.get(6),valuesAL.get(7),valuesAL.get(8), valuesAL.get(9)}; 
    System.out.println("Values [0]:"+valuesAL.get(0)+" [1]:"+valuesAL.get(1)+" [2]:"+valuesAL.get(2)+"Values[2]:"+valuesAL.get(2)+"Values[3]:"+valuesAL.get(3)+"Values[4]:"+valuesAL.get(4)+"Values[5]:"+valuesAL.get(5)+"Values[6]:"+valuesAL.get(6)+"Values[7]:"+valuesAL.get(7)+"Values[8]:"+valuesAL.get(8));
    System.out.println("Values[9]:"+valuesAL.get(9));
    String query = "INSERT INTO rules_stemmed(idrules_stemmed, StemmedConceptA, StemmedConceptB, conviction, gain, lift, laplace, ps, support, confidence) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    this.databaseInsertOneDataRecord(con, query, 10, values, id);
    
    return true;
  }
  
  public boolean databaseInsertRulev2(Connection con, ArrayList<String> valuesAL) {
    int id = this.databaseGetTableLastID(con, "rules_stemmed", "idrules_stemmed");
    System.out.print("«last id:"+id+"»");

    //String sqlQuery = "SELECT * FROM rules_stemmed";
    String[] values = {valuesAL.get(1),valuesAL.get(2),valuesAL.get(3),valuesAL.get(4),valuesAL.get(5),valuesAL.get(6),valuesAL.get(7),valuesAL.get(8), valuesAL.get(9)}; 
    System.out.println("Values [0]:"+valuesAL.get(0)+" [1]:"+valuesAL.get(1)+" [2]:"+valuesAL.get(2)+"Values[2]:"+valuesAL.get(2)+"Values[3]:"+valuesAL.get(3)+"Values[4]:"+valuesAL.get(4)+"Values[5]:"+valuesAL.get(5)+"Values[6]:"+valuesAL.get(6)+"Values[7]:"+valuesAL.get(7)+"Values[8]:"+valuesAL.get(8));
    System.out.println("Values[9]:"+valuesAL.get(9));
    String query = "INSERT INTO rules_stemmed(idrules_stemmed, idstemmed_wordA, idstemmed_wordB, conviction, gain, lift, laplace, ps, support, confidence) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    this.databaseInsertOneDataRecord(con, query, 10, values, id);
    
    return true;
  }
  
  public boolean databaseInsertKeyword(){
  
    
    return true;
  } 
  
  public boolean databaseInsertConcept(){return true;} 
  
  public boolean databaseInsertStemmedWord(){return true;} 
  
}
