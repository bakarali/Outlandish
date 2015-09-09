<?php
include('dbConnection.php');
/**
 *
 */

class User_start_loc{

public function getUrlSlug(){
$conn = new dbConnection();


      //  $conn->selectDatabase();
       
       $current_time = time();
       $date = date('m/d/Y h:i:s a', $current_time);
       $og_url_code = $current_time."UID".$_GET['uid'];
      $url_code_md5 = md5($og_url_code);
    
      $final_end_loc = preg_replace('@[^0-9\.\,]+@i', '',$_GET['end_loc']);
    $sql = "INSERT INTO USER_START_LOC VALUES ('usl_id','".$_GET['start_loc']."','".$final_end_loc."','$url_code_md5','".$_GET['uid']."','$date','$og_url_code');";
  
    $result = mysqli_query($conn->connectToDatabase(),$sql);
  

if(!$result )
{
 
echo '{"status":"ERROR","message":"Sorry"}';



}else {
  $response = array('url_code' => md5($og_url_code));
  $json = array( 'status' => 'OK', 'message' => "success", "response" => $response );
echo json_encode($json);



}

$conn->closeConnection();
  }

}

$get = new User_start_loc();
$get->getUrlSlug();

?>
