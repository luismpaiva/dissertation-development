<!-- 
    Document   : index
    Created on : 22/Set/2012, 1:14:46
    Author     : Luis
-->
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Association Rules Page</title>
        <link rel="stylesheet" href="css/arcss.css" type="text/css">
        
    </head>
    <body>
      <div class="backgroundblock">
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
        <div class="footer">
          <p>Webdeveloper: Luis Paiva</p>
        </div>
      </div>
        <div>
            <? 
                $dom = simplexml_load_file("xml/rules.xml");
                $query = $dom->xpath("/rules/rule/concept[@value='premise']");
                foreach ($query as $concept)
                {
                  '<li>'. $concept->keyword . '</li>';
                } 
            ?>
             teste
        </div>
        
    </body>
</html>

