<?php
include('dbConnection.php');
/**
 *
 */
 $conn = new dbConnection();
 $sql = null;
class getUrlSlug{

  public function insertInto(){


    $sql = "INSERT INTO GetURLSlug VALUES ('".$_GET['url_id']."','".$_GET['start_location']."','".$_GET['end_location']."','".$_GET['url_code']."');";
    //echo $sql;
    return $sql;

  }

}

$insert = new getUrlSlug();
//$insert->insertInto($sql);

 ?>
