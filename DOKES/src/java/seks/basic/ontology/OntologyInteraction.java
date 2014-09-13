package seks.basic.ontology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Property;
import java.util.ArrayList;



/**
 * Comprises all functions and algorithms that manage ontology interaction, 
 * ontology getters and setters included.
 * 
 * @author Paulo Figueiras
 */
public interface OntologyInteraction {
    
    /*  Semantic Getters  */
    
    /**
     * Retrieves the {@link com.hp.hpl.jena.ontology.Individual} object that has the input parameter as 
     * local name.
     * 
     * @param individualName    The {@link com.hp.hpl.jena.ontology.Individual}'s local name
     * 
     * @return                  A {@link com.hp.hpl.jena.ontology.Individual} object with the input 
     *                          parameter as local name
     * 
     * @see com.hp.hpl.jena.ontology.Individual
     */
    public Individual getIndividual(String individualName) ;
    
    public int getSubClassesNumber(String className) ;
    
    /**
     * Retrieves the local name of the first (direct) ontology class that is 
     * parent of the ontology resource who's local name is the input parameter.
     * 
     * @param resourceName    The {@link com.hp.hpl.jena.ontology.OntResource}'s local name
     * 
     * @return                  The {@link com.hp.hpl.jena.ontology.OntResource}'s direct parent class local name
     * 
     * @see seks.basic.ontology.OntologyInteraction#getAbsoluteParentClass(java.lang.String) 
     * @see seks.basic.ontology.OntologyInteraction#isIndividual(java.lang.String) 
     * @see seks.basic.ontology.OntologyInteraction#isClass(java.lang.String)
     * @see seks.basic.ontology.OntologyInteraction#getIndividual(java.lang.String)
     * @see com.hp.hpl.jena.ontology.Individual
     */
    public String getDirectParentClass(String resourceName) ;
    
    /**
     * Retrieves the local name of the last (absolute) ontology class that is 
     * parent of the ontology resource who's local name is the input parameter.
     * 
     * @param resourceName    The {@link com.hp.hpl.jena.ontology.OntResource}'s local name
     * 
     * @return                  The {@link com.hp.hpl.jena.ontology.OntResource}'s parent absolute class local name
     * 
     * @see seks.basic.ontology.OntologyInteraction#getDirectParentClass(java.lang.String) 
     * @see com.hp.hpl.jena.ontology.OntModel
     */
    public String getAbsoluteParentClass(String resourceName) ;
    
    /**
     * Retrieves the local names of all ontology classes that are 
     * subclasses of the ontology class which's local name is the input parameter.
     * 
     * @param resourceName    The {@link com.hp.hpl.jena.ontology.OntClass}'s local name
     * 
     * @return                  The {@link com.hp.hpl.jena.ontology.OntClass}'s 
     *                          subclasses local names, in the form of an {@link java.util.ArrayList}
     *                          object
     */
    public ArrayList<String> getAllSubclasses(String className) ;
    
    public ArrayList<String> getSubclasses(String className) ;
    
    /**
     * Retrieves the local names of all ontology classes that are 
     * parents of the ontology resource who's local name is the input parameter.
     * 
     * @param resourceName    The {@link com.hp.hpl.jena.ontology.OntResource}'s local name
     * 
     * @return                  The {@link com.hp.hpl.jena.ontology.OntResource}'s 
     *                          parent classes local names, in the form of an {@link java.util.ArrayList}
     *                          object
     * 
     * @see seks.basic.ontology.OntologyInteraction#getDirectParentClass(java.lang.String) 
     */
    public ArrayList<String> getAllParentClasses(String resourceName) ;
    
    /**
     * Retrieves the {@link com.hp.hpl.jena.ontology.Property} object that has the input parameter as 
     * local name.
     * 
     * @param propertyName  The {@link com.hp.hpl.jena.ontology.Property}'s local name
     * 
     * @return              A {@link com.hp.hpl.jena.ontology.Property} object with the input parameter 
     *                      as local name
     * 
     * @see com.hp.hpl.jena.ontology.Property
     */
    public Property getProperty(String propertyName) ;
    
    /**
     * Retrieves the {@link com.hp.hpl.jena.ontology.OntClass} object that has the input parameter as 
     * local name.
     * 
     * @param className     The {@link com.hp.hpl.jena.ontology.OntClass}'s local name
     * 
     * @return              A {@link com.hp.hpl.jena.ontology.OntClass} object with the input parameter 
     *                      as local name
     * 
     * @see com.hp.hpl.jena.ontology.OntClass
     */
    public OntClass getClass(String className) ;
    
