var initialOpacitySubarea = 0;

function makeRequestToGetSubareaLayer(){
	
	$.ajax({
	  url: 'servlet/SubareaLayerServlet',
	  dataType: "text",
	  type:	"GET",
	  success: function(data) {
		var subareaCoordsPGs = preprocessCoords(data);
		loadSubarea(subareaCoordsPGs, map, subareaArray, requestSubareaInfoOrg);
		loadSubarea(subareaCoordsPGs, mapCompare, subareaArrayOut, requestSubareaInfo);
		
	  }
	});
}

function loadSubarea(subareaCoordsPGs, thisMap, rnArray, requestFunction){
	
	for (var i = 0; i < subareaCoordsPGs.length; i++){
		
		var gid = subareaCoordsPGs[i][0];
		var subareaCoordsPG = subareaCoordsPGs[i][1];
		createSubareaPolygon(subareaCoordsPG, gid, thisMap, rnArray, requestFunction);
	}
}

function createSubareaPolygon(subareaCoords, code, thisMap, globalSubareaArray, requestFunction)
{
	var initOption = {
			name: code,
			paths: subareaCoords,
			strokeColor: "#00008B",
			strokeOpacity: initialOpacitySubarea,
			strokeWeight: 2,
			fillColor: "#FF0000",
			fillOpacity: 0,
			clickable:false
	};
	
	var subarea = new google.maps.Polygon(initOption);
	subarea.setMap(thisMap);
	globalSubareaArray.push(subarea);	
	
	google.maps.event.addListener(subarea, 'click', function(event) {
		
		requestFunction(code, event, thisMap);
	});
}

function requestSubareaInfo(code, event, thisMap){
	
	$.ajax({
		type: "POST",
		url: "servlet/SubareaInfoServlet",
		
		data: "id=" + encodeURIComponent(code),
		success: function(data){
		//alert( "Data Saved: " + msg );
		var subareaObj = jQuery.parseJSON(data);
		showSubareaInfo(subareaObj, event, thisMap);
	   }
	});
	
}

function requestSubareaInfoOrg(code, event, thisMap){
	
	$.ajax({
		type: "POST",
		url: "servlet/SubareaInfoOrgServlet",
		
		data: "id=" + encodeURIComponent(code),
		success: function(data){
		//alert( "Data Saved: " + msg );
		var subareaObj = jQuery.parseJSON(data);
		showSubareaInfo(subareaObj, event, thisMap);
	   }
	});
	
}

function setPolyLines(array, canClick, opacity){
	
	var mapOption = {strokeOpacity:opacity, clickable:canClick};
	
	for (var i = 0; i < array.length; i++){

		array[i].setOptions(mapOption);		
	}	
}

function makeSubareaLayerClickable(array){
	
	var showOption = {clickable:true};
	
	for (var i = 0; i < array.length; i++){

		array[i].setOptions(showOption);		
	}	
	
	//alert(subareaArray[0].strokeOpacity );
	
	//alert("hide subarea!");
}

function setSubareaBoundaries(array, opacity){
	
		var mapOption = {strokeOpacity:opacity};
	for (var i = 0; i < array.length; i++){

		array[i].setOptions(mapOption);		
	}	
}

function showSubareaInfo(subareaObj, event, thisMap){
		
	//var contentString = '<table><tr><td>TravelZone: </td><td>' + subareaObj.subareaName + "</td></tr><tr><td>Area (km^2): </td><td>" + subareaObj.area + "</td></tr><tr><td>Population Number:</td><td>" + subareaObj.population;
	//contentString += "</td></tr><tr><td>HoseholdNumber:</td><td>" + subareaObj.household + "</td></tr><tr><td>Population Density:</td><td>" + subareaObj.popDensity + "</td></tr><tr><td>Household Density:</td><td>" + subareaObj.houDensity + "</td></tr></table>";
	//contentString += "ClickedLocation: <br />" + event.latLng.lat() + "," + event.latLng.lng() + "<br />";
	
	var contentString = 'Travel Zone: ' + subareaObj.subareaName + "<br/>Liveability: " + subareaObj.liveability* 100 + "%<br/>Area(km^2) :" + subareaObj.area.toFixed(2) + "<br/>Population: " + subareaObj.population;
	contentString += "<br/>Hosehold: " + subareaObj.household + "<br/>Population Density: " + subareaObj.popDensity + "(per km^2)<br/>Household Density: " + subareaObj.houDensity + "(per km^2)";
	
	coords = new google.maps.LatLng(event.latLng.lat(), event.latLng.lng());
	
	
	// Set sparkline on the map
	if (sparkMarker != null){
		sparkMarker.setMap(null);
	}
	
	setSparklineMarker(coords);

	infoWindow.setContent(contentString);
	infoWindow.setPosition(coords);
	infoWindow.open(thisMap);	
	
	setTimeout(function(){
		infoWindow.close();
	},5000);
	google.maps.event.addListener(infoWindow, 'closeclick', function(event) {
		sparkMarker.setMap(null);
	});
	
}
