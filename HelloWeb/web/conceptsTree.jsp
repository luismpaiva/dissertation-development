<%-- 
    Document   : conceptsTree
    Created on : 3/Mai/2013, 17:00:41
    Author     : Luis
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <script type="text/javascript">
        $(document).ready(initConceptsTree());
    </script>
    <a id="popupTreeClose" href="#" onClick="javascript:disablePopup('#popupTree');">x</a>
    <!--<form action="" id="treeForm" method="post"> -->
        <fieldset>
            <label>Concept</label>
            <input type="text" id="concept" name="concept" />
            <legend id="legend" >Concepts Tree</legend>
            <div id="tree"></div>
            <br>
            <label>Selected Parent</label>
            <input type="text" id="parent" name="parent" contenteditable="false" readonly="readonly" />
            <input type="submit" id="submitConceptsButton" value="Submit" name="submitConceptsButton" onclick="javascript:addNewConcept();" />
        </fieldset>
    <!--</form>-->
</html>
