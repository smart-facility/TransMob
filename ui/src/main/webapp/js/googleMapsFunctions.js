var markersArray = [];
var coordinatesArray=[];
var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
var newPoly;

function initializeGoogleMaps()
{
	var latLng = new google.maps.LatLng(-33.95089329049571, 151.2434363365173);

	var myOptions = {
			zoom:12,
			center:latLng,
			mapTypeId: google.maps.MapTypeId.ROADMAP,
			streetViewControl: true,
			scaleControl: true
	};

	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	mapEdit= new google.maps.Map(document.getElementById("map_edit"), myOptions);
	mapRun = new google.maps.Map(document.getElementById("run_map"), myOptions);
	mapCompare = new google.maps.Map(document.getElementById("compare_map"), myOptions);


//	var clickedOption = {fillOpacity: 0.35};
//	var doubleClickedOption = {fillOpacity: 0};


	makeRequestToGetSubareaLayer();
	makeRequestToGetBusNetworkLayer();
	makeRequestToGetRoadNetworkLayer();
	makeRequestToGetLightRailRouteNetworkLayer();

	
	
	//makeRequestToGetBusStopsLayer();

	/*
	var myLatlng = new google.maps.LatLng(-33.95089329049571, 151.2434363365173);
	var myOptions = {
	  zoom: 11,
	  center: myLatlng,
	  mapTypeId: google.maps.MapTypeId.ROADMAP
	}

	var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

	var ctaLayer = new google.maps.KmlLayer('http://130.130.37.13/geoserver/gwc/service/kml/cite:randwick_travel_zones_v1.png.kml');
	ctaLayer.setMap(map);

	/*
	var mark1 = new google.maps.LatLng(-33.98834915267796,151.24122619628906);
	placeMarker(mark1);

	subarea2 = new google.maps.Polygon(initOption);
	subarea2.setOptions({paths: subarea2Coords});
	subarea2.setMap(map);
	var mark2 = new google.maps.LatLng(-33.94862890003363,151.24191284179688);
	placeMarker(mark2);

	subarea3 = new google.maps.Polygon(initOption);
	subarea3.setOptions({paths: subarea3Coords});
	subarea3.setMap(map);
	var mark3 = new google.maps.LatLng(-33.91273660179618,151.24414443969727);
	placeMarker(mark3);*/

	/*
	var lineCoords = [
		new google.maps.LatLng(151.236970599156,-33.8906735654826),
		new google.maps.LatLng(151.24010700427,-33.9029859610342)
	];

	var polyOptions = {
		path: lineCoords,                                                                                                                                  
		strokeColor: "#000000",
		strokeOpacity: 1.0,
		strokeWeight: 3
	};

	poly = new google.maps.Polyline(polyOptions);
	poly.setMap(map);*/

	//google.maps.event.addListener(map, 'click', addLatLng);		

	infoWindow = new google.maps.InfoWindow();

//	google.maps.event.addListener(map, 'click', function(event) {
//	placeMarker(event.latLng);

//	});

	//getContextMenu();
//	getNewLink();
	/*
	 * real-time traffic information
	 */
//	var trafficLayer = new google.maps.TrafficLayer();
//	trafficLayer.setMap(map);
}


function getSparklineAjax(){

	var sparkImg=null;

	$.ajax({
		url:"servlet/SparklineServlet",
		async:false,
		
		success:function(msg){
			sparkImg = msg;

			//alert(msg);
		}
	});

	return sparkImg;
}

function setSparklineMarker(location) {
	// Add markers to the map

	// Marker sizes are expressed as a Size of X,Y
	// where the origin of the image (0,0) is located
	// in the top left of the image.

	// Origins, anchor positions and coordinates of the marker
	// increase in the X direction to the right and in
	// the Y direction down.

	var sparkImg = getSparklineAjax();
	//alert(sparkImg);


	var image = new google.maps.MarkerImage(sparkImg,
			// This marker is 20 pixels wide by 32 pixels tall.
			new google.maps.Size(100, 50),
			// The origin for this image is 0,0.
			new google.maps.Point(0,0),
			// The anchor for this image is the base of the flagpole at 0,32.
			new google.maps.Point(0, 32));

	// Shapes define the clickable region of the icon.
	// The type defines an HTML <area> element 'poly' which
	// traces out a polygon as a series of X,Y points. The final
	// coordinate closes the poly by connecting to the first
	// coordinate.
	var shape = {
			coord: [1, 1, 1, 20, 18, 20, 18 , 1],
			type: 'poly'
	};

	//var myLatLng = new google.maps.LatLng(location);
	sparkMarker = new google.maps.Marker({
		position: location,
		map: map,
		icon: image,
		shape: shape
		//title: location
		//zIndex: location
	});
}


