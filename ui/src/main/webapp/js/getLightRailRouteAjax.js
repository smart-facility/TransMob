/**
 * Get coordinates from shapefile through Ajax for displaying on the Google Map
 * @author qun
 * @version 0.1 (2011-11-24)
 */

var initialOpacityLightTrain = 0;

function makeRequestToGetLightRailRouteNetworkLayer(){
	
	$.ajax({
	  url: 'servlet/LightRailRouteServlet',
	  type: 'GET',
	  dataType: "text",
	  success: function(data) {
		var lightRailCoordsPGs = preprocessCoords(data);
//		
		loadLightRailNetwork(lightRailCoordsPGs, map, lightRailRouteArray);
		loadLightRailNetwork(lightRailCoordsPGs, mapEdit, lightRailRouteArrayEdit);
//		  alert(data);
	  }
	});
}

function getLightRailRoute(rnArray){
	
	$.ajax({
		url: 'servlet/LightRailRouteServlet',
		type: 'GET',
		
//		data: "id=" + routeName,
		success: function(data){
//			var singleBusCoordsPGs = preprocessCoords(data);
//			createBusNetworkPolyLine(singleBusCoordsPGs, gid, thisMap, rnArray);
			var roadArray = jQuery.parseJSON(data);
//			alert(data);
			//alert(rnArray.length);
			//alert(data);
			//alert(roadArray.length);
			showLightRailRoute(roadArray,rnArray);
		
		}
	});
}

function showLightRailRoute(roadArray, rnArray){
	
	var showOption = {strokeOpacity:1, clickable:true, strokeColor: "#FF00FF"};
	
	/*
	for (var i = 0; i < roadArray.length; i++){

		var id = roadArray[i];
		rnArray[id].setOptions(showOption);		
		
	}*/
	
	for (var i = 0; i < roadArray.length; i++){
		
		//var gid = roadArray[i] + 1;
		var gid = roadArray[i];
		for (var j = 0; j < rnArray.length; j++){
			
			if (rnArray[j].name == gid){
				rnArray[j].setOptions(showOption);
			}
			
		}
		
	}	
}

function loadLightRailNetwork(coordsPGs, thisMap, ltArray){
	
	for (var i = 0; i < coordsPGs.length; i++){
		
		var gid = coordsPGs[i][0];        
		var coordsPG = coordsPGs[i][1];
		createLightTrainNetworkPolyLine(coordsPG, gid, thisMap,ltArray);
	}
}

function createLightTrainNetworkPolyLine(lightTrainNetworkCoords, lightTrainNetworkCode, thisMap, ltArray) 
{
	var initOption = {
			name: lightTrainNetworkCode,
			path: lightTrainNetworkCoords,
			strokeColor: "#FF00FF",
			strokeOpacity: initialOpacityLightTrain,
			strokeWeight: 2,
			clickable:false
	};
	
	var lightTrainNetwork = new google.maps.Polyline(initOption);
	lightTrainNetwork.setMap(thisMap);
	ltArray.push(lightTrainNetwork);	
	
	google.maps.event.addListener(lightTrainNetwork, 'click', function(event) {
		getLightTrainRouteInfo(lightTrainNetworkCode, event, thisMap);
		
		var changeOption={
				strokeColor: "lime"
		};
		lightTrainNetwork.setOptions(changeOption);

	});
}

function getLightTrainRouteInfo(lightTrainNetworkCode, event, thisMap){
	
//	var scenarioName = $('#starting_definition :selected').text();
	
	$.ajax({
		type:"POST",
		url: "servlet/LightRailInfoServlet",
		
		data: "id=" + lightTrainNetworkCode,
		success: function (data){
			//alert(msg);	
//			var lightTrainRouteObj = jQuery.parseJSON(data);
//			showLightTrainRouteInfo(lightTrainRouteObj, event, thisMap);
//			alert(data);
			clearOverlays();
			var coordinates=(new String(data)).split(",");
			if($("#edit_light_rail").is(':checked')){
				for(var i=0; i<coordinates.length; i++){
					var x=coordinates[i].split(" ")[0];
					var y=coordinates[i].split(" ")[1];
					var latLng=new google.maps.LatLng(y,x);
					placeMarker(latLng,"Node "+(i+1));
				}
			}
		}
	});
}

function showLightTrainRouteInfo(lightTrainRouteObj, event, thisMap){
	
	
//	var contentString = "Route ID: " + busRouteObj.busRouteId + "<br/>Direction: " + busRouteObj.direction + "<br />Speed Limit: " + busRouteObj.speedLim;
	
	coords = new google.maps.LatLng(event.latLng.lat(), event.latLng.lng());
	
	infoWindow.setContent(contentString);
	infoWindow.setPosition(coords);
	infoWindow.open(thisMap);
	
	setTimeout(function(){
		infoWindow.close();
	},5000);

}


function finishRailLink()
{
	clearOverlays();
//	createNewPolyLine();
//	alert($("p#path").text());
	
//	$( "#the_geom" ).val($("p#path").text());
//	$( "#dialog-form" ).dialog( "open" );
	
	
	
	coordinatesArray.length=0;
	if(directionsDisplay!=null){
		directionsDisplay.setMap(null);
		directionsDisplay.setPanel(null);
	}
	

	var coordinates=$("#path").text();
	if(!$("#path").is(':empty')){
		$( "#dialog-confirm" ).dialog( "option" , "buttons" , { "Ok": function() {
			makeRequestToUpdateLightRailRouteNetworkLayer(coordinates);
			    $(this).dialog("close"); 
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		} );

		$("#path").text("");
		$( "#dialog-confirm" ).dialog("open");
		
	}
	

	if(newPoly!=null){
		deletePolyLine(newPoly);
	}
	
	
}

function makeRequestToUpdateLightRailRouteNetworkLayer(coordinates){

	
	$.ajax({
		url: 'servlet/LightRailRouteServlet',
		type: 'POST',
		dataType: "text",
		data: "&coordinates=" + coordinates,
		success: function(data){
			$("#current_status").html("The current number of link in data base is: "+data);
			
			$( "#dialog-message" ).dialog("open");
			
			
		}
	});
}