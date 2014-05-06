var sqlArray = new Array();
var previousActiveId = 0;

function setDatabase(SelecteddatabaseName, isNew, NewdatabaseName) {
	// hide the button if the process is still on
	if (!$("#save_changes").is(":hidden")) {
		$('#save_changes').hide();
	}

	$.ajax({
		type : "POST",
		url : "servlet/configuration/DatabaseConfigurationServlet",
		data : {
			SelecteddatabaseName : SelecteddatabaseName,
			isNew : isNew,
			NewdatabaseName : NewdatabaseName,
		}
	}).done(function(msg) {
		displayResponse(msg);
		if (msg != "The first Character should not be _ (an underscore)") {
			$('#save_changes').show('slow');
			showTabs(true);
		}
	});

}

function showTabs(isShow) {
	if (isShow === true) {
		$("#li1").show();
		$("#li2").show();
		$("#li3").show();
		$("#li4").show();
		$("#li5").show();
		$("#li6").show();
		$("#li7").show();
		$("#li8").show();
		$("#li9").show();
		$("#li10").show();
	} else {

		$("#li1").hide();
		$("#li2").hide();
		$("#li3").hide();
		$("#li4").hide();
		$("#li5").hide();
		$("#li6").hide();
		$("#li7").hide();
		$("#li8").hide();
		$("#li9").hide();
		$("#li10").hide();
	}
}

function saveDatabase(databaseName) {
	$.ajax({
		type : "POST",
		url : "servlet/configuration/DatabaseSaveServlet",
		data : {
			databaseName : databaseName,
		}
	}).done(function(msg) {
		displayResponse(msg);
	});
}

function submitTable() {
	// save the configuration
	var agentNumber = $('#agent_number').val();
	var shapefile = $('#shapefile').val();
	var startYear = $('#start_year').val();
	var runningYear = $('#running_year').val();
	var nameofscenario = $('#name_of_scenario_for_run').val();

	$.ajax({// save the configuration
		type : "POST",
		url : "servlet/configuration/ConfigurationServlet",
		data : {
			agentNumber : agentNumber,
			shapefile : shapefile,
			startYear : startYear,
			runningYear : runningYear,
			nameofscenario : nameofscenario,
		}
	}).done(function(msg) { // save the runs
		var ConfigurationId = parseInt(msg);
		var seed = $('#random_seed').val();
		$.ajax({
			type : "POST",
			url : "servlet/ModelRunServlet",
			data : {
				ConfigurationId : ConfigurationId,
				seed : seed,
			}
		}).done(function(another_msg) {
			displayResponse(another_msg);
		});

	});
}

function run(inputDatabaseName) {
	var id = $("#id_select").val();
	var seed = $("#random_seed").val();
	// var schema = $("#schema_select").val();
	$.ajax({
		type : "POST",
		url : "servlet/ModelRunServlet",
		data : {
			id : id,
			seed : seed,
			inputDatabaseName : inputDatabaseName
		// schema : schema
		}
	}).done(function(msg) {
		displayResponse(msg);
	});
}

function update() {

	var jsonText = JSON.stringify(sqlArray);

	$.ajax({
		type : "POST",
		url : "servlet/configuration/DwellingCapacityServlet",
		data : {
			sqlArray : jsonText
		},
		dataType : "json"
	}).done(function(msg) {
		alert("Updated: " + msg);
	});
}

function exportDwellingCSV(year) {
	window.location.href = "servlet/configuration/DwellingFileServlet?year="
			+ encodeURIComponent(encodeURIComponent(year));
}

function exportTableCSV(tableName) {
	window.location.href = "servlet/configuration/FileServlet?tableName="
			+ encodeURIComponent(encodeURIComponent(tableName));

}

function exportFile(fileName) {
	window.location.href = "servlet/configuration/TransimsFileServlet?fileName="
			+ encodeURIComponent(encodeURIComponent(fileName));
}

function displayResponse(msg) {
	$("#dialog-message").empty();
	$("#dialog-message").append("<p>" + msg + "</p>");
	$("#dialog-message")
			.dialog(
					{
						weight : 1200,
						modal : true,
						show : {
							effect : "shake",
							duration : 1000
						},
						hide : {
							effect : "clip",
							duration : 1000
						},
						buttons : {
							Ok : function() {
								$(this).dialog("close");
								$("#progressbar").hide('slow');
								if (msg == "Name conflicts with system database or existing scenario!\n Try another name please.") {
									$('#save_changes').hide();
									$('#db_indicator')
											.text(
													"Name conflict error! Try another name please.");
								} else if (msg == "The first Character should not be _ (an underscore)") {
									$('#save_changes').hide();
									$('#db_indicator')
											.text(
													"The first Character should not be _ (an underscore). Try another name please.");
								}
								$('#message_indicator').hide('slow');

							}
						}
					});

}

