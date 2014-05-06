/**
 * 
 */


function createDialogMessage(){
	$( "#dialog-message" ).dialog({
		modal: true,
		autoOpen: false
	});
}

function createDialogConfirm(){
	$( "#dialog-confirm" ).dialog({
		resizable: false,
//		height:140,
		modal: true,
		autoOpen: false
//		buttons: {
//			OK: function() {
//				$( this ).dialog( "close" );
//			},
//			Cancel: function() {
//				$( this ).dialog( "close" );
//			}
//		}
	});
}

function createLinkForm()
{
	
	var street = $( "#street" ),
	swUfi=$("#sw_ufi"),
	fromleft=$("#fromleft"),
	toleft=$("#fromleft"),
	fromright=$("#fromright"),
	toright=$("#fromright"),
	aliasname=$("#aliasname"),
	label=$("#label"),
	length_m=$("#length_m"),
	roadtype=$("#roadtype"),
	id=$("#id"),
	geom=$( "#the_geom" ),
	allFields = $( [] ).add( street ).add(swUfi).add(fromleft).add(toleft).add(fromright).add(toright).add(aliasname)
	.add(label).add(length_m).add(roadtype).add(id).add( geom ),
	tips = $( ".validateTips" );
	
	$( "#dialog-form" ).dialog({
		autoOpen: false,
		show: "bounce",
		hide: "puff",
		width: 600,
		modal: true,
		close: function() {
			allFields.val( "" ).removeClass( "ui-state-error" );
		},
		buttons:{
			"Create a Link": function() {
				var bValid = true;
				allFields.removeClass( "ui-state-error" );
				
				bValid = bValid && checkLength( street, "street", 3, 16 ,tips);
				bValid = bValid && checkLength( swUfi, "sw_ufi", 8, 10 ,tips);
				bValid = bValid && checkLength( fromleft, "fromleft", 0, 5 ,tips);
				bValid = bValid && checkLength( toleft, "toleft", 0, 5 ,tips);
				bValid = bValid && checkLength( fromleft, "fromright", 0, 5 ,tips);
				bValid = bValid && checkLength( toleft, "toright", 0, 5 ,tips);
				// bValid = bValid && checkLength( geom, "geom", 6, 80 );
				
				bValid = bValid && checkRegexp(street, /^([a-zA-Z])+$/, "Only letters. e.g:CLEVELAND ST",tips );
				bValid = bValid && checkRegexp(swUfi, /^([0-9])+$/, "Only numbers.",tips);
				bValid = bValid && checkRegexp(fromleft, /^([0-9])+$/, "Only numbers.",tips);
				bValid = bValid && checkRegexp(toleft, /^([0-9])+$/, "Only numbers.",tips);
				bValid = bValid && checkRegexp(fromright, /^([0-9])+$/, "Only numbers.",tips);
				bValid = bValid && checkRegexp(toright, /^([0-9])+$/, "Only numbers.",tips);
				if ( bValid ) {
					makeRequestToUpdateRoadNetworkLayer($( "#street" ).val(),$( "#the_geom" ).val());
//					alert(street.val()+geom.val());
					$( this ).dialog( "close" );
				}
				
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		}
	});
	
}

function updateTips( tips,t ) {
	tips
		.text( t )
		.addClass( "ui-state-highlight" );
	setTimeout(function() {
		tips.removeClass( "ui-state-highlight", 1500 );
	}, 500 );
}

function checkLength( o, n, min, max ,tips) {
	if ( o.val().length > max || o.val().length < min ) {
		o.addClass( "ui-state-error" );
		updateTips(tips, "Length of " + n + " must be between " +
			min + " and " + max + "." );
		return false;
	} else {
		return true;
	}
}

function checkRegexp( o, regexp, n ,tips) {
	if ( !( regexp.test( o.val() ) ) ) {
		o.addClass( "ui-state-error" );
		updateTips(tips, n );
		return false;
	} else {
		return true;
	}
}