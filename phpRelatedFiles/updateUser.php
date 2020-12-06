<?php



	require 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

/*mysqli_query($db->gCon, "INSERT INTO users(userName, pWord, gender, age, nativeLanguage, bio) VALUES('was reached', 'dude', 'dude', '23', 'dude', 'dude')");*/
	$userID = $_POST["userID"];
	
	if (isset($_POST["imageData"])) {
    	$imageData = $_POST["imageData"];
    	$imageDecoded = base64_decode($imageData);
		if(file_put_contents("C:/wamp64/www/webprogramming/sqlstuff/sqlConnect/profilePics/".$userID.".jpeg", $imageDecoded)){
			echo "image: success,";
		}
		else{
			echo "image: failure,";
		}
    }  
    if (isset($_POST["user"])) {
    	$user = $_POST["user"];
    	$result = mysqli_query($db->gCon, "UPDATE users SET userName = '$user' WHERE userID = $userID");
    	if ($result) {
		    // successfully inserted into database
		    echo "username: success,";
		} 
		else {
		    // failed to insert row
		    echo "username: failure,";
		}
    }   
    if (isset($_POST["bio"])) {
    	$bio = $_POST["bio"];
    	$result = mysqli_query($db->gCon, "UPDATE users SET bio = '$bio' WHERE userID = $userID");
    	if ($result) {
		    // successfully inserted into database
		    echo "bio: success";
		} 
		else {
		    // failed to insert row
		    echo "bio: failure";
		}
    }   


?>