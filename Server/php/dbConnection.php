<?php

/**
 * @author damith
 * @copyright 2011
 */
class dbConnection // create a class for make connection
{
	var $host = "localhost";
	var $username = "root"; // specify the sever details for mysql
	var $password = "";
	var $database = "Outlandish";
	var $myconn;
	function connectToDatabase() // create a function for connect database
{
		$this->myconn = mysqli_connect ( "localhost", "root", "bak11ali@#", "Outlandish" ) or die ( "Error " . mysqli_error ( $conn ) );
		
		return $this->myconn;
	}
	
	
	function closeConnection() // close the connection
	{
	
		mysqli_close($this->myconn);
	}
	
}

/*
 * function selectDatabase() // selecting the database.
 * {
 * mysql_select_db($this->database); //use php inbuild functions for select database
 *
 * if(mysql_error()) // if error occured display the error message
 * {
 *
 * echo "Cannot find the database ".$this->database;
 *
 * }
 *
 * }
 */
// public function insertInto(){
// code...
// $data = getUrlSlug::insertInto();
// $data1 = SendCurrentLocation::insertInto();
// echo $data1;
// echo $this->myconn;
// echo $sql;
// $sql_result = mysql_query($sql,$this->myconn);
// $sql_result = mysql_query($sql,$this->myconn);
// return $sql_result;
// }
// function select(){
// code...
// $data = getUrlSlug::insertInto();
// $data = getInvteeCurrentLocation::select();
// echo $data;
// echo "<br />";
// echo $this->myconn;
// mysql_query($data,$this->myconn);
// $sql = mysql_query($data,$this->myconn);
// echo json_encode($sql);
// }


// }
// $connection = new dbConnection(); //i created a new object

// $connection->connectToDatabase(); // connected to the database

// echo "<br />"; // putting a html break

// $connection->selectDatabase();// closed connection
// echo "<br />";
// $connection->insertInto();
// $connection->insertInto();
// echo "<br />";
// $connection->select();
// echo "<br />";

// $connection->closeConnection();
?>
