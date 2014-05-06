var buttonSize = 170;
var buttonStore = 120;
var buttonSmall = 90;
var buttonMedium = 100;
var densityOffset = 0.1;

function clearPath()
{
//	if($("#new_link").is(':checked')){
//		
//	}
//	else{
//		alert("unclick");
		coordinatesArray.length=0;
		$("#path").html("");
//	}
		
}

function cbDispSubareaFeatures(checkBoxId, divId, legendId, sbArray, radioBoxName, contentBoxId){
	var isChecked = $(checkBoxId).is(':checked');
	//alert(isChecked);
	if(isChecked){
//		$(divId).show();
		$(checkBoxId).animate({
			opacity: 0.25
		},500,function(){
			$(divId).show(1000);
		});
		setPolyLines(sbArray, true, 0);
	} else {
		$(divId).hide();
		setPolyLines(sbArray, false, 0);
		hideDensityLayer(legendId, sbArray);
		
		$('input[name='+ radioBoxName +']:radio').attr("checked", false);
		$(contentBoxId).attr("checked", false);
	}	
}

function cbDispSubareaBoundaries(checkBoxId, sbArray){
	var isChecked = $(checkBoxId).is(':checked');
	//alert(isChecked);
	if(isChecked){
		$(checkBoxId).animate({
			opacity: 0.25
		},500);
		setSubareaBoundaries(sbArray, 0.8);
	} else {
		setSubareaBoundaries(sbArray, 0);
	}	
}

function rbHideHeatMap(legendId, sbArray){
	hideDensityLayer(legendId, sbArray);
}

function rbDispPopDensity(legendId, sbArray){
	dispPopDensity(legendId, sbArray);
}

function rbDispHouDensity(legendId, sbArray){
	dispHouDensity(legendId, sbArray);
}

function rbDispSatisfaction(legendId, sbArray){
	dispSatisfaction(legendId, sbArray);
}

function rbDispOrgSatisfaction(legendId, sbArray){
	dispOrgSatisfaction(legendId, sbArray);
}



function cbDispBusNetworkUsage(){
	var isChecked = $('#disp_bus_network_usage').is(':checked');
	//alert(isChecked);
	if(isChecked){
		
		//alert("isChecked");
		//var scenarioName = $('#select_scenario_run :selected').text();

		//alert(scenarioName);
		//dispTraDensityAjax(scenarioName);
		$('#disp_bus_network_usage').animate({
			opacity: 0.25,
		},500);
		
		setPolyLines(busNetworkArrayUsa, false, 1);
		
	} else {
		setPolyLines(busNetworkArrayUsa, false, 0);
	}	
}


function cbDispRoadNetworkUsage(){
	var isChecked = $('#disp_road_congestion').is(':checked');
	//alert(isChecked);
	if(isChecked){
		
		//alert("isChecked");
		//var scenarioName = $('#select_scenario_run :selected').text();

		//alert(scenarioName);
		//dispTraDensityAjax(scenarioName);
		$('#disp_road_congestion').animate({
			opacity: 0.25,
		},500);
		dispRoadDensity();
		
	} else {
		setPolyLines(roadNetworkArrayCon, false, 0);
	}	
}




function cbDispRoadNetwork(checkBoxId, sbArray){
	var isChecked = $(checkBoxId).is(':checked');
	//alert(isChecked);
	if(isChecked){
		$(checkBoxId).animate({
			opacity: 0.25,
		},500);
		setPolyLines(sbArray, true, 1);
		if(checkBoxId.search("edit")!=-1){
			$("p#road_help").show("bounce","fast");
			$("#set_panel").show("scale","fast");
		}
		
	} else {
		setPolyLines(sbArray, false, 0);
		if(checkBoxId.search("edit")!=-1){
			$("p#road_help").hide("bounce","fast");
			$("#set_panel").hide("scale","fast");
		}
		finishLink();
		$("p#path").html("");
	}	
}

function cbDispLightRail(checkBoxId, ltArray) {
	var isChecked = $(checkBoxId).is(':checked');
	
	if(isChecked){
		$(checkBoxId).animate({
			opacity: 0.25,
		},500);
		setPolyLines(ltArray, true, 1);
		if(checkBoxId.search("edit")!=-1){
			$("p#rail_help").show("bounce","fast");
			$("#set_panel_light_rail").show("scale","fast");
		}
	}else{
		setPolyLines(ltArray, false, 0);
		if(checkBoxId.search("edit")!=-1){
			$("p#rail_help").hide("bounce","fast");
			$("#set_panel_light_rail").hide("scale","fast");
		}
		finishRailLink();
		$("p#path").html("");
	}
}

