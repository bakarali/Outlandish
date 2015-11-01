<?php
require_once 'libs/vendor/autoload.php';

include ('dbConnection.php');
/**
 */
class Current_loc {
	public function send_current_loc() {
		$connObj = new dbConnection ();
		$date = date ( 'm/d/Y h:i:s a', time () );
		// $_GET ['url_code'] = "abcurl";
		$sql = "INSERT INTO CURRENT_LOC VALUES('cl_id','" . $_GET ['current_loc'] . "','" . $_GET ['url_code'] . "','$date');"; // remove usl id while INSERT also delete column from database
		                                                                                                                        // remove hardcode for url code , get it from url
		
		$result = mysqli_query ( $connObj->connectToDatabase (), $sql );
		
		if (! $result) {
			// die('Could not enter data: ' . mysql_error());
			echo '{"status":"ERROR","message":"Sorry"}';
		} else {
			
			echo '{"status":"OK","message":"success"}';
		}
		$connObj->closeConnection ();
	}
	public function get_current_loc() {
		$connObj = new dbConnection ();
		$expire_time;
		
		$expire_status="false";
		$get_current_loc_sql = "SELECT current_loc FROM CURRENT_LOC WHERE url_code = '" . $_GET ['url_code'] . "' order by clid desc limit 1;";
		$get_status_sql = "SELECT status,end_loc,expire_time FROM USER_START_LOC WHERE url_code = '" . $_GET ['url_code'] . "';";
		$current_time = time ();
		$start_time = date ( 'm/d/Y h:i:s a', $current_time );
		// $sql = 'SELECT * FROM CURRENT_LOC WHERE url_code =' .$_GET ['url_code'].';';
		
		$resultStatus = mysqli_query ( $connObj->connectToDatabase (), $get_status_sql );
		$status = 0;
		// print_r($resultStatus);
		$rs = mysqli_fetch_assoc ( $resultStatus );
		
		if (count ( $rs ) > 0){
			if($rs ['status'] == 1) {	
				$status = 1;	
			}
			$expire_time = $rs['expire_time'];
			
			if (strtotime($start_time) >= strtotime($expire_time)) {
				$expire_status = "true";
			} else {
				$expire_status = "false";							
			}
		}
		
		
		
		
		$result = mysqli_query ( $connObj->connectToDatabase (), $get_current_loc_sql );
		
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
						'current_loc' => $r ['current_loc'],
						'end_loc' => $rs ['end_loc'],
						'status' => $status,
						'expire_status'=>$expire_status
						
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
		
		$connObj->closeConnection ();
	}
	
	// getLocation - It will take last location from user_start_loc url_code
	function getLocation() {
		$connObj = new dbConnection ();
		
		$uid;
		$name;
		
		
		$expire_time;
		$user_start_loc_sql = "SELECT start_loc,end_loc,uid,expire_time FROM USER_START_LOC WHERE url_code = '" . $_GET ['url_code'] . "' LIMIT 1";
		$expire_status;
		$current_time = time ();
		$start_time = date ( 'm/d/Y h:i:s a', $current_time );
		$start_result = mysqli_query ( $connObj->connectToDatabase (), $user_start_loc_sql );
		$user_start_loc;
		$user_end_loc;
		$uid;
		if (mysqli_num_rows ( $start_result ) > 0) {
			// $r = mysql_fetch_assoc ( $result );
			$r = mysqli_fetch_assoc ( $start_result );
			$user_start_loc = $r ['start_loc'];
			$user_end_loc = $r ['end_loc'];
			$uid = $r ['uid'];
			$expire_time = $r ['expire_time'];
			
			
			$user_detail_sql = "Select name from USER_INFO where uid='" . $uid . "'";
			$user_detail_result = mysqli_query ( $connObj->connectToDatabase (), $user_detail_sql );
			if (mysqli_num_rows ( $user_detail_result )) {
				$row = mysqli_fetch_assoc ( $user_detail_result );
				$name = $row ['name'];
			} else {
			}
		} else {
		}
		if (strtotime($start_time) >= strtotime($expire_time)) {
			$expire_status = "true";
			//echo $start_time,$expire_time,$expire_status;
			
		} else {
			$expire_status = "false";
		//	echo $start_time,$expire_time,$expire_status;
	
		}
		
		$connObj->closeConnection ();
		
		$start_loc = explode ( ",", $user_start_loc );
		$start_loc_lat = $start_loc [0];
		$start_loc_lng = $start_loc [1];
		
		if ($user_end_loc !== NULL) {
			$end_loc = explode ( ",", $user_end_loc );
			$end_loc_lat = $end_loc [0];
			$end_loc_lng = $end_loc [1];
		} else {
			$end_loc_lat = "";
			$end_loc_lng = "";
		}
		
		$loader = new Twig_Loader_Filesystem ( 'templates/' );
		$twig = new Twig_Environment ( $loader );
		$template = $twig->loadTemplate ( 'currentLocation.html' );
		echo $template->render ( array (
				'name' => ucfirst ( $name ),
				'start_loc_lat' => $start_loc_lat,
				'start_loc_lng' => $start_loc_lng,
				'end_loc_lat' => $end_loc_lat,
				'end_loc_lng' => $end_loc_lng,
				'url_code' => $_GET ['url_code'],
				'expire_status'=>$expire_status		
		)
		 );
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
