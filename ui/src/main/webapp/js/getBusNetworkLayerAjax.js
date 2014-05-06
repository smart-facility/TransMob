var initialOpacityBn = 0;

function showBusRoute(roadArray, rnArray){
	
	var showOption = {strokeOpacity:1, clickable:true,strokeColor: "#FF0000"};
	
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


function getSingleBusRoute(routeName, rnArray){
	
	$.ajax({
		url: 'servlet/GetSingleBusRouteServlet',
		type: 'POST',
		
		data: "id=" + routeName,
		success: function(data){
//			var singleBusCoordsPGs = preprocessCoords(data);
//			createBusNetworkPolyLine(singleBusCoordsPGs, gid, thisMap, rnArray);
			var roadArray = jQuery.parseJSON(data);
			
			//alert(rnArray.length);
			//alert(data);
			//alert(roadArray.length);
			showBusRoute(roadArray, rnArray);
		
		}
	});
}
function finishBus(){

	var roadIDs=$("#link_id").text().split(":")[1];
	
	
	$( "#dialog-confirm" ).dialog( "option" , "buttons" , { "Ok": function() {
		makeRequestToUpdateBusNetworkLayer(roadIDs);
		$(this).dialog("close"); 
		},
		Cancel: function() {
			$( this ).dialog( "close" );
		}
	} );

	$("#link_id").text("New Bus Route:");
	$( "#dialog-confirm" ).dialog("open");
}


function makeRequestToGetBusNetworkLayer(){
	
	$.ajax({
	  url: 'servlet/BusNetworkLayerServlet',
	  type: 'GET',
	  dataType: "text",
	  success: function(data) {
		var busCoordsPGs = preprocessCoords(data);
		
		loadBusNetwork(busCoordsPGs, map, busNetworkArray);
		loadBusNetwork(busCoordsPGs, mapEdit, busNetworkArrayEdit);
		loadBusNetwork(busCoordsPGs, mapCompare, busNetworkArrayOut);		
	  }
	});
}

//function makeRequestToUpdateBusNetworkLayer(coordinates){
//
//	
//	$.ajax({
//		url: 'servlet/BusNetworkLayerServlet',
//		type: 'POST',
//		
//		data: "&coordinates=" + coordinates,
//		success: function(data){
//			$("#current_status").html("The current number of link in data base is: "+data);
//			
//			$( "#dialog-message" ).dialog("open");
//			
//			
//		}
//	});
//}

function makeRequestToUpdateBusNetworkLayer(roadIDs){
	$.ajax({
		url: 'servlet/BusNetworkLayerServlet',
		type: 'POST',
		
		data: "&roadIDs=" + roadIDs,
		success: function(data){
			$("#current_status").html("The current number of link in data base is: "+data);
			
			$( "#dialog-message" ).dialog("open");		
		}
	});
}

function loadBusNetwork(coordsPGs, thisMap, rnArray){
	
	for (var i = 0; i < coordsPGs.length; i++){
		
		var gid = coordsPGs[i][0];
		var coordsPG = coordsPGs[i][1];
		createBusNetworkPolyLine(coordsPG, gid, thisMap, rnArray);
	}
}

function createBusNetworkPolyLine(busNetworkCoords, busNetworkCode, thisMap, bnArray)
{
	var initOption = {
			name: busNetworkCode,
			path: busNetworkCoords,
			strokeColor: "#FF0000",
			strokeOpacity: initialOpacityBn,
			strokeWeight: 2,
			clickable:false
	};
	
	var busNetwork = new google.maps.Polyline(initOption);
	busNetwork.setMap(thisMap);
	bnArray.push(busNetwork);	
	
	google.maps.event.addListener(busNetwork, 'click', function(event) {
		getBusRouteInfo(busNetworkCode, event, thisMap);
		
		var changeOption={
				strokeColor: "lime"
		};
		busNetwork.setOptions(changeOption);
		
//		$("p#path").html(""+busRouteObj.firstPoint[0]+","+busRouteObj.firstPoint[1])
		
//		$("#choose_point").change(function () {
//			var str = "";
//	        $("#choose_point option:selected").each(function () {
//	              str += $(this).text();
//	            });
//	        if(str=="First Point")
//	        {
//	        	$("p#path").html(""+busRouteObj.firstPoint[0]+","+busRouteObj.firstPoint[1])
//	        }
//	        if(str=="Last Point")
//	        {
//	        	$("p#path").html(""+busRouteObj.lastPoint[0]+","+busRouteObj.lastPoint[1])
//	        }
//		}).change();
	});
}

function getBusRouteInfo(busRouteCode, event, thisMap){
	
//	var scenarioName = $('#starting_definition :selected').text();
	
	$.ajax({
		type:"GET",
		url: "servlet/BusRouteInfoServlet",
		
		data: "id=" + busRouteCode,
		success: function (msg){
			//alert(msg);	
			var busRouteObj = jQuery.parseJSON(msg);
			showBusRouteInfo(busRouteObj, event, thisMap);
		}
	});
}

function showBusRouteInfo(busRouteObj, event, thisMap){
	
	
	var contentString = "Route ID: " + busRouteObj.busRouteId + "<br/>Direction: " + busRouteObj.direction + "<br />Speed Limit: " + busRouteObj.speedLim;
	//contentString += "Speed Limit: " + busRouteObj.speedLim;
	//contentString += "ClickedLocation: <br />" + event.latLng.lat() + "," + event.latLng.lng() + "<br />";
	//var scenarioName = $('#select_scenario_compare :selected').text();
	//var contentString = "Scenario name: " + scenarioName + "<br />Run ID: " + busRouteObj[16] + "<br /> Line ID: " + busRouteObj[0] + "<br />Speed Limit: " + busRouteObj[1] + "<br />";
	
	//contentString += "Scenario name: " + scenarioName + "<br />";
	//contentString += "Run ID: " + busRouteObj[16] + "<br />";
	//for (var i = 0; i < 14; i++){ contentString += "Tick " + (i + 1) + ": " + busRouteObj[2 + i] + "<br />";}
	
	coords = new google.maps.LatLng(event.latLng.lat(), event.latLng.lng());
	
	infoWindow.setContent(contentString);
	infoWindow.setPosition(coords);
	infoWindow.open(thisMap);
	
	setTimeout(function(){
		infoWindow.close();
	},5000);
//	alert(busRouteObj.firstPoint);
//	$("p#path").html(""+busRouteObj.firstPoint[0]+","+busRouteObj.firstPoint[1])
	
//	$("#choose_point").change(function () {
//			var str = "";
//	        $("#choose_point option:selected").each(function () {
//	              str += $(this).text();
//	            });
//	        if(str=="First Point")
//	        {
//	        	$("p#path").html(""+busRouteObj.firstPoint[1]+","+busRouteObj.firstPoint[0])
////	        	var latLng=new google.maps.LatLng(busRouteObj.firstPoint[0],busRouteObj.firstPoint[1]);
////	        	placeMarker(latLng);
//	        }
//	        if(str=="Last Point")
//	        {
//	        	$("p#path").html(""+busRouteObj.lastPoint[1]+","+busRouteObj.lastPoint[0])
////	        	var latLng=new google.maps.LatLng(busRouteObj.lastPoint[0],busRouteObj.lastPoint[1]);
////	        	placeMarker(latLng);
//	        }
//		}).change();

}

function makeRequestToGetBusUsageNetwork(thisMap){
	
	$.ajax({
	  url: 'servlet/BusNetworkUsageLayerServlet',
	  
	  success: function(data) {
	    //alert(data);
		
		var busNetworkCoordsArray = data.split(";");
		
		//alert(streetNetworkCoordsArray);
		loadBusUsageNetwork(busNetworkCoordsArray, thisMap);
		
	  }
	});
}

function loadBusUsageNetwork(coordsArray, thisMap){
	
	for (var i = 0; i < coordsArray.length; i++){
	//for (var i = 0; i < 1; i++){
		
		var subareaCoordsPG = [];		
		var gidCoordsLine = coordsArray[i].split("@");
		var gid = gidCoordsLine[0];
		var coordsLine = gidCoordsLine[1];
		
		//alert(gidCoordsLine[1]);
		
		var subareaCoords = coordsLine.split(",");
		
		for(var j = 0; j < subareaCoords.length; j++){
		//for (var j = 0; j < 2; j++){
			
			var coords = subareaCoords[j].split(" ");
			var lng = coords[0];
			var lat = coords[1];
			
			var gLatLng = new google.maps.LatLng(lat, lng);
			
			//alert(coords[0] + " " + coords[1]);
			
			subareaCoordsPG.push(gLatLng);
		}
		
		//alert(gid);
		// operation as subareaHere
		createBusNetworkUsagePolyLine(subareaCoordsPG, gid, thisMap);
	}
}

function createBusNetworkUsagePolyLine(busNetworkCoords, code, thisMap)
{
	var initOption = {
			name: code,
			path: busNetworkCoords,
			strokeColor: "#FF0000",
			strokeOpacity: initialOpacityBn,
			strokeWeight: 2,
			clickable:false
	};
	
	busNetwork = new google.maps.Polyline(initOption);
	busNetwork.setMap(thisMap);
	busNetworkUsageArray.push(busNetwork);	
	
	google.maps.event.addListener(busNetwork, 'click', function(event) {
		getBusRouteInfo(code, event, thisMap);
	});
}