function cbDispBusNetworkDemo(checkBoxId, bnArray,sbArray){
	
	var isChecked = $(checkBoxId).is(':checked');
	//alert(isChecked);
	if(isChecked){
		$(checkBoxId).animate({
			opacity: 0.25,
		},500);
		//alert(scenarioName);
		//dispTraDensityAjaxDemo();
		
		setPolyLines(bnArray, true, 1);
		
		if(checkBoxId.search("edit")!=-1){
		setPolyLines(sbArray, true, 0.25);
		$("#bus_panel").show("bounce","fast");
		}
	} else {
		setPolyLines(bnArray, false, 0);
		
		clearLegend();
		if(checkBoxId.search("edit")!=-1){
		setPolyLines(sbArray, false, 0);
		$("#bus_panel").hide( 'puff',"fast");
		}
	}	
}

function cbDispBusRoutes(selectBoxId, divId, rnArray){
	var isChecked = $(selectBoxId).is(':checked');
	
	if (isChecked){
		$(selectBoxId).animate({
			opacity: 0.25,
		},500);
		$(divId).show("bounce","fast");
		//$('#disp_bus_routes_selection').css('width', 300);			
		
	}
	else {
		$(divId).hide( 'puff',"fast");
		setPolyLines(rnArray, false, 0);
	}	
}

function setViewBusRoutesButton(divId, buttonId, selectBoxId, rnArray){
	$(divId).hide();
	
	$(buttonId).button({
			icons:{primary: 'ui-icon-circle-zoomin'},
			label:"View"
		});
	$(buttonId).css('width',buttonSmall);
	$(buttonId).click(function(){
		
		setPolyLines(rnArray, false, 0);
		var routeName = $(selectBoxId).text();
		//alert(routeName);
		getSingleBusRoute(routeName, rnArray);
	});
}


function cbDispBusStops(){
	var isChecked = $('#disp_bus_stops').is(':checked');
	
	//alert("housing Density");
	//alert(isChecked);
	if(isChecked){
		alert("Checked!");
		showBusStopsLayer();
	} else {
		hideDensityLayer();
	}	
}


function setRunModelButton(){
	$("#run_model").button({
			icons:{primary: 'ui-icon-play'},
			label:"<span class='content-font'>Run</span>"
		});
	$("#run_model").css('width',buttonSmall);
	$("#run_model").click(function(){
		
		var scenarioName = $('#starting_definition_edit option:selected').val();
		var yearNumber = $('#num_years_sim').val();
		
		updateAllSelectOptions(scenarioName);
		//alert("Runing... ScenarioName: " + scenarioName);
		runModel(scenarioName,yearNumber);
		//dispTraDensityAjax(scenarioName);
	});
}

function setPauseModelButton(){
	$("#pause_model").button({
			icons:{primary: 'ui-icon-pause'},
			label:"<span class='content-font'>Pause</span>"
		});
	$("#pause_model").css('width',buttonSmall);
	$("#pause_model").click(function(){

	});
}

function setStopModelButton(){
	$("#stop_model").button({
			icons:{primary: 'ui-icon-stop'},
			label:"<span class='content-font'>Stop</span>"
		});
	$("#stop_model").css('width',buttonSmall);
	$("#stop_model").click(function(){

	});
}

function setReplayModelButton(){
	$("#replay_model").button({
			icons:{primary: 'ui-icon-arrowrefresh-1-e'},
			label:"<span class='content-font'>Replay</span>"
		});
	$("#replay_model").css('width',buttonMedium);
	$("#replay_model").click(function(){

		
		
	});
}



function setRunModelCompareButton(){
	$("#run_model_compare").button({
			icons:{primary: 'ui-icon-play'},
			label:"<span class='content-font'>Run</span>"
		});
	$("#run_model_compare").css('width',buttonSmall);
	$("#run_model_compare").click(function(){
		
		var scenarioName = $('#select_scenario_run :selected').text();
		//alert("Runing... ScenarioName: " + scenarioName);
		runModel(scenarioName,yearNumber);
	});
}

function setPauseModelCompareButton(){
	$("#pause_model_compare").button({
			icons:{primary: 'ui-icon-pause'},
			label:"<span class='content-font'>Pause</span>"
		});
	$("#pause_model_compare").css('width',buttonSmall);
	$("#pause_model_compare").click(function(){
		
	});
}

