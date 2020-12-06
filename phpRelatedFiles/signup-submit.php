<?php



	require 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

/*mysqli_query($db->gCon, "INSERT INTO users(userName, pWord, gender, age, nativeLanguage, bio) VALUES('was reached', 'dude', 'dude', '23', 'dude', 'dude')");*/
	$imageData = $_POST["imageData"];
	$user = $_POST["user"];
	$password = $_POST["pw"];
	$gender = $_POST["gender"]; 
	$age = $_POST["age"];
	$natlang = $_POST["natlang"] ;
	$bio = $_POST["bio"];

	//echo $user."<br>".$password."<br>".$gender."<br>".$age."<br>".$natlang."<br>".$bio."<br>";


	/*$password = "android reached";
	$gender = "gender";
	$age = 12;
	$natlang = "lang";
	$bio = "bio";*/

	$result = mysqli_query($db->gCon, "INSERT INTO users(userName, pWord, gender, age, nativeLanguage, bio) VALUES('$user', '$password', '$gender', '$age', '$natlang', '$bio')");

	if ($result) {
	    // successfully inserted into database
	    echo "success,";
	    //getting last id(the id of the insert done above for the image name)
		$idQ = mysqli_query($db->gCon,"SELECT userID FROM users ORDER BY userID DESC LIMIT 1");
		$id = -1; //default
		$id = strval(mysqli_fetch_assoc($idQ)['userID']); 
		$imageDecoded = base64_decode($imageData);
		if(file_put_contents("C:/wamp64/www/webprogramming/sqlstuff/sqlConnect/profilePics/".$id.".jpeg", $imageDecoded)){
			echo "success,";
		}
		else{
			echo "failure,";
		}
		echo $id;
		} 
	else {
	    // failed to insert row
	    echo "failure,";
	}
	
	

?>