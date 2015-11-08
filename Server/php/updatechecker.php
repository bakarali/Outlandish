<?php
include ('dbConnection.php');
class updatechecker{
	
	public function updatechecker(){
		$updatecheck = true;
		
		$update_version = 1.01;
		
		if ($updatecheck==true){

			$json = array (
					'status' => 'OK',
					'message' => 'success',
					'update_required' => 'true',
					'min_version'=>$update_version
			);
					
			echo json_encode ( $json );
		}else{
			$json = array (
					'status' => 'ERROR',
					'message' => 'Not required',
					'update_required' => 'false'
			);
					
			echo json_encode ( $json );
		}
			
	}
}
$set = new updatechecker ();


?>