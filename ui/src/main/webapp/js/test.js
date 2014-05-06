/**
 * 
 */

function displayLink()
{
	var id = 1;
	$("span#set").click(function(){
		$.ajax({
			url: 'servlet/RoadNetworkInfoServlet',
			type: 'POST',
			data: "id=" + id,
			success: function (data){
				var roadBeanObj = jQuery.parseJSON(data);
				generateLinkOutput(roadBeanObj);
			}
		});
	})
	
	$("span#set").click(function(){
		$.ajax({
			url: 'servlet/RoadNetworkLayerServlet',
			type: 'POST',
			success: function (data){
				var roadCoordsPGs = preprocessCoords(data);
				$("p#display_link").append("<strong>Hello</strong>");
			}
		});
	})
}

function generateLinkOutput(roadBeanObj)
{
	var contentString = "Name: " + roadBeanObj.roadName + "<br/>Road ID: " + roadBeanObj.roadId 
	+ "<br />Road Type: " + roadBeanObj.roadType+"<br />Speed: "+roadBeanObj.speed;
	$("p#display_link").html(contentString);
}