function setStopModelCompareButton(){
	$("#stop_model_compare").button({
			icons:{primary: 'ui-icon-stop'},
			label:"<span class='content-font'>Stop</span>"
		});
	$("#stop_model_compare").css('width',buttonSmall);
	$("#stop_model_compare").click(function(){
		
	});
}

function setEndModelCompareButton(){
	$("#end_model_compare").button({
			icons:{primary: 'ui-icon-power'},
			label:"<span class='content-font'>End</span>"
		});
	$("#end_model_compare").css('width',buttonMedium);
	$("#end_model_compare").click(function(){

	});
}

function setReplayModelCompareButton(){
	$("#replay_model_compare").button({
			icons:{primary: 'ui-icon-arrowrefresh-1-e'},
			label:"<span class='content-font'>Replay</span>"
		});
	$("#replay_model_compare").css('width',buttonMedium);
	$("#replay_model_compare").click(function(){

		//dispRoadDensity();
	});
}

function dispRoadDensity(){
	
	$.ajax({
		url:"servlet/RoadTrafficDensityServlet",
		type:"POST",
		success: function(msg){
			//alert(msg);
			var roadDensityArray = jQuery.parseJSON(msg);
			dispTraDensity(roadNetworkArrayCon, roadDensityArray, "#330066");
		}
	});

}

function setViewStatsButton(){
	$("#view_stats").button({
		icons:{primary: 'ui-icon-image'},
		label: "View Statistics"});
	$("#view_stats").css('width',buttonSize);
	$("#view_stats").click(function(){
		
		//window.open("imgoutput.html");
		
		window.open("imgoutput.html","View Statistics","width="+ 980 +",height="+ 980 +",scrollbars=no ,menubar=no,location=no,left=0,top=0");
		
	});
}

function setDefButton(){
	$("#save_def").button({
		icons:{primary: 'ui-icon-circle-plus'},
		label: "<span class='content-font'>Save</span>"
	});
	$("#save_def").css('width',buttonStore);
	$("#save_def").click(function(){
		var newDef = $('#new_definition').val();
		saveNewScenario(newDef);
		
		alert("Save new scenario: " + newDef);
	});
	
	$("#cancel_def").button({
		icons:{primary: 'ui-icon-circle-minus'},
		label: "<span class='content-font'>Cancel</span>"
	});
	$("#cancel_def").css('width',buttonStore);
	$("#cancel_def").click(function(){
		
//		var newDef = $('#new_definition').attr("value", "");
	});
}

function setScenButton(){
	$("#save_scen").button({
		icons:{primary: 'ui-icon-circle-plus'},
		label: "<span class='content-font'>Save</span>"
	});
	$("#save_scen").css('width',buttonStore);
	$("#save_scen").click(function(){
		var newSce = $('#new_scenario').val();
		saveNewScenario(newSce);
		alert("Save new scenario: " + newSce);
	});
	
	$("#cancel_scen").button({
		icons:{primary: 'ui-icon-circle-minus'},
		label: "<span class='content-font'>Cancel</span>"
	});
	$("#cancel_scen").css('width',buttonStore);
	$("#cancel_scen").click(function(){
//		var newDef = $('#new_scenario').attr("value", "");
	});
	
}

function setBusStopsButtons(){
	$("#dispBusStops").button({label: "@Bus Stops"});
	$("#dispBusStops").css('width',buttonSize);
	$("#dispBusStops").click(function(){
		
		if (busStopsArray.length == 0){
			makeRequestToGetBusStopsLayer();
		}else{
			showBusStopsLayer();
		}
	});

	$("#hideBusStops").button({label: "#Bus Stops"});
	$("#hideBusStops").css('width',buttonSize);
	$("#hideBusStops").click(function(){
		hideBusStopsLayer();
	});
}

function hideDensityLayer(legendId, sbArray){
	var minFillOpacity = {fillOpacity:0};
	for (var i = 0; i < sbArray.length; i++){
		sbArray[i].setOptions(minFillOpacity);
	}
	//setLegend("#FFFFFF", "", "", "");
	clearLegend(legendId);
}

function dispTraDensityAjax(scenarioName){
	$.ajax({
		url:"servlet/BusRouteTrafficDensityServlet",
		async:false,
		type:"POST",
		
		data:"scenarioName=" + scenarioName,
		success: function(msg){
			var traDenArray = eval("(" + msg + ")");
			var colorOption = {strokeWeight:4};
			var maxPop = dispTraDensity(traDenArray, colorOption);
			var avg = maxPop/2;

			//setLegend("#FF0000", maxPop, 0, avg);
		}
	});
}

