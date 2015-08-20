<?php
include('dbConnection.php');
/**
 *
 */
 $conn = new dbConnection();
 //$sql = null;
class CurrentLocation{

  public function SendCurrentLocation(){
    $conn = new dbConnection();
            $conn->connectToDatabase();
            echo "<br />";
            $conn->selectDatabase();
            echo "<br />";

    $sql = "INSERT INTO SendCurrentLocation VALUES ('".$_GET['scl_id']."','".$_GET['currentlat']."','".$_GET['currentlng']."','".$_GET['url_id']."');";
    $chkinst = mysql_query($sql);

  if(! $chkinst )
  {
  die('Could not enter data: ' . mysql_error());
  echo "<br />";
  $conn->closeConnection();
  }else {
  echo "New record inserted successfully";
  echo "<br />";
  $conn->closeConnection();

  }


  }

}

$get = new CurrentLocation();
$get->SendCurrentLocation();

 ?>
