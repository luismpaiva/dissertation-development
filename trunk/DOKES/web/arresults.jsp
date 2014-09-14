<%-- 
    Document   : arresults
    Created on : 22/Set/2012, 13:08:16
    Author     : Luis Paiva
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Association Rules Results Page</title>
        <link rel="stylesheet" href="css/arcss.css" type="text/css">
    </head>
    <body>
        <jsp:useBean id="arbean" scope="request" class="doars.controller.DinOntAssociationRules" />

        <div class="backgroundblock"> 
          <div class="header">
            <h1>ASSOCIATION RULES</h1>
          </div>
          <div class="menu">
            <a href="arresults.jsp" target="_self" onclick="return false">Discover Association Rules (no concepts)
                <form name="Association Rules" target="_self" action="arresults.jsp">
                    <input type="submit" value="Discover Association Rules (no concepts)" disabled />
                </form>
            </a>
            <a href="arconceptsresults.jsp" target="_self">Discover Association Rules
              <form id="association_rules2" name="Association Rules" target="_self" action="arconceptsresults.jsp">
                  <input type="submit" value="Discover Association Rules" />
              </form>
            </a>
            <a href="artodatabaseresults.jsp" target="_self" onclick="return false">Analyse files in RM and Renew DB
              <form name="Association Rules" target="_self" action="artodatabaseresults.jsp">
                  <input type="submit" value="Analyse files in RM and Renew DB" disabled />
              </form>
            </a>
          </div>
          <div class="resultsblock"> 
            <ul><li>Premise</li><li>Conclusion</li><li  class="metrics"><span title="Confidence represents ... ">Conf.</span></li><li  class="metrics"><span title="Conviction represents ... ">Conv.</span></li><li  class="metrics"><span title="Gain represents ... ">Gain</span></li><li  class="metrics"><span title="Laplace represents ... ">Laplace</span></li><li  class="metrics"><span title="Lift represents ... ">Lift</span></li><li  class="metrics"><span title="Ps represents ... ">Ps</span></li><li  class="metrics"><span title="Support represents ... ">Support</span></li><li  class="num_rules"><span title="Rule Index">N</span></li></ul>
              <jsp:getProperty name="arbean" property="results" />
          </div>
          <div class="footer">
            <p>Webdeveloper: Luis Paiva Paiva</p>
          </div>
        </div>
    </body>
</html>