function dispTraDensityAjaxDemo(){
	$.ajax({
		url:"servlet/BusRouteDemoServlet",
		async:false,
		
		success: function(msg){
			var traDenArray = eval("(" + msg + ")");
			var colorOption = {strokeWeight:3};
			var maxPop = dispTraDensityDemo(traDenArray, colorOption);
			var avg = maxPop/2;
			setLegend("#FF0000", maxPop, 0, avg);
		}
	});
}

function dispTraDensity(network, popDenArray, colorOption){
	var maxPop = popDenArray[0][1];
	var showOption = {clickable:true, strokeWeight:2};

	for (var i = 0; i < popDenArray.length; i++){		
		if(popDenArray[i][1] > maxPop){				
			maxPop = popDenArray[i][1];
		}
	}
	
	var nDensityAdjust=0.3;
	for (var i = 0; i < popDenArray.length; i++){
		popDenArray[i][1] = popDenArray[i][1]/maxPop*(1-0.3)+nDensityAdjust; 
		
	}
	
	for (var i = 0; i < popDenArray.length; i++){
		for (var j = 0; j < network.length; j++){
			if (network[j].name == popDenArray[i][0]){	
				var opacityOption = {strokeOpacity:popDenArray[i][1]};
				network[i].setOptions(opacityOption);
				network[i].setOptions(colorOption);	
			}
		}
	}

	for (var i = 0; i < network.length; i++){

		network[i].setOptions(showOption);		
	}	
	return maxPop;
}

function dispTraDensityDemo(popDenArray, colorOption){
	var maxPop = popDenArray[0][1];
	var showOption = {clickable:true};

	for (var i = 0; i < popDenArray.length; i++){		
		if(popDenArray[i][1] > maxPop){				
			maxPop = popDenArray[i][1];
		}
	}
	
	for (var i = 0; i < popDenArray.length; i++){
		popDenArray[i][1] = popDenArray[i][1]/maxPop;	
	}
	
	for (var i = 0; i < busNetworkArray.length; i++){
		if(busNetworkArray[i].name == popDenArray[i][0]){
			var opacityOption = {strokeOpacity:popDenArray[i][1]};
			busNetworkArray[i].setOptions(opacityOption);
			busNetworkArray[i].setOptions(colorOption);
		}
	}
	
	for (var i = 0; i < busNetworkArray.length; i++){

		busNetworkArray[i].setOptions(showOption);		
	}	
	return maxPop;
}

function dispPopDensity(legendId, sbArray){
	$.ajax({
		url:"servlet/SubareaPopDensityServlet",
		async:false,
		dataType: "text",
		success: function(msg){
			var popDenArray = eval("(" + msg + ")");
			var colorOption = {fillColor:"#FF0000"};
			var maxPop = dispDensity(popDenArray, colorOption, sbArray);
			
			setLegend(legendId, "#FF0000", maxPop, 0, maxPop/2, 'people/km^2');
			
		}
	});
}

function dispHouDensity(legendId, sbArray){
	$.ajax({
		url:"servlet/SubareaHousingDensityServlet",
		async:false,
		dataType: "text",
		success: function(msg){
			var houDenArray = eval("(" + msg + ")");
			var colorOption = {fillColor:"#CC00FF"};
			var maxPop = dispDensity(houDenArray, colorOption, sbArray);
			var avg = maxPop/2;
			
			setLegend(legendId, "#CC00FF", maxPop, 0, avg, 'households/km^2');
		}
	});
}

function dispSatisfaction(legendId, sbArray){
	$.ajax({
		url:"servlet/SatisfactionServlet",
		async:false,

		success: function(msg){
			var houDenArray = eval("(" + msg + ")");
			var colorOption = {fillColor:"#009933"};
			var maxPop = dispLiv(houDenArray, colorOption, sbArray);
			var avg = maxPop/2;
			
			setLivLegend(legendId, "#009933", maxPop, 0, avg);
		}
	});
}

function dispOrgSatisfaction(legendId, sbArray){
	$.ajax({
		url:"servlet/InitialSatisfactionServlet",
		async:false,
		dataType: "text",
		success: function(msg){
			var houDenArray = jQuery.parseJSON(msg);
			var colorOption = {fillColor:"#009933"};
			var maxPop = dispLiv(houDenArray, colorOption, sbArray);
			var avg = maxPop/2;
			
			setLivLegend(legendId, "#009933", maxPop, 0, avg);
		}
	});
}