function placeMarker(location, title)
{
	directionsDisplay = new google.maps.DirectionsRenderer();
	directionsDisplay.setMap(mapEdit);
	directionsDisplay.setPanel(document.getElementById("directions_panel"));

//	var distanceToPreviousIndex=3;
	var marker = new google.maps.Marker({
		position: location,
		draggable:true,
		animation: google.maps.Animation.DROP,
		map: mapEdit, 
		title: title
	});
	markersArray.push(marker);
	google.maps.event.addListener(marker, 'dblclick', function(){
		toggleBounce(marker);
//		if($("#new_link").is(':checked')){
			
			$("#path").html(""+marker.getPosition());
			coordinatesArray.push(marker.getPosition());
			createNewPolyLine();
			
//		}
		infowindow.open(mapEdit,marker);
		setTimeout(function(){
			infowindow.close();
		},5000);
	});

	google.maps.event.addListener(marker, 'dragend',function(){
//		if($("#new_link").is(':checked')){
		$("#path").append("<br>-->"+marker.getPosition());
		coordinatesArray.push(marker.getPosition());
		var path = newPoly.getPath();
		path.push(marker.getPosition());
//		calcRoute(directionsService, path.getAt(path.length-distanceToPreviousIndex),marker.getPosition());
//		}
	});

	google.maps.event.addListener(marker, 'rightclick',function(){
		marker.setPosition(location);
//		newPoly.getPath().clear();

	});

	var contentString ='<div style="font-size: 8pt;">'+title+'<br>'+marker.getPosition()+'</div>';
	var infowindow = new google.maps.InfoWindow({
		content: contentString,
		maxWidth: 1000
	});
}

function toggleBounce(marker) {

	if (marker.getAnimation() != null) {
		marker.setAnimation(null);
	} else {
		marker.setAnimation(google.maps.Animation.BOUNCE);
	}
}

function getContextMenu()
{
	contextMenu = $('#contextMenu');

	// Disable the browser context menu on our context menu
	contextMenu.bind('contextmenu', function() { return false; });

	// Append it to the map object
	$(map.getDiv()).append(contextMenu);

	google.maps.event.addListener(map, 'rightclick', function(e)
			{
		// start buy hiding the context menu if its open
		contextMenu.hide();

		var mapDiv = $(map.getDiv());
		x = e.pixel.x;
		y = e.pixel.y;

		// save the clicked location
		clickedLatLng = e.latLng;

		// adjust if clicked to close to the edge of the map
		if ( x > mapDiv.width() - contextMenu.width() )
			x -= contextMenu.width();

		if ( y > mapDiv.height() - contextMenu.height() )
			y -= contextMenu.height();

		// Set the location and fade in the context menu
		contextMenu.css({ top: y, left: x }).fadeIn(100);
			});
	// Set some events on the context menu links
	contextMenu.find('a').click( function()
			{
		// fade out the menu
		contextMenu.fadeOut(75);

		var action = $(this).attr('href').substr(1);

		switch ( action )
		{
		case 'Add node here':
			placeMarker(clickedLatLng);
			break;

		case 'zoomOut':
			map.setZoom(
					map.getZoom() - 1
			);
			map.panTo(clickedLatLng);
			break;

		case 'centerHere':
			map.panTo(clickedLatLng);
			break;
		}

		return false;
			});

	// Hover events for effect
	contextMenu.find('a').hover( function() {
		$(this).parent().addClass('hover');
	}, function() {
		$(this).parent().removeClass('hover');
	});

	// Hide context menu on some events
	$.each('click dragstart zoom_changed maptypeid_changed'.split(' '), function(i,name){
		google.maps.event.addListener(map, name, function(){ contextMenu.hide() });
	});
}

function clearOverlays() {
	if (markersArray) {
		for (i in markersArray) {
			markersArray[i].setMap(null);
		}
	}
}
//Shows any overlays currently in the array
function showOverlays() {
  if (markersArray) {
    for (i in markersArray) {
      markersArray[i].setMap(map);
    }
  }
}

// Deletes all markers in the array by removing references to them
function deleteOverlays() {
  if (markersArray) {
    for (i in markersArray) {
      markersArray[i].setMap(null);
    }
    markersArray.length = 0;
  }
}
function createNewPolyLine()
{
//	if(newLine!=null)
//	{
//	newLine.getPath().clear();
//	}

	var initOption = {
			path: coordinatesArray,
			strokeColor: "lime",
			strokeWeight: 2,
			clickable:false
	};
	newPoly = new google.maps.Polyline(initOption);
	newPoly.setMap(mapEdit);

}

function deletePolyLine(newPoly)
{
	if(newPoly!=null){
		newPoly.setMap(null);
	}
}

function calcRoute(directionsService,start,end) {
	
	  var request = {
	    origin:start,
	    destination:end,
	    travelMode: google.maps.TravelMode.DRIVING
	  };
	  directionsService.route(request, function(result, status) {
	    if (status == google.maps.DirectionsStatus.OK) {
	      directionsDisplay.setDirections(result);
	      //alert("Distance:"+result.routes[0].legs[0].distance.value+" m");
	    }
	  });
	}
