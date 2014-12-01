package seks.basic.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import seks.basic.ontology.OntologyInteraction;
import seks.basic.ontology.OntologyInteractionImpl;

/**
 * Servlet implementation class ConceptsTreeServlet
 */
// @WebServlet("/ConceptsTree")
public class ConceptsTreeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConceptsTreeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	OntologyInteraction oi = new OntologyInteractionImpl() ;
    	String json = this.getJSONOntologyTree(oi, "Concept", false) ;
    	response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        System.out.println(json) ;    
        response.getWriter().println(json) ;
        return ;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private String getJSONOntologyTree(OntologyInteraction oi, String clsName, boolean last) {
		ArrayList<String> subclasses = new ArrayList<String>() ;
		String obj = "" ;
		if (clsName.equals("Concept") && obj.equals("")) {
			obj = "{\"title\" : \"Concept\", \"isFolder\" : \"true\", \"children\" :[" ;
			subclasses = oi.getSubclasses("Concept");
	    	for (int i = 0; i < subclasses.size(); i++) {
	    		clsName = subclasses.get(i) ;
                        if (i == (subclasses.size() - 1)) {
                                obj += this.getJSONOntologyTree(oi, clsName, true) ;
                        }
                        else {
                                obj += this.getJSONOntologyTree(oi, clsName, false) ;
                        }
            }
	    	obj += ("]}") ;
		}
		else {
			obj += "{\"title\" : \"" + clsName + "\"" ;
			if (oi.hasSubclasses(clsName)) {
				subclasses = oi.getSubclasses(clsName) ;
				obj += ", \"isFolder\" : \"true\", \"children\" : [" ;
				for (int i = 0; i < subclasses.size(); i++) {
	        		clsName = subclasses.get(i) ;
	        		if (i == (subclasses.size() - 1)) {
	        			obj += this.getJSONOntologyTree(oi, clsName, true) ;
	        		}
	        		else {
	        			obj += this.getJSONOntologyTree(oi, clsName, false) ;
	        		}
	        	}
				obj += ("]") ;
				
			}
			if (last) {
				obj += ("}") ;
			}
			else {
				obj += ("}, ") ;
			}
		}
		return obj ;
	}

}