function importDwellingCSV(year, filepath) {
	var data = new FormData(document.forms.namedItem("dwelling_upload"));
	data.append("year", year);
	$.ajax({
		type : "POST",
		url : "servlet/configuration/DwellingFileServlet",
		data : data,
		processData : false, // tell jQuery not to process the data
		contentType : false
	}).done(function(msg) {
		displayResponse(msg);
	});
}

function importTableCSV(tableName, data) {
	data.append("tableName", tableName);
	$.ajax({
		type : "POST",
		url : "servlet/configuration/FileServlet",
		data : data,
		processData : false, // tell jQuery not to process the data
		contentType : false
	}).done(function(msg) {
		displayResponse(msg);
	});
}

function importFile(fileName, data) {
	data.append("fileName", fileName);
	$.ajax({
		type : "POST",
		url : "servlet/configuration/TransimsFileServlet",
		data : data,
		processData : false, // tell jQuery not to process the data
		contentType : false, // tell jQuery not to set contentType
	}).done(function(msg) {
		displayResponse(msg);
	});
}

// function importCSV(){
//	
//	
// $.ajax({
// type:"POST",
// url: "servlet/configuration/DwellingFileServlet",
// data:{schema:$("#schema_input").val(),year:$("#year_input").val()},
// processData:false,
// contentType:false
// }).done(function(msg){
// alert(msg);
// });
// }

function increase(percentage) {

	$.ajax({
		type : "POST",
		url : "servlet/configuration/DwellingCapacityServlet",
		data : {
			percentage : percentage
		}
	}).done(function(msg) {
		alert("Updated: " + msg);
	});
}

function showTable(msg) {
	var result = JSON.parse(msg);
	$("#myDataTable tbody").empty();
	$.each(result, function(index, item) {

		$("#myDataTable").append("<tr id=" + item.tz06 + "></tr>");
		$("#myDataTable tr:last").append("<td>" + item.tz06 + "</td>");
		$("#myDataTable tr:last").append(
				"<td><input value=" + item._1bd + "></input></td>");
		$("#myDataTable tr:last").append(
				"<td><input  value=" + item._2bd + "></input></td>");
		$("#myDataTable tr:last").append(
				"<td><input  value=" + item._3bd + "></input></td>");
		$("#myDataTable tr:last").append(
				"<td><input value=" + item._4bd + "></input></td>");
	});
}

function showRunsTable(msg) {

	$.ajax({
		type : "GET",
		url : "servlet/JudgeServersServlet",
	}).done(function(msg) {
		if (msg == "1")
			$.ajax({
				type : "GET",
				url : "servlet/configuration/RunsServlet",
			}).done(function(msg) {
				displayRunsTable(msg);
			});
		else 
			$.ajax({
				type : "GET",
				url : "servlet/configuration/RunsServlet",
			}).done(function(msg) {
				displayRunsTable2(msg);
			});

	});

}

function displayRunsTable(msg) {
	var result = JSON.parse(msg);
	$("#runs_table tbody").empty();
	$("#runs_table")
			.append(
					"<tr><td>Run(ID)</td><td>Name of scenario</td>"
							+ "<td>Simulation Seed</td><td>Status</td><td>Start Time</td>"
							+ "<td>Completed Time</td><td>Name of Output database</td>"
							+ "<td>User's Operation</td></tr>");
	$
			.each(
					result,
					function(index, item) {
						$("#runs_table").append("<tr></tr>");
						$("#runs_table  tr:last").append(
								"<td>" + item.id + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.nameofscenario + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.seed + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.status + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.timeStart + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.timeFinished + "</td>");

						if (item.status == "Model running."
								|| item.status == "Model completed, processing of output data commenced.") {
							$("#runs_table  tr:last").append(
									"<td>" + item.nameforoutputdb + "</td>");
							$("#runs_table  tr:last").append(
									"<td> <button  onclick=TerminateModel("
											+ item.id
											+ ") >Terminate</button>  </td>");

						} else {
							$("#runs_table  tr:last").append(
									"<td>" + item.nameforoutputdb + "</td>");
							$("#runs_table  tr:last").append(
									"<td> <button onclick=StartSingleModel("
											+ item.id
											+ ") >Start</button>  </td>");
						}

					});
}

