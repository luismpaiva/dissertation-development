/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seks.basic.servlets;

import dokes.controller.utilities.LogHandlerClass;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import seks.basic.ontology.OntologyInteraction;
import seks.basic.ontology.OntologyInteractionImpl;

/**
 *
 * @author Luis Paiva
 */
public class AddConceptToOntoServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String concept = request.getParameter("concept") ;
        String parent = request.getParameter("parent");
        String keyword = request.getParameter("keyword") ;
        
        LogHandlerClass logFile = new LogHandlerClass();
        
        logFile.openLogFile("F:\\Dissertacao\\FrontEnd\\AddConteptToOntoServlet.txt");
        
        
        OntologyInteractionImpl oi = new OntologyInteractionImpl() ;

        System.out.println("New concept data: Concept="+concept+"; Parent="+parent+"; Keyword="+keyword);        
        
        //TODO: Insert concept in ontology; create new individual; create property "has_Keyword" with keyword
        if (!concept.contains("_Individual"))
            concept.concat("_Individual");
        oi.setIndividual(concept, parent);
        oi.setPropertyValue(concept, "has_Keyword", keyword);
        
        System.out.println("Did we insert new concept? " + oi.isIndividual(concept)+ " " );        

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().println(concept) ;
        } catch (IOException ex) {
            Logger.getLogger(AddConceptToOntoServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        logFile.closeLogFile();
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