function setLegend(legendId, color, max, min, avg, label){
	var opacity = 1;
	var opa_deduction = 0.05;
	
	for (var i = 1; i <= 20; i++){
		$(legendId + '_legend' + i).css('width',60);
		$(legendId + '_legend' + i).css('height', 5);
		$(legendId + '_legend' + i).css('background-color', color);
		$(legendId + '_legend' + i).css('opacity', opacity);
		opacity = opacity - opa_deduction;	
	}
	
	$(legendId + '_legend_label1').html('<p style="font-size: 70%">Density</p>');
	$(legendId + '_legend_label2').html('<p style="font-size: 70%">' + label + '</p>');
	$(legendId + '_max').html('<p style="font-size: 70%">' + Math.round(max) + '</p>');
	$(legendId + '_avg').html('<p style="font-size: 70%">' + Math.round(avg) + '</p>');
	$(legendId + '_min').html('<p style="font-size: 70%">' + min + '</p>');
}

function setLivLegend(legendId, color, max, min, avg){
	var opacity = 1;
	var opa_deduction = 0.05;
	
	for (var i = 1; i <= 20; i++){
		$(legendId + '_legend' + i).css('width',60);
		$(legendId + '_legend' + i).css('height', 5);
		$(legendId + '_legend' + i).css('background-color', color);
		$(legendId + '_legend' + i).css('opacity', opacity);
		opacity = opacity - opa_deduction;	
	}
	
	$(legendId + '_legend_label1').html('<p style="font-size: 70%">Density</p>');
	$(legendId + '_legend_label2').html('<p style="font-size: 70%">Satisfaction</p>');
	$(legendId + '_max').html('<p style="font-size: 70%">' + max + '</p>');
	$(legendId + '_avg').html('<p style="font-size: 70%">' + avg + '</p>');
	$(legendId + '_min').html('<p style="font-size: 70%">' + min + '</p>');
}

function clearLegend(legendId){
	var opacity = 0;
	var opa_deduction = 0.05;
	
	for (var i = 1; i <= 20; i++){
		$(legendId + '_legend' + i).css('width',60);
		$(legendId + '_legend' + i).css('height', 5);
		$(legendId + '_legend' + i).css('background-color', "#FFFFFF");
		$(legendId + '_legend' + i).css('opacity', opacity);
		opacity = opacity - opa_deduction;	
	}
	
	$(legendId + '_legend_label1').html('');
	$(legendId + '_legend_label2').html('');
	$(legendId + '_max').html('');
	$(legendId + '_avg').html('');
	$(legendId + '_min').html('');
}


function dispDensity(popDenArray, colorOption, sbArray){
	var maxPop = popDenArray[0][1];
	
	for (var i = 0; i < popDenArray.length; i++){		
		if(popDenArray[i][1] > maxPop){				
			maxPop = popDenArray[i][1];
		}
	}
	
	for (var i = 0; i < popDenArray.length; i++){		
		if(popDenArray[i][1] != 0){
			popDenArray[i][1] = popDenArray[i][1]/maxPop + densityOffset;
			if(popDenArray[i][1] > 1){
				popDenArray[i][1] = 1;
			}
		}			
	}
	
	for (var i = 0; i <popDenArray.length; i++){
		for(var j = 0; j <sbArray.length; j++){
			if(subareaArray[j].name == popDenArray[i][0]){
				var opacityOption = {fillOpacity:popDenArray[i][1]};
				sbArray[j].setOptions(opacityOption);
				sbArray[j].setOptions(colorOption);
				break;
		}	
		}
	}
	
	return maxPop;
}

function dispLiv(popDenArray, colorOption, sbArray){
	var maxPop = popDenArray[0][1];
	
	for (var i = 0; i < popDenArray.length; i++){		
		if(popDenArray[i][1] > maxPop){				
			maxPop = popDenArray[i][1];
		}
	}
	
	for (var i = 0; i < popDenArray.length; i++){		
		if(popDenArray[i][1] != 0){
			popDenArray[i][1] = popDenArray[i][1]/maxPop + densityOffset;
			if(popDenArray[i][1] > 1){
				popDenArray[i][1] = 1;
			}
		}			
	}
	
	for (var i = 0; i < sbArray.length; i++){
		for(var j = 0; j <sbArray.length; j++){
			if(subareaArray[j].name == popDenArray[i][0]){
			var opacityOption = {fillOpacity:popDenArray[i][1]};
			sbArray[j].setOptions(opacityOption);
			sbArray[j].setOptions(colorOption);
			break;
			}
		}
	}
	return maxPop;
}