function displayRunsTable2(msg) {
	var result = JSON.parse(msg);
	$("#runs_table tbody").empty();
	$("#runs_table")
			.append(
					"<tr><td>Run(ID)</td><td>Name of scenario</td>"
							+ "<td>Simulation Seed</td><td>Status</td><td>Start Time</td>"
							+ "<td>Completed Time</td><td>VM Id</td><td>Name of Output database</td>"
							+ "<td>User's Operation</td></tr>");

	$
			.each(
					result,
					function(index, item) {
						$("#runs_table").append("<tr></tr>");
						$("#runs_table  tr:last").append(
								"<td>" + item.id + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.nameofscenario + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.seed + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.status + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.timeStart + "</td>");
						$("#runs_table  tr:last").append(
								"<td>" + item.timeFinished + "</td>");

						if (item.status == "Model running."
								|| item.status == "Model completed, processing of output data commenced.") {
							$("#runs_table  tr:last").append(
									"<td>" + item.vmid + "</td>");
							$("#runs_table  tr:last").append(
									"<td>" + item.nameforoutputdb + "</td>");
							$("#runs_table  tr:last").append(
									"<td> <button  onclick=TerminateModel("
											+ item.id
											+ ") >Terminate</button>  </td>");

						} else {
							$("#runs_table  tr:last")
									.append(
											"<td>"
													+ "<select id=\"choose_vmid"
													+ item.id
													+ "\">	<option value=\"1\">1</option> <option value=\"2\">2</option> </select>"
													+ "</td>");
							$("#runs_table  tr:last").append(
									"<td>" + item.nameforoutputdb + "</td>");
							$("#runs_table  tr:last").append(
									"<td> <button onclick=StartModel("
											+ item.id
											+ ") >Start</button>  </td>");
						}

					});
}

function getConfiguration() {
	$.ajax({
		type : "GET",
		url : "servlet/configuration/ConfigurationServlet",
	}).done(
			function(msg) {
				// alert( "Data Retrived: " + msg );

				var result = JSON.parse(msg);

				var idSelect = $("#id_select");
				idSelect.empty();

				$.each(result, function(index, item) {
					idSelect.append(new Option(item.id, item.id));

				});

				idSelect.change(function() {
					var id = idSelect.val();

					$.each(result, function(index, item) {
						if (item.id == id) {
							$('p[name|="agent_number"]').text(
									"Number of Agent: " + item.agentNumber);
							$('p[name|="shapefile"]').text(
									"Shapefile: " + item.shapefile);
							$('p[name|="start_year"]').text(
									"Start Year: " + item.startYear);
							$('p[name|="running_year"]').text(
									"Running Year: " + item.runningYear);
						}

					});
				});

			});
}

// function showDwellingSchema(schemaSelector) {
//
// // var schemaSelector = $("#schema_select");
// schemaSelector.empty();
// // alert(result);
// $.each(dwellingSchemaResult, function(index, item) {
// schemaSelector.append(new Option(dwellingSchemaResult[index],
// dwellingSchemaResult[index]));
// });
//
// }

// function chooseDwellingSchema() {
//	
// var schemaSelector = $("#schema_choose");
// schemaSelector.empty();
// // alert(result);
// $.each(dwellingSchemaResult, function(index, item) {
// schemaSelector.append(new Option(dwellingSchemaResult[index],
// dwellingSchemaResult[index]));
// });
//	
//	
// }

// function getDwellingSchema() {
// $.ajax({
// type : "GET",
// url : "servlet/configuration/DwellingSchemaServlet",
// }).done(function(msg) {
// dwellingSchemaResult = JSON.parse(msg);
//		
// });
// }

function getDatabase(databaseSelector) {
	$.ajax({
		type : "GET",
		url : "servlet/configuration/DatabaseConfigurationServlet",
	}).done(function(msg) {
		var databaseResult = JSON.parse(msg);
		showDatabase(databaseSelector, databaseResult);
	});
}

function showDatabase(databaseSelector, databaseResult) {
	databaseSelector.empty();
	databaseSelector
			.append('<option value="List of scenarios">List of scenarios</option>');
	$.each(databaseResult, function(index, item) {
		databaseSelector.append('<option value= ' + databaseResult[index] + '>'
				+ databaseResult[index] + '</option>');
	});
	databaseSelector.show('slow');

}


function StartSingleModel(id) {
	$.ajax({
		type : "GET",
		url : "servlet/configuration/UserOperationServlet",
		data : {
			useroperation : "start",
			runsid : id,
			vmid : 1,
		}
	}).done(function(msg) {
		if (msg == "The VM server is busy. Please wait until it finishes.")
			displayResponse(msg);
		showRunsTable();
	});

}

function StartModel(id) {
	$.ajax({
		type : "GET",
		url : "servlet/configuration/UserOperationServlet",
		data : {
			useroperation : "start",
			runsid : id,
			vmid : $("#choose_vmid" + id).val(),
		}
	}).done(function(msg) {
		if (msg == "The VM server is busy. Please wait until it finishes.")
			displayResponse(msg);
		showRunsTable();
	});

}

function TerminateModel(id) {
	$.ajax({
		type : "GET",
		url : "servlet/configuration/UserOperationServlet",
		data : {
			useroperation : "terminate",
			runsid : id,
		}
	}).done(function(msg) {
		showRunsTable();
	});

}
