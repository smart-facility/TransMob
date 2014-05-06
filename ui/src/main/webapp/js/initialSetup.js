/**
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>qun@uow.edu.au
 */
var selectTab = "";
	var map;
	var contextMenu;
	var clickedLatLng;
	var infoWindow;
	var subareaArray = [];
	var subareaArrayOut = [];
	
	var busNetworkArray = [];
	var busNetworkArrayEdit = [];
	var busNetworkArrayOut = [];
	
	var busNetworkArrayUsa = [];
	
	var roadNetworkArray = [];
	var roadNetworkArrayEdit = [];
	var roadNetworkArrayOut = [];
	var roadNetworkArrayCon = [];
	var busStopsArray = [];
	var sparkMarker = null;
	
	var allRoutesArray = [];
	var allRoutesArrayEdit = [];
	var allRoutesArrayOut = [];
	
	var lightRailRouteArray=[];
	var lightRailRouteArrayEdit=[];

	$(document).ready(function(){

		
		
		createDialogMessage();
		createDialogConfirm();
		
		/*
		 * test
		 */
////		$("#new_link").click(function(){
//			getNewLink();
////		});
		
		$("#finish_link").click(function(){
			finishLink();
			$("p#path").html("");
		});
		
		$("#finish_link_light_rail").click(function(){
			finishRailLink();
			$("p#path").html("");
		});
		
		$("#finish_bus").click(function(){
			finishBus();
		});

		$("p#path").addClass("small-font");
		
//		displayLink();
//		
//		 var contextmenu = document.createElement("div");
//	      contextmenu.style.visibility="hidden";
//	      contextmenu.style.background="#ffffff";
//	      contextmenu.style.border="1px solid #8888FF";
		
		
		$("#sam_tabs").tabs();
		initializeGoogleMaps();
		setRunModelButton();
		setPauseModelButton();
		setStopModelButton();
		setReplayModelButton();
		
		setRunModelCompareButton();
		setPauseModelCompareButton();
		setStopModelCompareButton();
		setReplayModelCompareButton();
		setEndModelCompareButton();
//		setViewBusRoutesButton("#disp_bus_routes_selection", "#view_bus_routes", '#select_bus_routes :selected', allRoutesArray);
//		setViewBusRoutesButton('#disp_bus_routes_selection_out', "#view_bus_routes_out", '#select_bus_routes_out :selected', allRoutesArrayOut);
		setViewBusRoutesButton("#disp_bus_routes_selection", "#view_bus_routes", '#select_bus_routes :selected', roadNetworkArray);
		setViewBusRoutesButton('#disp_bus_routes_selection_out', "#view_bus_routes_out", '#select_bus_routes_out :selected', roadNetworkArrayOut);
		//setViewBusRoutesOutButton();
		
		setViewStatsButton();
		setDefButton();
		setScenButton();
		getAllScenarioNames();
		getAllBusRouteNames();
		
		$('#dips_heat_map').hide();
		$('#dips_heat_map_out').hide();
		
		$("#run_progress_bar").progressbar({value: 0});
		$("#compare_progress_bar").progressbar({value: 0});
		$('#right2left').click(function(){
			return !$('#left_change option:selected').remove().appendTo("#right_change");
		});

		$('#left2right').click(function(){
			var value = $('#right_change option:selected').val();
			$('#left_change').append($("<option></option>").attr("value",value).text(value));
			$('#right_change option:selected').remove();
		});
		
		
		$('a[href^="http://"]')
		  .attr({
		    target: "_blank", 
		    title: "Opens in a new window"
		  })
		  .append(' [^]');
				
	});	
	
	$(function() {
		var pdate = $( "#datepicker" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true,
			buttonText:"Select Date",
			onSelect:function(dateText){
				var curValue = $('#right_change option:selected').val();
				$('#right_change option:selected').attr("value", curValue).text(curValue + " : " + dateText);
			}
		});
	});