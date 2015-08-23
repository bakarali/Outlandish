<?php
include('dbConnection.php');
/**
 *
 */

class User_start_loc{

public function getUrlSlug(){
$conn = new dbConnection();


      //  $conn->selectDatabase();
       $url_code="abcurl";
       $date = date('m/d/Y h:i:s a', time());
    $sql = "INSERT INTO USER_START_LOC VALUES ('usl_id','".$_GET['start_loc']."','".$_GET['end_loc']."','$url_code','".$_GET['uid']."','$date');";
    //$sql = "INSERT INTO GetURLSlug VALUES (50,'dd','dd','dd')";
    //echo $sql;

    $result = mysqli_query($conn->connectToDatabase(),$sql);
    var_dump($result);

if(!$result )
{
  //die('Could not enter data: ' . mysql_error());
echo '{"status":"ERROR","message":"Sorry"}';
  //$conn->closeConnection();


}else {
  $response = array('url_code' => $_GET['url_code']);
  $json = array( 'status' => 'OK', 'message' => "success", "response" => $response );
echo json_encode($json);

  //echo '{"status":"OK","message":"success","response":{"url_code":"http://www.url_code.com"}}';
//$conn->closeConnection();

}


  }

}

$get = new User_start_loc();
$get->getUrlSlug();

?>
