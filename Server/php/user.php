<?php
include ('dbConnection.php');
/**
 */
class User {
	public function register() {
		$conn = new dbConnection ();
		$conn = $conn->connectToDatabase ();
		$date = date ( 'm/d/Y h:i:s a', time () );
		// $passwordDecrypt = $_GET ['password'];
		$passwordDecrypt = sha1 ( $_GET ['password'] );
		$checkuser = "SELECT uid FROM USER_INFO WHERE email_id='" . $_GET ['email_id'] . "' or mobile_number='" . $_GET ['mobile_number'] . "'";
		$count = array ();
		$result = $conn->query ( $checkuser );
		$row = $result->fetch_assoc ();
		if (count ( $row ) > 0) {
			
			// $response = array (
			// 'status' => 'ERROR',
			// 'message' => 'Already registered'
			
			// );
			$response = array (
					'status' => 'ERROR',
					'message' => 'Already registered' 
			)
			;
			echo json_encode ( $response );
		} else {
			$signupuser = "INSERT INTO USER_INFO VALUES ('uid','" . $_GET ['name'] . "','" . $_GET ['email_id'] . "','$passwordDecrypt','" . $_GET ['mobile_number'] . "','$date');";
			
			// $signupuser = "INSERT INTO GetURLSlug VALUES (50,'dd','dd','dd')";
			
			$result = mysqli_query ( $conn, $signupuser );
			$last_uid = mysqli_insert_id ( $conn );
			$response = array ();
			if (! $result) {
				
				echo '{"status":"ERROR","message":"Sorry"}';
			} else {
				$response = array (
						'status' => 'OK',
						'message' => 'success',
						'response' => array (
								'uid' => $last_uid 
						) 
				);
				echo json_encode ( $response );
			}
			$conn->closeConnection ();
		}
	}
	public function login() {
		
		//
		$connClass = new dbConnection ();
		$conn = $connClass->connectToDatabase ();
		
		if (isset ( $_GET ['mobile_number'] ) and isset ( $_GET ['password'] )) {
			$mobile_number = $_GET ['mobile_number'];
			$password = $_GET ['password'];
			$passwordDecrypt = sha1 ( $_GET ['password'] );
			
			$getlogin_query = "SELECT uid,name,password FROM `USER_INFO` WHERE mobile_number='$mobile_number'";
			
			$count = array ();
			$result = $conn->query ( $getlogin_query );
			$row = $result->fetch_assoc ();
			if (count ( $row ) > 0) {
				if ($row ['password'] == $passwordDecrypt) {
					$response = array (
							'status' => 'OK',
							'message' => 'success',
							'response' => array (
									'uid' => $row ['uid'],
									'name' => $row ['name'] 
							) 
					);
					echo json_encode ( $response );
				} else {
					echo '{"status":"ERROR","message":"Wrong credential"}';
				}
			} else {
				echo '{"status":"ERROR","message":"Sorry, Please register first."}';
			}
		}
	}
}
$set = new User ();
if ($_GET ['action'] == 'login') {
	
	$set->login ();
} elseif ($_GET ['action'] == 'signup') {
	
	$set->register ();
}

?>