    /**
     * Retrieves all object values from an OWL/RDF triple, with subject ({@link com.hp.hpl.jena.ontology.OntClass} or
     * {@link com.hp.hpl.jena.ontology.com.hp.hpl.jena.ontology.Individual}) and {@link Property} local names given by the input 
     * parameters.
     * 
     * @param subjectName   The local name for the subject in the OWL/RDF triple 
     * @param propertyName  The {@link com.hp.hpl.jena.ontology.Property}'s local name in the OWL/RDF 
     *                      triple 
     * 
     * @return              All object values from the OWL triple, with subject 
     *                      and property given by the input parameters, in the 
     *                      form of an {@link java.util.ArrayList}
     * 
     * @see seks.basic.ontology.OntologyInteraction#getProperty(java.lang.String) 
     * @see com.hp.hpl.jena.ontology.Property
     * @see com.hp.hpl.jena.ontology.OntResource
     * @see java.util.ArrayList
     */
    public ArrayList<String> getObjectsFromTriple(String subjectName, String propertyName) ;
    
    /**
     * Retrieves all subject values from an OWL/RDF triple, with object 
     * ({@link com.hp.hpl.jena.ontology.Literal}) and {@link com.hp.hpl.jena.ontology.Property} local names given by the input 
     * parameters.
     * 
     * @param objectName    The local name for the object in the OWL/RDF triple
     * @param propertyName  The {@link com.hp.hpl.jena.ontology.Property}'s local name in the OWL/RDF 
     *                      triple
     * 
     * @return              All subject values from the OWL triple, with object 
     *                      and property given by the input parameters, in the 
     *                      form of an {@link java.util.ArrayList}
     * 
     * @see seks.basic.ontology.OntologyInteraction#getProperty(java.lang.String) 
     * @see com.hp.hpl.jena.ontology.Property
     * @see com.hp.hpl.jena.rdf.model.Literal
     * @see java.util.ArrayList
     */
    public ArrayList<String> getSubjectsFromTriple(String objectName, String propertyName) ;
    
    /**
     * Retrieves all object values from an OWL/RDF property, with {@link com.hp.hpl.jena.ontology.Property} 
     * local name given by the input parameter.
     * 
     * @param propertyName  The {@link com.hp.hpl.jena.ontology.Property}'s local name in the OWL/RDF 
     *                      triple
     * 
     * @return              All object values from the OWL triple, with property
     *                      given by the input parameter, in the form of an 
     *                      {@link java.util.ArrayList}
     * 
     * @see seks.basic.ontology.OntologyInteraction#getProperty(java.lang.String) 
     * @see com.hp.hpl.jena.ontology.Property
     * @see java.util.ArrayList
     */public ArrayList<String> getObjectsFromProperty(String subjectName, String propertyName) ;
    
    /**
     * Retrieves all subject values from an OWL/RDF triple, with object 
     * ({@link com.hp.hpl.jena.ontology.Literal}) and {@link com.hp.hpl.jena.ontology.Property} local names given by the input 
     * parameters.
     * 
     * @param objectName    The local name for the object in the OWL/RDF triple
     * @param propertyName  The {@link com.hp.hpl.jena.ontology.Property}'s local name in the OWL/RDF 
     *                      triple
     * 
     * @return              All subject values from the OWL triple, with object 
     *                      and property given by the input parameters, in the 
     *                      form of an {@link java.util.ArrayList}
     * 
     * @see seks.basic.ontology.OntologyInteraction#getProperty(java.lang.String) 
     * @see com.hp.hpl.jena.ontology.Property
     * @see com.hp.hpl.jena.rdf.model.Literal
     * @see java.util.ArrayList
     */
    public ArrayList<String> getSubjectsFromProperty(String objectName, String propertyName) ;
    
    /**
     * Retrieves all object values from an OWL/RDF triple, with {@link com.hp.hpl.jena.ontology.Property} 
     * local name given by the input parameter.
     * 
     * @param propertyName  The {@link com.hp.hpl.jena.ontology.Property}'s local name in the OWL/RDF 
     *                      triple
     * 
     * @return              All object values from the OWL triple, with property
     *                      given by the input parameter, in the form of an 
     *                      {@link java.util.ArrayList}
     * 
     * @see seks.basic.ontology.OntologyInteraction#getProperty(java.lang.String) 
     * @see com.hp.hpl.jena.ontology.Property
     * @see java.util.ArrayList
     */
    public ArrayList<String> getAllValuesFromProperty(String propertyName) ;
    
    //public ArrayList<SemanticRelation> getStatements() ;
    
