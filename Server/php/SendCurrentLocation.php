<?php
include('dbConnection.php');
/**
 *
 */
 $conn = new dbConnection();
 //$sql = null;
class CurrentLocation{

  public function SendCurrentLocation(){
    $connObj = new dbConnection();
            $connObj->connectToDatabase();
         
    

    $sql = "INSERT INTO SendCurrentLocation VALUES ('".$_GET['scl_id']."','".$_GET['currentlat']."','".$_GET['currentlng']."','".$_GET['url_id']."');";
    $chkinst = mysql_query($sql);

  if(! $chkinst )
  {
  die('Could not enter data: ' . mysql_error());
  echo "<br />";
  $connObj->closeConnection();
  }else {
  echo "New record inserted successfully";
  echo "<br />";
  $connObj->closeConnection();

  }


  }

}

$get = new CurrentLocation();
$get->SendCurrentLocation();

 ?>
