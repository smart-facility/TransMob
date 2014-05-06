function makeRequestToGetBusStopsLayer(){
	
	$.ajax({
		url:"servlet/BusStopsLayerServlet",
		async:false,
		
		success: function(msg){
			//alert(msg);
			var busStopsCoordsArray = msg.split(";"); 
			
			loadAllPoints(busStopsCoordsArray);
		}
	});	
}

function loadAllPoints(coordsArray){
	
	for(var i = 0; i < coordsArray.length; i++){
		
		var gidCoords = coordsArray[i].split("@");
		var gid = gidCoords[0];
		var coords = gidCoords[1];
		
		var coordsSplit = coords.split(" ");
		
		var lng = coordsSplit[0];
		var lat = coordsSplit[1];
		
		var gLatLng = new google.maps.LatLng(lat, lng);
		
		var marker = new google.maps.Marker({
			position: gLatLng,
			map: map,
			title:gid,
			visible:false
		});	
		
		busStopsArray.push(marker);
	}	
}

function hideBusStopsLayer(){
	
	for (var i = 0; i < busStopsArray; i++){
		busStopsArray[i].setMap(null);
	}
}

function showBusStopsLayer(){
	
	for (var i = 0; i < busStopsArray; i++){
		busStopsArray[i].setOptions({visible:true});
	}
	
	
}