<?php
include ('dbConnection.php');
/**
 */
class Current_loc {
	public function send_current_loc() {
		$conn = new dbConnection ();
		$date = date('m/d/Y h:i:s a', time());
		//$_GET ['url_code'] = "abcurl";
		$sql = "INSERT INTO CURRENT_LOC VALUES('cl_id','" . $_GET ['current_loc'] . "','" . $_GET ['url_code'] . "','$date');"; // remove usl id while INSERT also delete column from database
		                                                                                                                // remove hardcode for url code , get it from url
		
		$result = mysqli_query ( $conn->connectToDatabase (), $sql );
		
		if (! $result) {
			// die('Could not enter data: ' . mysql_error());
			echo '{"status":"ERROR","message":"Sorry"}';
			
		} else {
			
			echo '{"status":"OK","message":"success"}';
			
		}
		$conn->closeConnection();
	}
	public function get_current_loc() {
		$conn = new dbConnection ();
		
		$sql = "SELECT current_loc FROM CURRENT_LOC WHERE url_code = '".$_GET ['url_code']."';";
		// $sql = 'SELECT * FROM CURRENT_LOC WHERE url_code =' .$_GET ['url_code'].';';
		
		$result = mysqli_query ( $conn->connectToDatabase (), $sql );
		
		if (! $result) {
			// die ( 'Could not enter data: ' . mysql_error () );
			echo '{"status":"ERROR","message":"Sorry"}';
		
		} else {
			$json = array();
			// echo '{"status":"OK","message":"success"}';
			
			if (mysqli_num_rows ( $result ) > 0) {
				// $r = mysql_fetch_assoc ( $result );
				$r = mysqli_fetch_assoc ( $result );
				// save the fetched row and add it to the array.
				$response = array (
						'current_loc' => $r ['current_loc'] 
				);
				$json = array (
						"status" => "OK",
						"message" => "success",
						"response" => $response 
				);
			}
			//echo $json;
			echo json_encode($json);
			// $conn->closeConnection ();
		}
		
		$conn->closeConnection();
	}
	
	//getLocation -  It will take last location from user_start_loc  url_code
	
	function  getLocation(){
	
		
		$html = '<!DOCTYPE html>
<html>
  <head>
    <title>Geolocation</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <style>
      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
      }
      #map {
        height: 100%;
      }
    </style>
  </head>
  <body>
    <div id="map"></div>
    <script>
// Note: This example requires that you consent to location sharing when
// prompted by your browser. If you see the error "The Geolocation service
// failed.", it means you probably did not give permission for the browser to
// locate you.
 var map;
  var infoWindow;
				
function initMap() {
 
map =  new google.maps.Map(document.getElementById("map"), {
    center: {lat: -34.397, lng: 150.644},
    zoom: 14
  });
				infoWindow = new google.maps.InfoWindow({map: map});
				
				
  
  
    navigator.geolocation.getCurrentPosition(function(position) {
//       var pos = {
//         lat: position.coords.latitude,
//         lng: position.coords.longitude
//       };
				
	var pos = {
	  lat:12.9232885,
	  lng:77.61284283			
	}

      infoWindow.setPosition(pos);
      infoWindow.setContent("Manzur Husain.");
      map.setCenter(pos);
    }, function() {
      handleLocationError(true, infoWindow, map.getCenter());
    });
  
}

function handleLocationError(browserHasGeolocation, infoWindow, pos) {
  infoWindow.setPosition(pos);
  infoWindow.setContent(browserHasGeolocation ?
                        "Error: The Geolocation service failed." :
                        "Error: Your browser doesn\'t support geolocation.");
}
				
//updatelocation function
function cUpdateLocation(){
				var pos ={};
				setInterval(function(){
				
				
	 pos = {
	  lat:12.9232885,
	  lng:77.71284283			
	}
	infoWindow.setPosition(pos);			
				
	}, 5000);
				
	//http://code.runnable.com/UhY_jE3QH-IlAAAP/how-to-parse-a-json-file-using-jquery
	//http://www.w3schools.com/jsref/jsref_split.asp
	//http://stackoverflow.com/questions/5818129/how-to-change-the-position-of-marker-from-a-javascript-function
	}
				
cUpdateLocation();				
				
				
    </script>
    <script src="https://maps.googleapis.com/maps/api/js?signed_in=true&callback=initMap"
        async defer>
    </script>
  </body>
</html>';
		echo $html;
	}
	
	
}

$get = new Current_loc ();
if ($_GET ['action'] == 'send_current_loc') {
	$get->send_current_loc ();
} elseif ($_GET ['action'] == 'get_current_loc') {
	$get->get_current_loc ();
}elseif ($_GET ['action'] == 'getLocation') {
	$get->getLocation ();
}

?>
