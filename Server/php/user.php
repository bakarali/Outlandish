<?php
include ('dbConnection.php');
/**
 */
class User {
	public function register() {
		$conn = new dbConnection ();
		$date = date('m/d/Y h:i:s a', time());
		$sql = "INSERT INTO USER_INFO VALUES ('uid','" . $_GET ['name'] . "','" . $_GET ['email_id'] . "','" . $_GET ['password'] . "','" . $_GET ['mobile_number'] . "','$date');";
		// $sql = "INSERT INTO GetURLSlug VALUES (50,'dd','dd','dd')";
		
		$result = mysqli_query ( $conn->connectToDatabase (), $sql );
		
		if (! $result) {
			die ( 'Could not enter data: ' . mysql_error () );
			echo '{"status":"ERROR","message":"Sorry"}';
			$conn->closeConnection ();
		} else {
			echo '{"status":"OK","message":"New record inserted"}';
			$conn->closeConnection ();
		}
	}
	public function login() {
		
		// session_start ();
		$connClass = new dbConnection ();
		$conn = $connClass->connectToDatabase ();
		
		if (isset ( $_GET ['email_id'] ) and isset ( $_GET ['password'] )) {
			$email_id = $_GET ['email_id'];
			$password = $_GET ['password'];
			$query = "SELECT email_id,password FROM `USER_INFO` WHERE email_id='$email_id' and password='$password'";
			// $result = mysqli_query ( $conn->connectToDatabase (), $query );
			$count = array();
			$result = $conn->query ( $query );
			
			$row = $result->fetch_assoc();
			
						
			if (count($row) > 0) {
				echo '{"status":"OK","message":"success"}';
				
			} else {
				echo '{"status":"ERROR","message":"Sorry"}';
			}
		}
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
