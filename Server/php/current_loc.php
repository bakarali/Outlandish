<?php
include ('dbConnection.php');
/**
 */
class Current_loc {
	public function send_current_loc() {
		$conn = new dbConnection ();
		$date = date ( 'm/d/Y h:i:s a', time () );
		// $_GET ['url_code'] = "abcurl";
		$sql = "INSERT INTO CURRENT_LOC VALUES('cl_id','" . $_GET ['current_loc'] . "','" . $_GET ['url_code'] . "','$date');"; // remove usl id while INSERT also delete column from database
		                                                                                                                        // remove hardcode for url code , get it from url
		
		$result = mysqli_query ( $conn->connectToDatabase (), $sql );
		
		if (! $result) {
			// die('Could not enter data: ' . mysql_error());
			echo '{"status":"ERROR","message":"Sorry"}';
		} else {
			
			echo '{"status":"OK","message":"success"}';
		}
		$conn->closeConnection ();
	}
	public function get_current_loc() {
		$conn = new dbConnection ();
		
		$get_current_loc_sql = "SELECT current_loc FROM CURRENT_LOC WHERE url_code = '" . $_GET ['url_code'] . "';";
		// $sql = 'SELECT * FROM CURRENT_LOC WHERE url_code =' .$_GET ['url_code'].';';
		$user_start_loc_sql = "SELECT start_loc FROM USER_START_LOC WHERE url_code = '" . $_GET ['url_code'] . "';";
		
		$result = mysqli_query ( $conn->connectToDatabase (), $get_current_loc_sql );
		
		if (! $result) {
			// die ( 'Could not enter data: ' . mysql_error () );
			echo '{"status":"ERROR","message":"Sorry"}';
		} else {
			$json = array ();
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
			// echo $json;
			echo json_encode ( $json );
			// $conn->closeConnection ();
		}
		
		$conn->closeConnection ();
	}
	
	// getLocation - It will take last location from user_start_loc url_code
	function getLocation() {
		$conn = new dbConnection ();
		
		$uid;
		$name;
		
		 $user_start_loc_sql = "SELECT start_loc,uid FROM USER_START_LOC WHERE url_code = '". $_GET['url_code']."'";
		
		$start_result = mysqli_query ( $conn->connectToDatabase (), $user_start_loc_sql );
		$current_location;
		
	
			
			
			if (mysqli_num_rows ($start_result) > 0) {
				// $r = mysql_fetch_assoc ( $result );
				$r = mysqli_fetch_assoc ($start_result);
				$current_location = $r['start_loc'];

				$uid = $r['uid'];
				
				$user_detail_sql = "Select name from USER_INFO where uid='".$uid."'";
				$user_detail_result = mysqli_query($conn->connectToDatabase(), $user_detail_sql);
				if(mysqli_num_rows($user_detail_result)){
					$row = mysqli_fetch_assoc($user_detail_result);
					$name = $row['name'];
						
				}else {
						
				}
			}else {
				
			}
			
			$conn->closeConnection();
			
			$loc = explode(",",$current_location);
			
		
		 
		$html = '<!DOCTYPE html>
<html>
  <head>
    <title>Geolocation</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
				<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
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
//var url = "http://192.168.1.8/current_loc.php?action=get_current_loc&url_code='.$_GET['url_code'].'";
 var url = "http://techhunger.com/current_loc.php?action=get_current_loc&url_code='.$_GET['url_code'].'";
  var infoWindow;
				
function initMap() {
				  
 
map =  new google.maps.Map(document.getElementById("map"), {
    center: {lat: -34.397, lng: 150.644},
    zoom: 14
  });
				infoWindow = new google.maps.InfoWindow({map: map});
				
				
  
  
   var lating = parseFloat('.$loc[0].');
	 var lngting = parseFloat('.$loc[1].');	
				
	var pos = {
	 lat:'.$loc[0].',
	  lng:'.$loc[1].'			
	}

      infoWindow.setPosition(pos);
      infoWindow.setContent("'.$name.'");
      map.setCenter(pos);
    
  }
				
//updatelocation function
function cUpdateLocation(){
				
				setInterval(function(){
					var lat1 ;
					var lng1 ;
					 $.ajax({
				//type:"GET",
                    url: "http://localhost/current_loc.php?action=get_current_loc&url_code=abcurl",
                    //force to handle it as text
                    dataType: "json",
                    success: function(data) {
					
                        var location = data.response.current_loc;
   					 	var res = location.split(",");
      		
   						  lat1 = parseFloat(res[0]);
						  lng1 = parseFloat(res[1]);
      				
	 pos = {
	 lat:lat1,
	 lng:lng1	
	}
	infoWindow.setPosition(pos);
						
                        
                    }
                });
				
				
				
	}, 5000);
				
	
	}
				
cUpdateLocation();				
				
				
    </script>
    <script src="https://maps.googleapis.com/maps/api/js?signed_in=true&callback=initMap&sensor=false"
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
} elseif ($_GET ['action'] == 'getLocation') {
	$get->getLocation ();
}

?>
