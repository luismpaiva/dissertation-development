<%-- 
    Document   : arconceptsresults
    Created on : 29/Dez/2012, 17:34:12
    Author     : Luis Paiva
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Association Rules Results Page</title>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/arcss.css" type="text/css" />
        <link rel="stylesheet" href="css/dynatree.css" type="text/css" />
        
        <script type="text/javascript" src="scripts/jquery-1.7.1.js"></script>
        <script type="text/javascript" src="scripts/jquery.form.js"></script>
        <script type="text/javascript" src="scripts/jquery.popup.js"></script>
        <script type='text/javascript' src='scripts/jquery-ui.custom.js'></script>
        <script type='text/javascript' src='scripts/jquery-ui-1.8.17.custom.min.js' ></script>
        <script type='text/javascript' src='scripts/jquery.cookie.js' ></script>
        <script type="text/javascript" src="scripts/jquery.dynatree.js"></script>
        <script type="text/javascript" src="scripts/arconcepts.js"></script>
        
    </head>
    <body>
        <jsp:useBean id="arbean" scope="request" class="dokes.controller.DinOntAssociationRules" />
        
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
     <%--   <ul><li class="titlebox" id="premise">Premise</li><li class="titlebox" id="conclusion">Conclusion</li><li class="titlebox" id="premise">Premise</li><li class="titlebox" id="conclusion">Conclusion</li></ul>
            <ul><li  class="metrics" id="left"><span title="Confidence represents ... ">Conf.</span></li><li  class="metrics" id="left"><span title="Conviction represents ... ">Conv.</span></li><li  class="metrics" id="left"><span title="Gain represents ... ">Gain</span></li><li  class="metrics" id="left"><span title="Laplace represents ... ">Laplace</span></li><li  class="metrics" id="left"><span title="Lift represents ... ">Lift</span></li><li  class="metrics" id="left"><span title="Ps represents ... ">Ps</span></li><li  class="metrics" id="support"><span title="Support represents ... ">Support</span></li><li  class="metrics" id="right"><span title="Confidence represents ... ">Conf.</span></li><li  class="metrics" id="right"><span title="Conviction represents ... ">Conv.</span></li><li  class="metrics" id="right"><span title="Gain represents ... ">Gain</span></li><li  class="metrics" id="right"><span title="Laplace represents ... ">Laplace</span></li><li  class="metrics" id="right"><span title="Lift represents ... ">Lift</span></li><li  class="metrics" id="right"><span title="Ps represents ... ">Ps</span></li><li  class="metrics" id="right"><span title="Support represents ... ">Support</span></li></ul> --%>
              <jsp:getProperty name="arbean" property="resultsconcepts" />
          </div>
          <div class="footer">
            <p>Webdeveloper: Luis Paiva Paiva</p>
          </div>
        </div>
        <div id="popupTree"></div>
        <div id="backgroundPopup"></div>
    </body>
</html>
