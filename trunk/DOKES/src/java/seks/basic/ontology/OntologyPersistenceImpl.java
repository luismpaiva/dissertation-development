package seks.basic.ontology;

import java.io.*;
import com.hp.hpl.jena.db.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import seks.basic.exceptions.MissingParamException;

/**
 * Implementation class of interface {@link OntologyPersistence}
 * 
 * @author Paulo Figueiras
 * 
 * @see OntologyPersistence
 */
public class OntologyPersistenceImpl implements OntologyPersistence {

    private HashMap<String, String> params = new HashMap<String, String>() ;
    private String configFilePath = "D:\\NetBeansProjects\\DOKES\\src\\java\\" ;
    private static final String configFileName = "jenaConfig.xml" ;

    private String s_params[] = {"ont_file", "db_url", "db_user", "db_passwd", "db", "db_driver"} ;
    private String s_dbURL ;
    private String s_dbUser ;
    private String s_dbPw = "" ;
    private String s_dbType ;
    private String s_dbDriver ;

    // if true, reload the data
    private boolean s_reload = false;

    // source URL to load data from; if null, use default
    private String s_source;

    private ModelMaker maker ;

    private OntModel m ;

    /**
     * Class Constructor
     * 
     * @throws MissingParamException
     * @throws IOException
     * @throws ClassNotFoundException
     * 
     * @see #loadConfigParams() 
     */
    public OntologyPersistenceImpl() throws MissingParamException, IOException, ClassNotFoundException {
        loadConfigParams();
        load();
    }

    /**
     * Loads the XML file with the database URL, user, password and MySQL driver
     * used to open a link to the ontology's persistence model
     * 
     * @throws seks.basic.exceptions.MissingParamException
     * @throws IOException
     * @throws ClassNotFoundException 
     * 
     * @see DOMParser
     * @see Document
     * @see Element
     * @see seks.basic.exceptions.MissingParamException
     */
    private void loadConfigParams() throws MissingParamException, IOException, ClassNotFoundException {
        DOMParser domp = new DOMParser();
        try {
            //this.configFilePath = this.getClass().getClassLoader().getResource(configFileName).toString() ;
            //configFilePath = configFilePath.substring(0, configFilePath.indexOf(configFileName)) ;
            domp.parse(configFilePath + configFileName);
            Document doc = domp.getDocument() ;
            Element root = doc.getDocumentElement() ;
            for(String param: s_params){
                NodeList paramNode = root.getElementsByTagName(param) ;
                if(paramNode != null){
                    Element el = (Element) paramNode.item(0) ;
                    String paramVal = el.getFirstChild().getNodeValue() ;
                    if(paramVal != null)
                        params.put(param, paramVal) ;
                    else
                        throw new MissingParamException("Configuration file incomplete.") ;
                }
            }
            s_source = configFilePath + params.get(s_params[0]) ;
            s_dbURL = params.get(s_params[1]) ;
            s_dbUser = params.get(s_params[2]) ;
            //s_dbPw = params.get(s_params[3]) ;
            s_dbType = params.get(s_params[4]) ;
            s_dbDriver = params.get(s_params[5]) ;
            Class.forName( s_dbDriver );
        } catch (SAXException ex) {
            Logger.getLogger(OntologyPersistenceImpl.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1) ;
        } 
    }

    /**
     * Loads the ontology's persistence model.
     * 
     * @see #loadMaker() 
     */
    @Override
    public void load() {
        loadMaker() ;
    }

    /**
     * Generates the persistence model in the database, if not created (flag 
     * {@link seks.basic.ontology.OntologyPersistence#s_reload}), and load the 
     * persisted model.
     * 
     * @see seks.basic.ontology.OntologyPersistence#getRDBMaker(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean) 
     * @see seks.basic.ontology.OntologyPersistence#loadDB(com.hp.hpl.jena.rdf.model.ModelMaker, java.lang.String) 
     */
    private void loadMaker() {
         if (isS_reload()) {

            // we pass cleanDB=true to clear out existing models
            // NOTE: this will remove ALL Jena models from the named persistent store, so
            // use with care if you have existing data stored
            maker = getRDBMaker( s_dbURL, s_dbUser, s_dbPw, s_dbType, true ) ;

            // now load the source data into the newly cleaned db
            loadDB( maker, s_source );
            s_reload = false ;
        }

        // now we list the classes in the database, to show that the persistence worked
        maker = getRDBMaker( s_dbURL, s_dbUser, s_dbPw, s_dbType, false );

        Model base = maker.createModel( s_source, false );

        m = ModelFactory.createOntologyModel( getModelSpec( maker ), base );
    }

