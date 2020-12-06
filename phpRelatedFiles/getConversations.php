<?php



	require 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

	//on android user must enter info for all fields so post variables will always be set(no need for ISSET)

	//$userID = 9;            ///////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!change
	$userID = $_POST["userID"];

	
	
	$result = mysqli_query($db->gCon, "SELECT * FROM conversations WHERE user1 = $userID OR user2 = $userID ORDER BY lastMessageID DESC");   //need ' ' around string!!!!!!
	if ($result->num_rows > 0) {
		
		while($row = $result->fetch_assoc()) { 
			//echo $row["conversationID"]."<br>";
			$partnerID;
			if($row["user1"] == $userID){ //determine which of the users is the partner id
				$partnerID = $row["user2"];
			}
			else{
				$partnerID = $row["user1"];
			}
			//echo $partnerID.'<br>';
			//get picture of partner
			$image = file_get_contents("C:/wamp64/www/webprogramming/sqlstuff/sqlConnect/profilePics/".$partnerID.".jpeg");
			$imageBase64 = base64_encode($image);

			$lastMessageIDVar = $row['lastMessageID'];
			$messageQuery = mysqli_query($db->gCon, "SELECT message, userFrom FROM messages WHERE messageID = $lastMessageIDVar");
			$messageArray = mysqli_fetch_assoc($messageQuery);
			$message = $messageArray['message'];
			$fromU = $messageArray['userFrom'];
			$partnerOrUser; //to show who last message was from
			if($fromU == $userID){
				$partnerOrUser = "user";
			}
			else{
				$partnerOrUser = "partner";
			}
			//echo $message. " ";
			//echo $fromU;
			$partnerInfo = mysqli_query($db->gCon, "SELECT * FROM users WHERE userID = $partnerID");
			$partnerArray = mysqli_fetch_assoc($partnerInfo);
			//echo $partnerArray["userID"]." ".$partnerArray["userName"]."<br>";
			echo $message."uniqueDelimeter".$partnerArray["userID"]."uniqueDelimeter".$partnerArray["userName"]."uniqueDelimeter".$partnerArray["gender"]."uniqueDelimeter".$partnerArray["age"]."uniqueDelimeter".$partnerArray["nativeLanguage"]."uniqueDelimeter".$partnerArray["bio"]."uniqueDelimeter".$imageBase64."uniqueDelimeter".$partnerOrUser."uniqueLineDelimeter";
		}

	} else {
		echo "no result";
	}
//userID,userName,gender,age,nativeLanguage,bio
?>