    /**
     * Retrives the taxonomical depth of the ontology resource with localname given 
     * by the input parameter.
     * 
     * @param resourceName The localname for an ontology resource
     * 
     * @return          The taxonomical depth of that resource
     * 
     * @see com.hp.hpl.jena.ontology.OntClass
     */
    public int getDepth(String resourceName) ;
    
    /*  Semantic Boolean Operators  */
    
    /**
     * Checks if the object ({@link com.hp.hpl.jena.ontology.OntResource}) with local name given by the 
     * input parameter is an {@link com.hp.hpl.jena.ontology.Individual} object.
     * 
     * @param objectName    The {@link com.hp.hpl.jena.ontology.OntResource}'s local name
     * 
     * @return              <code>true</code> if {@link com.hp.hpl.jena.ontology.OntResource} object is 
     *                      an {@link Individual} object; <code>false</code> 
     *                      otherwise
     * 
     * @see com.hp.hpl.jena.ontology.OntResource
     * @see com.hp.hpl.jena.ontology.Individual
     */
    public boolean isIndividual(String objectName) ;
    
    /**
     * Checks if the object ({@link com.hp.hpl.jena.ontology.OntResource}) with local name given by the 
     * input parameter is an {@link com.hp.hpl.jena.ontology.OntClass} object.
     * 
     * @param objectName    The {@link com.hp.hpl.jena.ontology.OntResource}'s local name
     * 
     * @return              <code>true</code> if {@link com.hp.hpl.jena.ontology.OntResource} object is 
     *                      an {@link com.hp.hpl.jena.ontology.OntClass} object; <code>false</code> 
     *                      otherwise
     * 
     * @see com.hp.hpl.jena.ontology.OntResource
     * @see com.hp.hpl.jena.ontology.OntClass
     */
    public boolean isClass (String objectName) ;
    
    /**
     * Checks if the object ({@link com.hp.hpl.jena.ontology.OntClass}) with local name given by the 
     * input parameter has subclasses.
     * 
     * @param objectName    The {@link com.hp.hpl.jena.ontology.OntClass}'s local name
     * 
     * @return              <code>true</code> if {@link com.hp.hpl.jena.ontology.OntClass} object has
     * 						subclasses; <code>false</code> otherwise
     * 
     * @see com.hp.hpl.jena.ontology.OntClass
     */
    public boolean hasSubclasses(String className) ;
    
    /*  Semantic Setters  */
    
    /**
     * Creates a new {@link com.hp.hpl.jena.ontology.Individual} in the persistence model with local name 
     * given by <code>individualName</code> as child of the ontology class with
     * local name given by <code>parentClassName</code>.
     * 
     * @param individualName    The new {@link com.hp.hpl.jena.ontology.Individual}'s local name
     * @param parentClassName   The new {@link com.hp.hpl.jena.ontology.Individual} parent class's local 
     *                          name
     * 
     * @see com.hp.hpl.jena.ontology.OntClass
     * @see com.hp.hpl.jena.ontology.Individual
     */
    public void setIndividual(String individualName, String parentClassName) ;
    
    /**
     * Creates a new {@link com.hp.hpl.jena.ontology.OntClass} in the persistence model with local name 
     * given by <code>newClassName</code> as child of the ontology class with
     * local name given by <code>parentClassName</code>.
     * 
     * @param newClassName      The new {@link com.hp.hpl.jena.ontology.OntClass}'s local name
     * @param parentClassName   The new {@link com.hp.hpl.jena.ontology.OntClass} parent class's local 
     *                          name
     * 
     * @see com.hp.hpl.jena.ontology.OntClass
     */
    public void setClass(String newClassName, String parentClassName) ;
    /**
     * Creates a new {@link com.hp.hpl.jena.ontology.ObjectProperty} in the 
     * persistence model with local name given by <code>propertyName</code>.
     * 
     * @param propertyName  The new {@link com.hp.hpl.jena.ontology.ObjectProperty}'s 
     *                      local name
     * 
     * @see com.hp.hpl.jena.ontology.ObjectProperty
     */
    public void setObjectProperty(String propertyName) ;
    
    /**
     * Creates a new {@link com.hp.hpl.jena.ontology.DatatypeProperty} in the 
     * persistence model with local name given by <code>propertyName</code>.
     * 
     * @param propertyName  The new {@link com.hp.hpl.jena.ontology.DatatypeProperty}'s 
     *                      local name
     * 
     * @see com.hp.hpl.jena.ontology.DatatypeProperty
     */
    public void setDatatypeProperty(String propertyName) ;
    
    public void setPropertyValue(String indName, String propName, String keyword) ;

}
