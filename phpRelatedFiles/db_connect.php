<?php
/**
 * A class file to connect to database
 */
class DB_CONNECT {
    //set to con in connect() so close() can close the connection
    public $gCon;

    // constructor
    function __construct() {
        // connecting to database
        $this->connect();
    }
 
    // destructor
    function __destruct() {
        // closing db connection
        $this->close();
    }
 
    /**
     * Function to connect with database
     */
    function connect() {
        // import database connection variables
        require 'db_config.php';
 
        // Connecting to mysql database
        $con = mysqli_connect($SERVER, $USER, $PASSWORD) or die(mysqli_error());
        //if ($con) echo "Connection established\n"; 
        // Selecing database
        $db = mysqli_select_db($con, $DATABASE) or die(mysqli_error());
        //if ($db) echo "db connected\n"; 
        
        // returing connection cursor
        $this->gCon = $con;
    }
 
    /**
     * Function to close db connection
     */

    function close() {
        // closing db connection
        //echo 'closing';
        @mysqli_close($gCon);
    }
 
}
//$db = new DB_CONNECT();

?>