<?php



	require 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

	//on android user must enter info for all fields so post variables will always be set(no need for ISSET)

    //storing post
	
	$userID = $_POST["userID"];
	$userName = $_POST["user"];
	$password = $_POST["pw"];
	
	
	/*
	$userID = 10;
	$userName = "g";
	$password = "g";
	*/

	
	$result = mysqli_query($db->gCon, "SELECT * FROM users WHERE userID = $userID");
	if ($result->num_rows > 0) {
		$row = $result->fetch_assoc();
		if(strcmp($userName, $row["userName"]) != 0 | strcmp($password, $row["pWord"]) != 0 ){
			echo "User Name or Password is incorrect";
			exit();
		}

		$image = file_get_contents("C:/wamp64/www/webprogramming/sqlstuff/sqlConnect/profilePics/".$userID.".jpeg");
		$imageBase64 = base64_encode($image);
		echo $row["userID"]. "uniqueDelimeter" . $row["userName"]. "uniqueDelimeter" . $row["pWord"]. "uniqueDelimeter" . $row["gender"]. "uniqueDelimeter" . $row["age"]. "uniqueDelimeter" . $row["nativeLanguage"]. "uniqueDelimeter" . $row["bio"]. "uniqueDelimeter" . $imageBase64;
	  
	} else {
		echo "no result";
	}

?>