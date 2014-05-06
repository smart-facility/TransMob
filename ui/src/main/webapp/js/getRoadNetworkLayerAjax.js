var initialOpacityRn = 0;

function getNewLink()
{
//	if($("#new_link").is(':checked')){
//		
//		var cor=$("p#path").text().split(",");
//		var latLng=new google.maps.LatLng(cor[0],cor[1]);
//		coordinatesArray.push(latLng);
//		//alert("checked");
//		google.maps.event.addListener(map, 'rightclick', function(e){
//		//alert(e.latLng);
//		placeMarker(e.latLng);
//		coordinatesArray.push(e.latLng);
//		$("p#path").append("<br>-->"+e.latLng.lat()+","+e.latLng.lng());
//	});
//	}else{
		clearPath();
		google.maps.event.clearListeners(mapEdit, 'rightclick');
//	}
}



function finishLink()
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
	

	var coords=$("#path").text();
	if(!$("#path").is(':empty')){
		$( "#dialog-confirm" ).dialog( "option" , "buttons" , { "Ok": function() {
			makeRequestToUpdateRoadNetworkLayer(coords);
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
function makeRequestToUpdateRoadNetworkLayer(coordinates){

	
	$.ajax({
		url: 'servlet/RoadNetworkLayerServlet',
		type: 'POST',
		dataType: "text",
		data: "&coordinates=" + coordinates,
		success: function(data){
			$("#current_status").html("The current number of link in data base is: "+data);
			
			$( "#dialog-message" ).dialog("open");
			
			
		}
	});
}

//function makeRequestToUpdateRoadNetworkLayer(street,coordinates){
//	
//	$.ajax({
//		url: 'servlet/RoadNetworkLayerServlet',
//		type: 'POST',
//		data: "street="+street+"&coordinates=" + coordinates,
//		success: function(msg){
//			alert(""+street+" Saved: "+msg);
//		}
//	});
//
//}

function makeRequestToGetRoadNetworkLayer(){
	
	$.ajax({
	  url: 'servlet/RoadNetworkLayerServlet',
	  dataType: "text",
	  success: function(data) {
		var roadCoordsPGs = preprocessCoords(data);
		
		
		//loadRouteArray(roadCoordsPGs, map, busNetworkArray, "#FF0000" );
		//loadRouteArray(roadCoordsPGs, mapCompare, busNetworkArrayUsa, "#FF0000" );
		
		
		//Load individual bus routes
		loadRouteArray(roadCoordsPGs, map, allRoutesArray, "#FF0000" );
		loadRouteArray(roadCoordsPGs, mapCompare, allRoutesArrayOut, "#FF0000" );
		loadRouteArray(roadCoordsPGs, mapEdit, allRoutesArrayEdit, "#FF0000");
		
		loadRoadNetwork(roadCoordsPGs, map, roadNetworkArray, "#330066");
		loadRoadNetwork(roadCoordsPGs, mapEdit, roadNetworkArrayEdit, "#330066");
		loadRoadNetwork(roadCoordsPGs, mapCompare, roadNetworkArrayOut, "#330066");
		loadRoadNetwork(roadCoordsPGs, mapCompare, roadNetworkArrayCon, "#330066");	
	  }
	});
}

function loadRouteArray(roadCoordsPGs, thisMap, rnArray, colorCode){
	
	var routeArray;
	$.ajax({
		url: 'servlet/GetAllBusRouteIdServlet',
		dataType: "text",
		success: function(data){
			routeArray = jQuery.parseJSON(data);
			
			//alert(data);
			
			for (var i = 0; i < routeArray.length; i++){
	
				for (var j = 0; j < roadCoordsPGs.length; j++){
					
					var routeId = routeArray[i] + 1;
					
					if(routeId == roadCoordsPGs[j][0]){
						
						var code = roadCoordsPGs[j][0];
						
						//alert(code);
						
						var roadCoords = roadCoordsPGs[j][1];
						createRoadNetworkPolyLine(roadCoords, code, thisMap, rnArray, colorCode);
					}else{
						
					}
				}
			}	
		}		
	});
}

function preprocessCoords(coordsData){
	
	var coordsArray = coordsData.split(";");
	var coordsPGs = [];
	
	for (var i = 0; i < coordsArray.length; i++){
	//for (var i = 0; i < 1; i++){
		
		var coordsPG = [];		
		var gidCoordsLine = coordsArray[i].split("@");
		var gid = gidCoordsLine[0];
		var coordsLine = gidCoordsLine[1];
		var coordSet = coordsLine.split(",");
		
		for(var j = 0; j < coordSet.length; j++){
		//for (var j = 0; j < 2; j++){
			
			var coords = coordSet[j].split(" ");
			var lng = coords[0];
			var lat = coords[1];
			
			//alert(lng);alert(lat);
			var gLatLng = new google.maps.LatLng(lat, lng);			
			coordsPG.push(gLatLng);
		}
		
		coordsPGs[i]=new Array(2);
		coordsPGs[i][0]=gid;
		coordsPGs[i][1]=coordsPG;
	}
	
	return coordsPGs;
}

function loadRoadNetwork(roadCoordsPGs, thisMap, rnArray, colorCode){
	
	for (var i = 0; i < roadCoordsPGs.length; i++){
		
		var gid = roadCoordsPGs[i][0];
//		i++;
		var roadCoordsPG = roadCoordsPGs[i][1];
		createRoadNetworkPolyLine(roadCoordsPG, gid, thisMap, rnArray, colorCode);
	}
}

function createRoadNetworkPolyLine(roadCoords, code, thisMap, rnArray, colorCode)
{
	//alert("createStreetNetworkPolyLine");
	var initOption = {
			name: code,
			path: roadCoords,
			strokeColor: colorCode,
			strokeOpacity: initialOpacityRn,
			strokeWeight: 2,
			clickable:false
	};
	
	var roadNetwork = new google.maps.Polyline(initOption);
	roadNetwork.setMap(thisMap);
	rnArray.push(roadNetwork);	
	
	google.maps.event.addListener(roadNetwork, 'click', function(event) {
		
		getRoadInfo(code, event, thisMap);
		if($("#edit_road_network").is(':checked')){
		var changeOption={
				strokeColor: "lime"
		};
		roadNetwork.setOptions(changeOption);
		}
		if($("#edit_bus_network_demo").is(':checked')){
			var changeOption={
					strokeColor: "#FF0000",
					strokeOpacity: 1
			};
			
			$("#link_id").append(" "+code+";");
			
			
			roadNetwork.setOptions(changeOption);
		}
		//getBusRouteInfo(code, event);
	});
	
	google.maps.event.addListener(roadNetwork, 'rightclick', function(event){
		
		var changeOption={
				strokeColor: "#FF0000",
				strokeOpacity: 1
		};
		
		$("#link_id").append(" "+code+";");
		
		
		roadNetwork.setOptions(changeOption);
	});

}



function getRoadInfo(roadId, event, thisMap){
	
	$.ajax({
		url: 'servlet/RoadNetworkInfoServlet',
		type: 'POST',
		
		data: "id=" + roadId,
		success: function (data){
			var roadBeanObj = jQuery.parseJSON(data);
			showRoadInfo(roadBeanObj, event, thisMap);
		}
	});
	
}

function showRoadInfo(roadBeanObj, event, thisMap){

	var contentString = "Name: " + roadBeanObj.roadName + "<br/>Road ID: " + roadBeanObj.roadId 
	+ "<br />Road Type: " + roadBeanObj.roadType+"<br />Speed: "+roadBeanObj.speed;
	coords = new google.maps.LatLng(event.latLng.lat(), event.latLng.lng());
	
	infoWindow.setContent(contentString);
	infoWindow.setPosition(coords);
	infoWindow.open(thisMap);
	
	setTimeout(function(){
		infoWindow.close();
	},5000);
	var i=0;
	clearOverlays();
	if($("#edit_road_network").is(':checked')){
		while(true){
			
			
			if(roadBeanObj.points[i]==null){
				break;
			}
			var latLng=new google.maps.LatLng(roadBeanObj.points[i][1],roadBeanObj.points[i][0]);
			placeMarker(latLng,"Node "+(i+1));
			i++;
		}
	}	
}


/*
function makeRequestToGetRoadNetworkLayer(){
	
	$.ajax({
	  url: 'servlet/RoadNetworkLayerServlet',
	  success: function(data) {
	   //alert(data);
		
		var coordsArray = coordsData.split(";");
		//alert(streetNetworkCoordsArray.length);
		//alert(streetNetworkCoordsArray);
		loadRoadNetwork(data, map, roadNetworkArray);
		loadRoadNetwork(data, mapCompare, roadNetworkArrayOutput);
		loadRoadNetwork(data, mapCompare, roadNetworkArrayCon);
		
	  }
	});
}

function loadRoadNetwork(coordsArray, thisMap, rnArray){
	
	for (var i = 0; i < coordsArray.length; i++){
	//for (var i = 0; i < 1; i++){
		
		var roadCoordsPG = [];		
		var gidCoordsLine = coordsArray[i].split("@");
		var gid = gidCoordsLine[0];
		var coordsLine = gidCoordsLine[1];
		
		var roadCoords = coordsLine.split(",");
		
		//alert(streetCoords);
		
		for(var j = 0; j < roadCoords.length; j++){
		//for (var j = 0; j < 2; j++){
			
			var coords = roadCoords[j].split(" ");
			var lng = coords[0];
			var lat = coords[1];
			
			//alert(lng);
			//alert(lat);
			var gLatLng = new google.maps.LatLng(lat, lng);
			
			//alert(coords[0] + " " + coords[1]);
			
			roadCoordsPG.push(gLatLng);
		}
		//alert(gid);
		// operation as subareaHere
		createRoadNetworkPolyLine(roadCoordsPG, gid, thisMap, rnArray);
	}
}*/
