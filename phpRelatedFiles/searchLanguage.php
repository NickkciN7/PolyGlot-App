<?php



	require 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

	//on android user must enter info for all fields so post variables will always be set(no need for ISSET)

    //storing post
	
	$language = $_POST["language"];
	//$language = "English";
	$userID = $_POST["userID"];
	

	
	$result = mysqli_query($db->gCon, "SELECT * FROM users WHERE nativeLanguage = '$language' AND userID != $userID");   //need ' ' around string!!!!!!
	if ($result->num_rows > 0) {
		
		while($row = $result->fetch_assoc()) { 
			$image = file_get_contents("C:/wamp64/www/webprogramming/sqlstuff/sqlConnect/profilePics/".$row["userID"].".jpeg");
			$imageBase64 = base64_encode($image); 

			echo $row["userID"]. "uniqueDelimeter" . $row["userName"]. "uniqueDelimeter" . $row["gender"]. "uniqueDelimeter" . $row["age"]. "uniqueDelimeter" . $row["nativeLanguage"]. "uniqueDelimeter" . $row["bio"]. "uniqueDelimeter" . $imageBase64;
			echo "uniqueLineDelimeter";
	  	}
	} else {
		echo "no result";
	}

?>