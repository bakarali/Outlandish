<?php
include('dbConnection.php');
/**
 *
 */
 $conn = new dbConnection();
 $sql = null;
class getInvteeCurrentLocation{

  public function select(){


    $sql = "SELECT * FROM SendCurrentLocation;";

    //echo $sql;
    return $sql;

  }

}

$insert = new getInvteeCurrentLocation();
//$insert->insertInto($sql);

 ?>
