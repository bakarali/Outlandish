<?php

/**
 * @author damith
 * @copyright 2011
 */

class dbConnection //create a class for make connection
{
    var $host="localhost";
    var $username="root";    // specify the sever details for mysql
    Var $password="";
    var $database="Outlandish";
    var $myconn;

    function connectToDatabase() // create a function for connect database
    {

        $conn= mysql_connect($this->host,$this->username,$this->password);

        if(!$conn)// testing the connection
        {
            die ("Cannot connect to the database");
        }

        else
        {

            $this->myconn = $conn;

            echo "Connection established";

        }

        return $this->myconn;

    }

    function selectDatabase() // selecting the database.
    {
        mysql_select_db($this->database);  //use php inbuild functions for select database

        if(mysql_error()) // if error occured display the error message
        {

            echo "Cannot find the database ".$this->database;

        }
         echo "Database selected..";
    }
    function insertInto(){
      # code...
    //  $data =  getUrlSlug::insertInto();
    //  $data1 =  SendCurrentLocation::insertInto();
     //echo $data1;
  //  echo $this->myconn;
  //mysql_query($data,$this->myconn);
  //  mysql_query($data1,$this->myconn);




    }
    function select(){
      # code...
    //  $data =  getUrlSlug::insertInto();
      $data =  getInvteeCurrentLocation::select();
     echo $data;
     echo "<br />";
    //echo $this->myconn;
  //mysql_query($data,$this->myconn);
     $sql1 = mysql_query($data,$this->myconn);
     echo $sql1;
  }

    function closeConnection() // close the connection
    {

        mysql_close($this->myconn);

        echo "Connection closed";
    }

}
$connection = new dbConnection(); //i created a new object

    $connection->connectToDatabase(); // connected to the database

    echo "<br />"; // putting a html break

    $connection->selectDatabase();// closed connection
    echo "<br />";
//    $connection->insertInto();
  $connection->insertInto();
    echo "<br />";
    $connection->select();
    echo "<br />";

    $connection->closeConnection();
?>
