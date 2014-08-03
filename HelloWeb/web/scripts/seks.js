function uploadFile() {
    $.ajax({
        url:"pages/uploadFile.html",
        dataType:"html",
        success: function(data) {
            $("#popupUpload").empty();
            $("#popupUpload").append(data);
            centerPopup('#popupUpload');
            loadPopup('#popupUpload');
        }
    });
}

function conceptsTree() {
    $.ajax({
        url: "pages/conceptsTree.html",
        dataType: "html",
        success: function(data) {
            $("#popupTree").empty();
            $("#popupTree").append(data);
            centerPopup('#popupTree');
            loadPopup('#popupTree');
                        
        }
    });
}

function about() {
    $.ajax({
        url: "pages/about.html",
        dataType: "html",
        success: function(data) {
            $("#popupAbout").empty();
            $("#popupAbout").append(data);
            centerPopup('#popupAbout');
            loadPopup('#popupAbout');
        }
    });
}

function contacts() {
    $.ajax({
        url: "pages/contacts.html",
        dataType: "html",
        success: function(data) {
            $("#popupContacts").empty();
            $("#popupContacts").append(data);
            centerPopup('#popupContacts');
            loadPopup('#popupContacts');
        }
    });
}

function loadAutocomplete() {
    var keywords = '' ;
    $.ajax({
        type: "POST",
        url: "/SemanticVectorsWeb/GetKeywords",
        beforeSend: function() {
            $('.searchGif').attr("hidden", false);
        },
        success: function(data) {
            $('.searchGif').attr("hidden", true);
            keywords = data.split(',') ;
            $('#search').autocomplete(keywords,{
                multiple: true,
                multipleSeparator: ", ",
                autoFill:true
            });
        }
    }) ;
}

function keywordsSearch() {
    $.ajax({
        type: "POST",
        url: "/SemanticVectorsWeb/KeywordsSearch",
        data: "search=" + $('#search').val() + "&radio=" + $("input:radio[name=vectorGroup]:checked").val(),
        beforeSend: function() {
            $('.searchGif').attr("hidden", false);
        },
        success: function(data) {
            $('.searchGif').attr("hidden", true);
            $('#intro').empty() ;
            $('#intro').append("<h1><span>Search Results</span></h1>") ;
            $('.main_content').empty() ;
            $('.main_content').append(data) ;
        }
    });
}

function conceptsSearch() {
    $.ajax({
        type: "POST",
        url: "/seks-tomcat/ConceptsSearch",
        data: "search=" + $('#concepts').val(),
        success: function(data) {
            
        }
    });
}

function getOntologyTree() {
    $.ajax({
        url: "/seks-tomcat/ConceptsTree",
        type: "POST",
        dataType: "json",
        beforeSend: function() {
            $('#conceptsGif').attr("hidden", false);
        },
        success: function(data) {
            $('#conceptsGif').attr("hidden", true);
            $('#tree').dynatree({
                checkbox: true,
                selectMode: 2,
                children: data,
                autoCollapse: true,

                onSelect: function(select, node) {
                    var selNodes = node.tree.getSelectedNodes();
                    var selKeys = $.map(selNodes, function(node){
                        return node.data.title ;
                    });
                    $("#concepts").val(selKeys.join(", ") + (", "));
                },
                onExpand: function(expand, node) {
                    centerPopup("#popupTree") ;
                    loadPopup("#popupTree") ;
                }
            });
        }
    });
}

function initUploadFile() {
    $('#file').customFileInput();
    $('#uploadForm').ajaxForm({
        beforeSubmit: function() {
            $('#loadingGif').attr("hidden", false);
        },
        success: function(data) {
            $('#loadingGif').attr("hidden", true);
            $('#result').append(data);
            setTimeout(function() {
                disablePopup('#popupUpload');
            }, 1000);
        }
    });
}

function initConceptsTree() {
    getOntologyTree() ;
    $('#treeForm').ajaxForm({
        success: function(data) {
            $('#intro').empty() ;
            $('#intro').append("<h1><span>Search Results</span></h1>") ;
            $('.main-content').empty() ;
            $('.main-content').append(data) ;
            disablePopup('#popupTree') ;
        }
    });
}
