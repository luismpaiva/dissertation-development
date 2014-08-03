package seks.basic.ontology;

import com.hp.hpl.jena.ontology.*;

/**
 * Comprises all functions and algorithms necessary to achieve ontology 
 * persistence, and to create connection links to the persisted ontology's model.
 * 
 * @author Paulo Figueiras
 */
public interface OntologyPersistence {
    
    /**
     * Loads the ontology's persistence model.
     * 
     * @see seks.basic.ontology.OntologyPersistenceImpl#loadMaker() 
     */
    public void load() ;

    /**
     * Retrieves the ontology's persistence model
     * 
     * @return the model
     * 
     * @see com.hp.hpl.jena.ontology.OntModel
     */
    public OntModel getModel() ;
    
    /**
     * @param s_reload the s_reload to set
     */
    public void setS_reload(boolean s_reload) ;

    /**
     * Closes the connection with the persistence model.
     * 
     * @see com.hp.hpl.jena.ontology.OntModel
     * @see com.hp.hpl.jena.rdf.model.ModelMaker
     */
    public void closeCon() ;

    /**
     * Establishes a new connection with the persistence model.
     * 
     * @see com.hp.hpl.jena.ontology.OntModel
     * @see com.hp.hpl.jena.rdf.model.ModelMaker
     * @see com.hp.hpl.jena.rdf.model.ModelFactory
     */
    public void reopenCon() ;
}
