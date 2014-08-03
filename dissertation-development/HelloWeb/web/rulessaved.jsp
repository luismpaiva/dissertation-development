<%-- 
    Document   : rulessaved
    Created on : 25/Mar/2013, 1:19:43
    Author     : Luis
--%>

<%@page import="seks.basic.ontology.OntologyInteractionImpl"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Association Rules Page</title>
        <link rel="stylesheet" href="css/arcss.css" type="text/css">
        <%String rule = request.getParameter("rule"); 
        String premise = request.getParameter("premise"+(rule));
        String conclusion = request.getParameter("conclusion"+(rule));
        OntologyInteractionImpl oi = new OntologyInteractionImpl();
        %>
        
        
    </head>
    <body>
        <jsp:useBean id="rulessavedbean" scope="request" class="dinont.associationrules.DinOntAssociationRules" />  
      <div class="backgroundblock" id="rulessavedpage">
        <div class="header">
          <h1>ASSOCIATION RULES</h1>
        </div>
        <div class="menu2" display="none">
            <ul>
                <li><a href="arresults.jsp" target="_self" onclick="return false;">Discover Association Rules (no concepts)</a></li>
                <li><a href="arconceptsresults.jsp" target="_self">Discover Association Rules</a></li>
                <li><a href="artodatabaseresults.jsp" target="_self" onclick="return false;">Analyse files in RM and Renew DB</a></li>
            </ul>
        </div>
        <div class="menu">
            <form name="Association Rules" target="_self" action="javascript:window.open('','_self').close();">
                <input type="submit" value="Close window" />
            </form>
        </div>
        <div class="resultsblock">
            <p><jsp:setProperty name="rulessavedbean" property="premise" value="<%= premise %>"/><jsp:setProperty name="rulessavedbean" property="conclusion" value="<%= conclusion %>"/>A premisa escolhida foi <span class="results" style="color: black;"><jsp:getProperty name="rulessavedbean" property="premise" /></span> e a conclusão <span class="results" style="color: black;"><jsp:getProperty name="rulessavedbean" property="conclusion" /></span>.<jsp:setProperty name="rulessavedbean" property="ruletosave" value="<%= rule %>"/></p>
            <p>Rules in database:</p>
            <jsp:getProperty name="rulessavedbean" property="ruletosave" />
        </div>
        <div class="footer">
          <p>Webdeveloper: Luis Paiva</p>
        </div>
      </div>
    </body>
</html>