    /**
     * Reads the ontology file (.owl or .rdf), and creates the ontology's 
     * persistence model
     * 
     * @param maker     A {@link com.hp.hpl.jena.rdf.model.ModelMaker} object
     * @param source    The name and path for an ontology file
     * 
     * @see seks.basic.ontology.OntologyPersistence#getModelSpec(com.hp.hpl.jena.rdf.model.ModelMaker) 
     * @see com.hp.hpl.jena.ontology.OntModel
     * @see com.hp.hpl.jena.rdf.model.ModelFactory
     * @see InputStream
     */
    private void loadDB( ModelMaker maker, String source ) {
        // use the model maker to get the base model as a persistent model
        // strict=false, so we get an existing model by that name if it exists
        // or create a new one
        Model base = maker.createModel( source, false );

        // now we plug that base model into an ontology model that also uses
        // the given model maker to create storage for imported models
        m = ModelFactory.createOntologyModel( getModelSpec( maker ), base );

        InputStream in = FileManager.get().open( source );
        if (in == null) {
            throw new IllegalArgumentException(
                                         "File: " + source + " not found" );
        }

        // now load the source document, which will also load any imports
        m.read( in,  "" );
    }
    
    /**
     * Creates a new connection to the database and generates an empty 
     * persistence {@link ModelMaker}, which will contain the persisted ontology's 
     * model.
     * 
     * @param dbURL     An URL to the database
     * @param dbUser    A username for the database
     * @param dbPw      The user's password for the database
     * @param dbType    A database type (MySQL, PostGRES, Oracle)
     * @param cleanDB   If <code>true</code>, cleans the database
     * 
     * @return          A ontology's empty persistence 
     *                  {@link com.hp.hpl.jena.rdf.model.ModelMaker}
     * 
     * @see com.hp.hpl.jena.db.IDBConnection
     * @see com.hp.hpl.jena.rdf.model.ModelMaker
     */

    private ModelMaker getRDBMaker( String dbURL, String dbUser, String dbPw, String dbType, boolean cleanDB ) {
        try {
            // Create database connection
            IDBConnection conn  = new DBConnection( dbURL, dbUser, dbPw, dbType );

            // do we need to clean the database?
            if (cleanDB) {
                conn.cleanDB();
            }

            // Create a model maker object
            return ModelFactory.createModelRDBMaker( conn );
        }
        catch (Exception e) {
            System.exit( 1 );
        }

        return null;
    }

    /**
     * Creates a spec for the new ont model that will use no inference over 
     * models made by the given maker (which is where we get the persistent 
     * models from).
     * 
     * @param maker     A {@link com.hp.hpl.jena.rdf.model.ModelMaker} object
     * 
     * @return          An {@link com.hp.hpl.jena.ontology.OntModelSpec} object
     * 
     * @see com.hp.hpl.jena.rdf.model.ModelMaker
     * @see com.hp.hpl.jena.ontology.OntModelSpec
     */
    public OntModelSpec getModelSpec( ModelMaker maker ) {
        OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_MEM );
        spec.setImportModelMaker( maker );

        return spec;
    }

    /**
     * 
     * @return the model
     */
    @Override
    public OntModel getModel() {
        return m ;
    }

    /**
     * @return the s_reload
     */
    public boolean isS_reload() {
        return s_reload;
    }

    /**
     * @param s_reload the s_reload to set
     */
    @Override
    public void setS_reload(boolean s_reload) {
        this.s_reload = s_reload;
    }

    /**
     * Closes the connection with the persistence model.
     * 
     * @see com.hp.hpl.jena.ontology.OntModel
     * @see com.hp.hpl.jena.rdf.model.ModelMaker
     */
    @Override
    public void closeCon() {
        m = null ;
        this.maker.close();
    }

    /**
     * Establishes a new connection with the persistence model.
     * 
     * @see com.hp.hpl.jena.ontology.OntModel
     * @see com.hp.hpl.jena.rdf.model.ModelMaker
     * @see com.hp.hpl.jena.rdf.model.ModelFactory
     */
    @Override
    public void reopenCon() {
        this.maker.openModel(s_source, s_reload) ;
        Model base = maker.createModel( s_source, false );
        m = ModelFactory.createOntologyModel( getModelSpec( maker ), base );
    }

    /**
     * Writes the ontology in RDF form, through an {@link OutputStream} object.
     * 
     * @param out   An {@link OutputStream} object
     * 
     * @see OutputStream
     */
    public void writeOnt(OutputStream out) {
        m.write(out, "RDF/XML") ;
    }
}
