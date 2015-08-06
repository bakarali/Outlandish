<?php
include('dbConnection.php');
/**
 *
 */
 $conn = new dbConnection();
 $sql = null;
class SendCurrentLocation{

  public function insertInto(){


    $sql = "INSERT INTO SendCurrentLocation VALUES ('".$_GET['scl_id']."','".$_GET['currentlat']."','".$_GET['currentlng']."','".$_GET['url_id']."');";
    //echo $sql;
    return $sql;

  }

}

$insert = new SendCurrentLocation();
//$insert->insertInto($sql);

 ?>
