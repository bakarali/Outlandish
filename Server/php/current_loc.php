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
}

$get = new Current_loc ();
if ($_GET ['action'] == 'send_current_loc') {
	$get->send_current_loc ();
} elseif ($_GET ['action'] == 'get_current_loc') {
	$get->get_current_loc ();
}

?>
