var popupStatus = 0;  

function loadPopup(divName){
	//loads popup only if it is disabled
	if(popupStatus==0){
		$("#backgroundPopup").css({
			"opacity": "0.7"
		});
		$("#backgroundPopup").fadeIn("slow");
		$(divName).fadeIn("slow");
		popupStatus = 1;
	}

}

function disablePopup(divName){
	//disables popup only if it is enabled
	if(popupStatus==1){
		$("#backgroundPopup").fadeOut("slow");
		$(divName).fadeOut("slow");
		popupStatus = 0;
	}
}

function centerPopup(divName){
	//request data for centering
	var windowWidth = document.documentElement.clientWidth;
	var windowHeight = document.documentElement.clientHeight;
	var popupHeight = $(divName).height();
	var popupWidth = $(divName).width();
	//centering
	$(divName).css({
		"position": "absolute",
		"top": windowHeight/2-popupHeight/2,
		"left": windowWidth/2-popupWidth/2
	});
	//only need force for IE6

	$("#backgroundPopup").css({
		"height": windowHeight
	});

}