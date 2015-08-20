<?php
include('dbConnection.php');
/**
 *
 */

class InvteeCurrentLocation{

  public function getInvteeCurrentLocation(){
    $conn = new dbConnection();
            $conn->connectToDatabase();

            $conn->selectDatabase();



    $sql = "SELECT * FROM SendCurrentLocation;";
    $result = mysql_query($sql);
    if(! $result )
    {
    die('Could not enter data: ' . mysql_error());

    $conn->closeConnection();
    }else {


      $rows = array();
      if (mysql_num_rows($result) > 0) {
    while($r = mysql_fetch_assoc($result)) {

       $rows[$id] = $r; //save the fetched row and add it to the array.
       echo json_encode($rows);

    }
}


    $conn->closeConnection();

  }

}
}
$get = new InvteeCurrentLocation();
$get->getInvteeCurrentLocation();

 ?>
