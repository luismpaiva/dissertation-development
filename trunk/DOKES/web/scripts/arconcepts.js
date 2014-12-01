/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var keyword_id = "";

function conceptsTree(id) {
    keyword_id = id ;
    $.ajax({
        url: "conceptsTree.jsp",
        dataType: "html",
        success: function(data) {
            $("#popupTree").empty();
            $("#popupTree").append(data);
            centerPopup('#popupTree');
            loadPopup('#popupTree');
                        
        }
    });
}

function getOntologyTree() {
    $.ajax({
        url: "/DOKES/ConceptsTree",
        type: "POST",
        dataType: "json",
        success: function(data) {
            $('#tree').dynatree({
                selectMode: 1,
                checkbox: true,
                children: data,
                autoCollapse: true,

                onSelect: function(select, node) {
                    var selNodes = node.tree.getSelectedNodes();
                    var selKeys = $.map(selNodes, function(node){
                        return node.data.title ;
                    });
                    $("#parent").val(selKeys);
                },
                onExpand: function(expand, node) {
                    centerPopup("#popupTree") ;
                    loadPopup("#popupTree") ;
                }
            });
        }
    });
}

function initConceptsTree() {
    getOntologyTree() ;
}

function addNewConcept() {
    var concept = $("#concept").val();
    var parent = $("#parent").val();
    
    var keyword = $("#" + keyword_id).text();
    keyword = keyword.replace(" (exact match not found) Candidates:", "") ;
    $("#" + keyword_id).text(keyword);
    var str = "concept=" + concept + "&parent=" + parent + "&keyword=" + keyword;
    callAjax(str);
}

function callAjax(str) {
    $.ajax({
        url: "/DOKES/AddConceptToOnto",
        type: "POST",
        data: str,
        dataType: "text",
        async:false,
        success: function(data) {
            disablePopup('#popupTree');
            if ((keyword_id.indexOf("premise") > -1) || (keyword_id.indexOf("conclusion") > -1) ) {
                var select_id = keyword_id.replace("_","");
                $("#" + select_id).empty() ;
                $("#" + select_id).append("<option class='level0' value='" + data + "_Individual'>" + data + "</option>") ;
                
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            alert(xhr.status);
            alert(thrownError);
        }
    }) ;
}