<?php
include ('dbConnection.php');
/**
 */
class User_start_loc {
	public function getUrlSlug() {
		$conn = new dbConnection ();
		
		// $conn->selectDatabase();
		
		$current_time = time ();
		$start_time = date ( 'm/d/Y h:i:s a', $current_time );
		$og_url_code = $current_time . "UID" . $_GET ['uid'];
		$url_code_md5 = md5 ( $og_url_code );
		$endTime = strtotime("+24 hours",strtotime($start_time));
		$url_expire_time = date ( 'm/d/Y h:i:s a', $endTime );
		
		$final_end_loc = preg_replace ( '@[^0-9\.\,]+@i', '', $_GET ['end_loc'] );
		$sql = "INSERT INTO USER_START_LOC VALUES ('usl_id','" . $_GET ['start_loc'] . "','" . $final_end_loc . "','$url_code_md5','" . $_GET ['uid'] . "','$start_time','$og_url_code',0,'$url_expire_time');";
		
		$result = mysqli_query ( $conn->connectToDatabase (), $sql );
		
		if (! $result) {
			
			echo '{"status":"ERROR","message":"Sorry"}';
		} else {
			$response = array (
					'url_code' => md5 ( $og_url_code ) 
			);
			$json = array (
					'status' => 'OK',
					'message' => "success",
					"response" => $response 
			);
			echo json_encode ( $json );
		}
		
		$conn->closeConnection ();
	}
	function stopShare() {
		$conn = new dbConnection ();
		$sql = "UPDATE USER_START_LOC SET status=1 WHERE url_code='" . $_GET ['url_code'] . "'";
		$result = mysqli_query ( $conn->connectToDatabase (), $sql );
		
		if ($result->affected_rows != - 1) {
			$response = array (
					'status' => 'OK',
					'message' => 'success' 
			);
		} else {
			$response = array (
					'status' => 'ERROR',
					'message' => 'failed' 
			);
		}
		
		echo json_encode ( $response );
	}
	function updateEndLoc() {
		$conn = new dbConnection ();
		$final_end_loc = preg_replace ( '@[^0-9\.\,]+@i', '', $_GET ['end_loc'] );
		$sql = "UPDATE USER_START_LOC SET end_loc='" . $final_end_loc . "' WHERE url_code='" . $_GET ['url_code'] . "'";
		$result = mysqli_query ( $conn->connectToDatabase (), $sql );
		
		if ($result->affected_rows != - 1) {
			$response = array (
					'status' => 'OK',
					'message' => 'success' 
			);
		} else {
			$response = array (
					'status' => 'ERROR',
					'message' => 'failed' 
			);
		}
		
		echo json_encode ( $response );
	}
}

$get = new User_start_loc ();
if ($_GET ['action'] == 'share_location') {
	$get->getUrlSlug ();
} elseif ($_GET ['action'] == 'stop_share_location') {
	$get->stopShare ();
} elseif ($_GET ['action'] == 'updateEndLoc') {
	$get->updateEndLoc ();
}

?>
