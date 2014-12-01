package seks.basic.ontology;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import seks.basic.exceptions.MissingParamException;

import dokes.controller.utilities.LogHandlerClass;
import org.apache.commons.lang.time.StopWatch;

/**
 * Implementation class of interface {@link OntologyInteraction}
 * 
 * @author Paulo Figueiras
 */
public class OntologyInteractionImpl implements OntologyInteraction {
    
    LogHandlerClass log;
    
    private static String namespace = ("http://www.knowspaces.com/ontology_v1.owl#") ;
    OntModel m ;
    
    /**
     * Class constructor
     * 
     * @see seks.basic.ontology.OntologyPersistence
     * @see seks.basic.ontology.OntologyPersistence#setS_reload(boolean) 
     * @see seks.basic.ontology.OntologyPersistence#load() 
     * @see seks.basic.ontology.OntologyPersistence#getModel() 
     */
    public OntologyInteractionImpl() {
        try {
            StopWatch oiSt = new StopWatch();
            oiSt.reset();
            oiSt.start();
            OntologyPersistence op = new OntologyPersistenceImpl() ;
            /*
            op.setS_reload(false) ;
            op.load() ;
            */
            oiSt.stop();
            System.out.println("<OntologyInteractionImpl Class> Time for persistence class init: "+(oiSt.toString()));
            oiSt.reset();
            oiSt.start();

            this.m = op.getModel();
            oiSt.stop();
            System.out.println("<OntologyInteractionImpl Class> Time for getting model: "+(oiSt.toString()));
        } catch (MissingParamException ex) {
            Logger.getLogger(OntologyInteractionImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OntologyInteractionImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OntologyInteractionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
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
    @Override
    public Individual getIndividual(String individualName) {
       return m.getIndividual(namespace + individualName) ;
    }
    
    /**
     * Calculates the number of subclasses for the {@link com.hp.hpl.jena.ontology.OntClass} object that 
     * has the input parameter as local name.
     * 
     * @param className The {@link com.hp.hpl.jena.ontology.OntClass}'s local name
     * 
     * @return			The number of subclasses
     * @see com.hp.hpl.jena.ontology.OntClass
     */
    @Override
    public int getSubClassesNumber(String className) {
        OntClass cls = this.getClass(className) ;
        int count = 0 ;
        if(cls.hasSubClass()) {
            ExtendedIterator<OntClass> iter = cls.listSubClasses() ;
            while (iter.hasNext()) {
                cls = (OntClass) iter.next() ;
                count++ ;
                count += this.getSubClassesNumber(cls.getLocalName());
            }
        }
        return count ;
    }
    
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
    @Override
    public String getDirectParentClass(String resourceName) {
        OntResource resource = m.getOntResource(namespace + resourceName) ;
        if (this.isClass(resourceName)) {
            OntClass cls = resource.asClass() ;
            return cls.getSuperClass().getLocalName() ;
        }
        else if (this.isIndividual(resourceName)) {
            Individual ind = this.getIndividual(resourceName) ;
            return ind.getOntClass().getLocalName() ;
        }
        else return null ;
    }
    
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
    @Override
    public String getAbsoluteParentClass(String resourceName) {
        String className = this.getAbsoluteParentClass(resourceName) ;
        while(!m.getOntClass(namespace + className).getSuperClass().getLocalName().equals("Concept")) {
            className = m.getOntClass(namespace + className).getSuperClass().getLocalName() ;
        }
        return className ;
    }
    
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
    @Override
    public ArrayList<String> getAllParentClasses(String resourceName) {
        ArrayList<String> superClasses = new ArrayList<String>() ;
        String className = this.getDirectParentClass(resourceName) ;
        while (!className.equals("Concept")) {
            superClasses.add(className) ;
            className = this.getDirectParentClass(className) ;
        }
        superClasses.add("Concept") ;
        return superClasses ;
    }
    
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
    @Override
    public ArrayList<String> getAllSubclasses(String className) {
    	ArrayList<String> subclassesNames = new ArrayList<String>() ;
    	OntClass cls = this.getClass(className) ;
    	ExtendedIterator<OntClass> subclasses = cls.listSubClasses() ;
    	while(subclasses.hasNext()) {
    		OntClass current = subclasses.next() ;
    		subclassesNames.add(current.getLocalName()) ;
    		subclassesNames.addAll(this.getAllSubclasses(current.getLocalName())) ;
    	}
    	return subclassesNames ;
    }
    
    public ArrayList<String> getSubclasses(String className) {
    	ArrayList<String> subclassesNames = new ArrayList<String>() ;
    	OntClass cls = this.getClass(className) ;
    	ExtendedIterator<OntClass> subclasses = cls.listSubClasses() ;
    	while(subclasses.hasNext()) {
    		OntClass current = subclasses.next() ;
    		subclassesNames.add(current.getLocalName()) ;
    	}
    	return subclassesNames ;
    }
    
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
    @Override
    public Property getProperty(String propertyName) {
        return m.getProperty(namespace + propertyName) ;
    }
    
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
    @Override
    public OntClass getClass(String className) {
        return m.getOntClass(namespace + className) ;
    }
    
    /**
     * Retrieves all object values from an OWL/RDF triple, with subject 
     * ({@link com.hp.hpl.jena.ontology.OntClass} or
     * {@link com.hp.hpl.jena.ontology.Individual}) and {@link com.hp.hpl.jena.ontology.Property} 
     * local names given by the input parameters.
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
     * @see com.hp.hpl.jena.ontology.Resource
     * @see java.util.ArrayList
     */
    @Override
    public ArrayList<String> getObjectsFromTriple(String subjectName, String propertyName) {
        Property property = this.getProperty(propertyName) ;
        Resource res = m.getResource(namespace + subjectName) ;
        
        ArrayList<String> list = new ArrayList<String>() ;
        StmtIterator iter = res.listProperties(property) ;
        while (iter.hasNext()) {
            Statement stmt = iter.next() ;
            list.add(stmt.getLiteral().getString());
        }
        return list ;
    }
    
    /**
     * Retrieves all subject values from an OWL/RDF triple, with object 
     * ({@link com.hp.hpl.jena.ontology.Literal}) and {@link com.hp.hpl.jena.ontology.Property} 
     * local names given by the input parameters.
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
     * @see com.hp.hpl.jena.ontology.Literal
     * @see java.util.ArrayList
     */
    @Override
    public ArrayList<String> getSubjectsFromTriple(String objectName, String propertyName) {
        ArrayList<String> list = new ArrayList<String>() ;
        Property property = this.getProperty(propertyName) ;
        NodeIterator iter = m.listObjectsOfProperty(property) ;
        while(iter.hasNext()) {
            Literal node = iter.next().asLiteral() ;
            if (node.getString().equalsIgnoreCase(objectName)) {
                ResIterator i = m.listResourcesWithProperty(property, m.asRDFNode(node.asNode())) ;
                while (i.hasNext()) {
                    list.add(i.nextResource().getLocalName());
                }
            }
        } 
        
        return list ;
    }
    /**
     * Retrieves all object values from an OWL/RDF triple, with subject 
     * ({@link com.hp.hpl.jena.ontology.OntClass} or
     * {@link com.hp.hpl.jena.ontology.Individual}) and {@link com.hp.hpl.jena.ontology.Property} 
     * local names given by the input parameters.
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
     * @see com.hp.hpl.jena.ontology.Resource
     * @see java.util.ArrayList
     */
    @Override
    public ArrayList<String> getObjectsFromProperty(String subjectName, String propertyName) {
        Property property = this.getProperty(propertyName) ;
        Resource res = m.getResource(namespace + subjectName) ;
        
        ArrayList<String> list = new ArrayList<String>() ;
        StmtIterator iter = res.listProperties(property) ;
        while (iter.hasNext()) {
            Statement stmt = iter.next() ;
            list.add(stmt.getLiteral().getString());
        }
        return list ;
    }
    
    /**
     * Retrieves all subject values from an OWL/RDF triple, with object 
     * ({@link com.hp.hpl.jena.ontology.Literal}) and {@link com.hp.hpl.jena.ontology.Property} 
     * local names given by the input parameters.
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
     * @see com.hp.hpl.jena.ontology.Literal
     * @see java.util.ArrayList
     */
    @Override
    public ArrayList<String> getSubjectsFromProperty(String objectName, String propertyName) {
        ArrayList<String> list = new ArrayList<String>() ;
        Property property = this.getProperty(propertyName) ;
        NodeIterator iter = m.listObjectsOfProperty(property) ;
        while(iter.hasNext()) {
            Literal node = iter.next().asLiteral() ;
            if (node.getString().equalsIgnoreCase(objectName)) {
                ResIterator i = m.listResourcesWithProperty(property, m.asRDFNode(node.asNode())) ;
                while (i.hasNext()) {
                    list.add(i.nextResource().getLocalName());
                }
            }
        } 
        
        return list ;
    }
    
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
    @Override
    public ArrayList<String> getAllValuesFromProperty(String propertyName) {
        ArrayList<String> list = new ArrayList<String>() ;
        Property property = this.getProperty(propertyName) ;
        NodeIterator iter = m.listObjectsOfProperty(property) ;
        
        while (iter.hasNext()) {
            list.add(iter.next().asLiteral().getString()) ;
        }
        
        return list ;
    }
    
    /**
     * Gets all triple statements present in the ontology.
     * 
     *  @return 	A {@link java.util.ArrayList} comprised by {@link seks.basic.pojos.SemanticRelation} objects
     *  
     *  @see seks.basic.pojos.SemanticRelation
     *  @see java.util.ArrayList 
     */
    /*public ArrayList<SemanticRelation> getStatements() {
    	StmtIterator iter = m.listStatements() ;
    	ArrayList<SemanticRelation> statements = new ArrayList<SemanticRelation>() ;
    	while (iter.hasNext()) {
    		Statement stmt = iter.next() ;
    		Triple triple = stmt.asTriple() ;
    		Node propertyNode = triple.getPredicate() ;
    		ExtendedIterator<ObjectProperty> props = m.listObjectProperties() ;
    		while (props.hasNext()) {
    			String propName = props.next().getLocalName() ;
    			if (propName.equals(propertyNode.getLocalName())) {
    				SemanticRelation relation = new SemanticRelation(triple.getSubject().getLocalName(), triple.getObject().getLocalName(), propertyNode.getLocalName(), 0.0) ;
    				statements.add(relation) ;
    				break ;
    			}
    			else continue ;
    		}
    	}
    	return statements ;
    }*/
    
    
    /**
     * Retrieves the taxonomic depth of the ontology resource with local name given 
     * by the input parameter.
     * 
     * @param resourceName The local name for an ontology resource
     * 
     * @return          The taxonomic depth of that resource
     * 
     * @see com.hp.hpl.jena.ontology.OntClass
     */
    @Override
    public int getDepth(String resourceName) {
        int depth = 0 ;
        OntClass cls = null ;
        if (this.isClass(resourceName)) {
            cls = this.getClass(resourceName) ;
        }
        else if (this.isIndividual(resourceName)) {
            cls = this.getClass(this.getDirectParentClass(resourceName)) ;
        }
        if(cls.getLocalName().equals("Concept")) {
            return 0 ;
        }
        while (!cls.getLocalName().equals("Concept")) {
            depth++ ;
            cls = cls.getSuperClass() ;
        }
        return depth ;
    }
    
    
    /*  Semantic Boolean Operators  */
    
    /**
     * Checks if the object ({@link com.hp.hpl.jena.ontology.OntResource}) with local name given by the 
     * input parameter is an {@link com.hp.hpl.jena.ontology.Individual} object.
     * 
     * @param objectName    The {@link com.hp.hpl.jena.ontology.OntResource}'s local name
     * 
     * @return              <code>true</code> if {@link com.hp.hpl.jena.ontology.OntResource} object is 
     *                      an {@link com.hp.hpl.jena.ontology.Individual} object; <code>false</code> 
     *                      otherwise
     * 
     * @see com.hp.hpl.jena.ontology.OntResource
     * @see com.hp.hpl.jena.ontology.Individual
     */
    @Override
    public boolean isIndividual(String objectName) {
        OntResource resource = m.getOntResource(namespace + objectName) ;
        return resource.isIndividual() ;
    }
    
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
    @Override
    public boolean isClass (String objectName) {
        OntResource resource = m.getOntResource(namespace + objectName) ;
        return resource.isClass() ;
    }
    
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
    @Override
    public boolean hasSubclasses(String className) {
    	OntClass cls = this.getClass(className) ;
    	ExtendedIterator<OntClass> iter = cls.listSubClasses() ;
    	if (iter.hasNext()) 
    		return true ;
    	else 
    		return false ;
    }
    
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
    @Override
    public void setIndividual(String individualName, String parentClassName) {
        OntClass parentClass = m.getOntClass(namespace + parentClassName) ;
        parentClass.createIndividual(namespace + individualName) ;
    }
    
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
    @Override
    public void setClass(String newClassName, String parentClassName) {
        OntClass parentClass = m.getOntClass(namespace + parentClassName) ;
        OntClass newClass = m.createClass(namespace + newClassName) ;
        //parentClass.setSubClass(newClass);
        newClass.setSuperClass(parentClass);
    }
    
    /**
     * Creates a new {@link com.hp.hpl.jena.ontology.ObjectProperty} in the 
     * persistence model with local name given by <code>propertyName</code>.
     * 
     * @param propertyName  The new {@link com.hp.hpl.jena.ontology.ObjectProperty}'s 
     *                      local name
     * 
     * @see com.hp.hpl.jena.ontology.ObjectProperty
     */
    public void setObjectProperty(String propertyName) {
        m.createObjectProperty(namespace + propertyName) ;
    }
    
    /**
     * Creates a new {@link com.hp.hpl.jena.ontology.DatatypeProperty} in the 
     * persistence model with local name given by <code>propertyName</code>.
     * 
     * @param propertyName  The new {@link com.hp.hpl.jena.ontology.DatatypeProperty}'s 
     *                      local name
     * 
     * @see com.hp.hpl.jena.ontology.DatatypeProperty
     */
    public void setDatatypeProperty(String propertyName) {
        m.createDatatypeProperty(namespace + propertyName) ;
    }
    
    public void setPropertyValue(String indName, String propName, String keyword) {
        Individual ind = this.getIndividual(indName) ;
          Property prop = this.getProperty(propName) ;
          m.add(ind, prop, keyword) ;
    }
}


