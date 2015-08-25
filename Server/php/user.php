<?php
include ('dbConnection.php');
/**
 */
class User {
	public function register() {
		$conn = new dbConnection ();
		$connection = $conn->connectToDatabase();
		$date = date('m/d/Y h:i:s a', time());
		$sql = "INSERT INTO USER_INFO VALUES ('uid','" . $_GET ['name'] . "','" . $_GET ['email_id'] . "','" . $_GET ['password'] . "','" . $_GET ['mobile_number'] . "','$date');";
		// $sql = "INSERT INTO GetURLSlug VALUES (50,'dd','dd','dd')";
		
		$result = mysqli_query ($connection , $sql );
		$last_uid = mysqli_insert_id($connection);
		$response = array();
		if (! $result) {
			die ( 'Could not enter data: ' . mysql_error () );
			echo '{"status":"ERROR","message":"Sorry"}';
			
		} else {	
			$response = array(
				'status' => 'OK',
				'message'=> 'success',
				'response'=> array(
						'uid'=> $last_uid
				)	
			);
			echo json_encode($response);
			
		}
		$conn->closeConnection();
	}
	public function login() {
		
		// session_start ();
		$connClass = new dbConnection ();
		$conn = $connClass->connectToDatabase ();
		
		if (isset ( $_GET ['mobile_number'] ) and isset ( $_GET ['password'] )) {
			$mobile_number = $_GET ['mobile_number'];
			$password = $_GET ['password'];
			$query = "SELECT uid,name FROM `USER_INFO` WHERE mobile_number='$mobile_number' and password='$password'";
			// $result = mysqli_query ( $conn->connectToDatabase (), $query );
			$count = array();
			$result = $conn->query ( $query );
			
			$row = $result->fetch_assoc();
				
						
			if (count($row) > 0) {
				$response = array(
						'status' => 'OK',
						'message'=> 'success',
						'response' => array(
								'uid'=> $row['uid'],
								'name'=>$row['name']
						)
					);
				echo json_encode($response);
				
			} else {
				echo '{"status":"ERROR","message":"Sorry"}';
			}
		}
	//	$conn->closeConnection();
	}
}
$set = new User ();
if ($_GET ['action'] == 'login') {
	
	$set->login ();
} elseif ($_GET ['action'] == 'signup') {
	// code...
	
	$set->register ();
}

?>