function runModel(scenarioName,yearNumber){
	
	$.ajax({
		url:"servlet/ModelRunServlet",
		type:"POST",
		
		data:"scenarioName=" + scenarioName+"&yearNumber="+yearNumber,
 		success: function(msg){
     		//alert(msg);
			$('#run_status_report').html('<p class="select-font">' + msg + '</p>');
  		}
	});	     	
	
    $("#run_progress_bar").progressbar({value: proValue});
	proInterval = setInterval("updateProgressBar()", 6000);
}

var proInterval;
var proValue = 0;

function updateProgressBar(){
	
	$.ajax({
		url:"servlet/CheckModelProgressServlet",
		type:"POST",
		
		success:function(msg){
			proValue = jQuery.parseJSON(msg);
			//alert("progress: " + proValue);
			
			$("#run_progress_bar").progressbar({value: proValue});
			$("#compare_progress_bar").progressbar({value: proValue});
			$('#run_status_report').html('<p class="select-font">' + '' + '</p>');
			$("#run_progress_bar").css({'background':'url(images/pbar-ani.gif)'});
			//.ui-progressbar-value { background-image: url(images/pbar-ani.gif); }
			
//			alert(proValue);
			
			//#99FF99 #666666
			if ( proValue == 100){
				alert("The simulation has been finished.");
				clearInterval(proInterval);
			}
		}
	});	
}

function viewStatsResults(){
	$.ajax({
		url:"servlet/ModelStatsServlet",
		async:false,
		
 		success: function(msg){
 			var statsObj = eval("(" + msg + ")");
 			var meanTravel = statsObj.meanTravelTime;
 			var meanWait = statsObj.meanWaitTime;
 			var varTravel = statsObj.varTravelTime;
 			var varWait = statsObj.varWaitTime;
 		
 			var plotTravel = statsObj.plotTravelTime;
 			var plotWait = statsObj.plotWaitTime;
 			//alert(meanTravel + ", " + meanWait + ", " + varTravel + ", " + varWait + ", " + plotTravel + ", " + plotWait);
 			$("#meanTravelTime").html(meanTravel);
 			$("#meanWaitTime").html(meanWait);
 			$("#varTravelTime").html(varTravel);
 			$("#varWaitTime").html(varWait);
 			
     		$("#dispPlotTravel").html('<p><img src="' + plotTravel +'"/></p>');
     		$("#dispPlotWait").html('<p><img src="' + plotWait  + '"/></p>');
     		$("#SAMTabs").tabs('select',1);
  		}});
}

function updateAllSelectOptions(scenarioName){
	$('#starting_definition').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#starting_definition_edit').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#open_scenario').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#select_scenario').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#select_scenario_run').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#select_scenario_compare').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
}

function saveNewScenario(scenarioName){
	$('#starting_definition').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#starting_definition_edit').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#open_scenario').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#select_scenario').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#select_scenario_run').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
	$('#select_scenario_compare').append($("<option></option>").attr("value", scenarioName).text(scenarioName));
}

function getAllScenarioNames(){
	var scenarioNamesArray;
	$.ajax({
		url:"servlet/GetAllScenarioNamesServlet",
		async:false,
		dataType: "text",
		success: function(msg){
			scenarioNamesArray = eval("(" + msg + ")");
			
			//setLegend("#FFFFFF", '', '', '');		
			
			//alert(scenarioNamesArray.length);
			for (var i = 0; i < scenarioNamesArray.length; i++){
			
				updateAllSelectOptions(scenarioNamesArray[i]);	
			}
		}
	});
}

function getAllBusRouteNames(){
	var busRouteNamesArray;
	//alert("Get bus routeNames");
	$.ajax({
		url: 'servlet/GetAllBusRouteNamesServlet',
		dataType: "text",
		success: function(data){
			busRouteNamesArray = jQuery.parseJSON(data);
			
			for(var i = 0; i < busRouteNamesArray.length; i++){
				updateAllBusRoutes(busRouteNamesArray[i]);
			}
		}
	});
	
}

function updateAllBusRoutes(routeName){
	$('#select_bus_routes').append($("<option></option>").attr("value", routeName).text(routeName));
	$('#select_bus_routes_out').append($("<option></option>").attr("value", routeName).text(routeName));
}


