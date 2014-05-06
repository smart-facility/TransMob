//	var dwellingSchemaResult;
$(document)
		.ready(

				function() {
					$('#save_changes').hide('slow');
					

					$(function() {
						$("#accordion").accordion();
						$("#accordion2").accordion();
					});

					$("#selecting_from_all_db").click(
							function() {
								getDatabase($('#database_list'));
							}
					);
					
					
					$("#li1").click(
							function() {
								getDatabase($('#name_of_scenario_for_run'));
							}
					);
					
					$("#tabs").tabs();
					showTabs(false);
					
					$("#li11").click(
							function() {
								showRunsTable();
							}
					);
					
							
					$('#create_db').click(
							function() {
								
								if ($("#new_db_name").val() == "") {
								$("#dialog-message").empty();
								$("#dialog-message")
										.append(
												"<p>Please fill in name of scenario!</p>");
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
															$(this)
																	.dialog(
																			"close");
														}
													}
												});}
								else
									{						
								var newDBname = $('#new_db_name').val();
								setDatabase(null, true, newDBname);			
								$('#db_indicator').val(newDBname);
								$('#db_indicator').text("Current scenario: " + newDBname);								
								$('#db_indicator').show('slow');
									}
							});

					$('#save_changes').click(function() {
						$('#message_indicator').hide('slow');
						$('#message_indicator').text("Saving the scenario ... ");
						$('#message_indicator').show('slow');
						$("#progressbar").progressbar({
							value : false
						});
						$("#progressbar").show('slow');				
						saveDatabase($('#db_indicator').val());
						$('#db_indicator').show('slow');
						//$(this).hide('slow');
						//showTabs(false);
					});

					$('#select_database').click(function() {
						if ($("#new_db_name_from_existing").val() == "") {
							$("#dialog-message").empty();
							$("#dialog-message")
									.append(
											"<p>Please fill in name of scenario!</p>");
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
														$(this)
																.dialog(
																		"close");
													}
												}
											});}
						else if ($('#database_list').val() != "List of scenarios") {
							$("#progressbar").progressbar({
								value : false
							});
							

							$("#progressbar").show('slow');
							setDatabase($('#database_list').val(), false, $('#new_db_name_from_existing').val());
							$('#db_indicator').val($('#new_db_name_from_existing').val());
							$('#db_indicator').text("Current scenario: "+ $('#new_db_name_from_existing').val());
							$('#db_indicator').show('slow');							
						}
					});
				
					$('#submit_config')
							.click(
									function() {
										if ($("#agent_number").val() == ""|| $("#random_seed").val() == ""
											|| $("#name_of_scenario_for_run").val() == "List of scenarios") {
											$("#dialog-message").empty();
											$("#dialog-message")
													.append(
															"<p>Please fill in all the parameters!</p>");
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
																		$(this)
																				.dialog(
																						"close");
																	}
																}
															});
										}

										else
											submitTable();
									});

					$("#target").validate({
						focusCleanup : true,
						errorElement : "span",
						// errorContainer: Selector,
						// focusInvalid: true,
						onkeyup : false,
						// errorLabelContainer: "#errorList ul",
						// errorPlacement : function(error, element) {
						// error.insertAfter(element);
						// error.appendTo(element.next());
						// $(element).next().html(error);
						// },

						submitHandler : function(form) {
							form.submit();
						},
					});

					$("#agent_number").rules("add", {
						required : true,
						number : true,
						min : 1,
						digits : true,
						messages : {
							required : "Required input",
							minlength : "Must be a number",
							min : "Must larger than 1",
						}
					});


					$("#random_form").validate({
						submitHandler : function(form) {
							form.submit();
						}
					});

					$("#random_seed").rules("add", {
						required : true,
						number : true,
						digits : true,
						messages : {
							required : "Required input",
							minlength : "Must be a number"
						}
					});

					$("#get_configuration").click(function() {
						getConfiguration();
					});

					$("#show_runs").click(function() {						
						showRunsTable();
					});
					



					$("#export").click(
							function() {
								exportDwellingCSV($("#choose_year").val());

							});

					$("#export_l").click(
							function() {
								exportTableCSV($("#choose_table_l").val());
							});

					$("#export_sp").click(
							function() {								
								exportTableCSV($("#choose_table_sp").val());
							});

					$("#export_t")
							.click(

									function() {
										//if ($("#choose_table_t").val() === "public.transims_activity_location") {
											exportTableCSV( "public.transims_activity_location");
										//} 
									});

					$("#submit_button_d").click(
							function() {			
								importDwellingCSV($("#form_1").val(), $("#choose_file").val());								
							});

					$("#submit_button_sp").click(
							function() {
								var data = new FormData(document.forms.namedItem("table_upload_sp"));								
								importTableCSV($("#choose_table_sp_import").val(), data);
							});


					$("#submit_button_l")
							.click(
									function() {
										var data = new FormData(document.forms.namedItem("table_upload_l"));
										importTableCSV($("#choose_table_l").val(), data);		
							});

					$("#submit_button_file")
							.click(
									function() {
										var data = new FormData(document.forms
												.namedItem("file_uploader"));
										importTableCSV("public.transims_activity_location", data);
//										if ($("#choose_file_t").val() === "public.transims_activity_location") {
//											importTableCSV($("#choose_file_t").val(), data);
//										} else {
//											importFile($("#choose_file_t")	.val(), data);
//										}
									});

					$("#dwelling_increase").click(function() {
						increase($("#precentage").val());
					});

					$("#run").click(function() {
						run($('#db_indicator').val());
					});
					

					
					
					
					


				});



