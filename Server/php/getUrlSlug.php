<?php
include('dbConnection.php');
/**
 *
 */

class UrlSlug{

public function getUrlSlug(){
$conn = new dbConnection();
        $conn->connectToDatabase();

        $conn->selectDatabase();

    $sql = "INSERT INTO GetURLSlug VALUES ('".$_GET['url_id']."','".$_GET['start_location']."','".$_GET['end_location']."','".$_GET['url_code']."');";
    //$sql = "INSERT INTO GetURLSlug VALUES (50,'dd','dd','dd')";

    $chkinst = mysql_query($sql);

if(! $chkinst )
{
  die('Could not enter data: ' . mysql_error());

  $conn->closeConnection();
}else {


$conn->closeConnection();

}


  }

}

$get = new UrlSlug();
$get->getUrlSlug();

?>
