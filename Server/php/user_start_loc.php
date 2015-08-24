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
      
    $sql = "INSERT INTO USER_START_LOC VALUES ('usl_id','".$_GET['start_loc']."','".$_GET['end_loc']."','md5($og_url_code)','".$_GET['uid']."','$date','$og_url_code');";
    //$sql = "INSERT INTO GetURLSlug VALUES (50,'dd','dd','dd')";
    //echo $sql;

    $result = mysqli_query($conn->connectToDatabase(),$sql);
    //var_dump($result);

if(!$result )
{
  //die('Could not enter data: ' . mysql_error());
echo '{"status":"ERROR","message":"Sorry"}';
  //$conn->closeConnection();


}else {
  $response = array('url_code' => md5($og_url_code));
  $json = array( 'status' => 'OK', 'message' => "success", "response" => $response );
echo json_encode($json);

  //echo '{"status":"OK","message":"success","response":{"url_code":"http://www.url_code.com"}}';

}

$conn->closeConnection();
  }

}

$get = new User_start_loc();
$get->getUrlSlug();

?>
