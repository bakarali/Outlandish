<?php
include ('dbConnection.php');
class updatechecker{
	
	public function updatechecker(){
		$update = 1;
		$current_version=2;
		echo 123;
		if ($update==$current_version){
			
		}else {
			$json = array (
					'status' => 'OK',
					'message' => "success",
					"response" => $response);
					$response = array (
							'status' => 'OK',
							'message' => 'success');
			
			echo json_encode ( $json );
		}
			
	}
}


?>