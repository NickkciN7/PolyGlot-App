<?php



	require 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

    ////********ALWAYS REMEMBER YOU NEED TO CHANGE BACK TO THIS********\\\\\\\\\\
    
	$userID = $_POST["userID"];
	$partnerID = $_POST["partnerID"];
	$message = $_POST["message"];
	//*/
	$conID;
	
	/*
	$userID = 2;
	$partnerID = 9;
	$message = "trying a new line 3\n";
	//*/

	$result = mysqli_query($db->gCon, "INSERT INTO messages(userFrom, userTO, message) VALUES('$userID', '$partnerID', '$message')"); //creates message
	/*if ($result) {
	    // successfully inserted into database
	    echo "message insert: success, ";
	} 
	else {
	    // failed to insert row
	    echo "message insert: failure, ";
	}
	echo "<br>";*/

	$idQ = mysqli_query($db->gCon, "SELECT messageID FROM messages ORDER BY messageID DESC LIMIT 1"); //gives last message created id
	$messageID = strval(mysqli_fetch_assoc($idQ)['messageID']); //sets variable to the last message id given from above query
	echo $messageID;
	//echo $messageID;
	
	//user1 and user2 is not supposed to matter in terms of order for conversation ID so check if there is a conversation where the user and partner are either user1 or user2 
	$conversationQuery = mysqli_query($db->gCon, "SELECT conversationID FROM conversations WHERE (user1 = $userID AND user2 = $partnerID) OR (user1 = $partnerID AND user2 = $userID)"); 


	if ($conversationQuery->num_rows == 1) {  //means there was a unique conversation already created
		//echo "conversation id: found<br>";
		$conID = mysqli_fetch_assoc($conversationQuery)['conversationID']; //gets conversation id of conversation just found
		updateMessagesQuery(); //updates conversation ID in messages table
		//updates lastmessageID in conversation table
		$updateMessageID = mysqli_query($db->gCon, "UPDATE conversations SET lastMessageID = $messageID WHERE conversationID = $conID" );
		/*if ($updateMessageID) {
			// successfully inserted into database
			echo "last message id update: success,";
		} 
		else {
			// failed to insert row
			echo "last message id update: failure,";
		}
		echo "<br>";*/
	} 
	else {
		//echo "conversation id: not found<br>";
		$insertConversation = mysqli_query($db->gCon, "INSERT INTO conversations(user1, user2, lastMessageID) VALUES('$userID', '$partnerID', '$messageID')");
		$newConvQuery = mysqli_query($db->gCon, "SELECT conversationID FROM conversations ORDER BY conversationID DESC LIMIT 1"); //gets id of conversation just created
		$conID = mysqli_fetch_assoc($newConvQuery)['conversationID'];
		updateMessagesQuery();//updates conversation ID in messages table
	}

	

	function updateMessagesQuery(){//updates conversation ID in messages table
		global $messageID;
		global $db;
		global $conID;
		//echo $messageID;
		//echo "<br>".$conID;
		$updateConID = mysqli_query($db->gCon, "UPDATE messages SET conversationID = $conID WHERE messageID = $messageID" ); //sets conversation id of message to what was found
		/*if ($updateConID) {
			// successfully inserted into database
			echo "conID update: success,";
		} 
		else {
			// failed to insert row
			echo "conID update: failure,";
		}
		echo "<br>";*/
	}
?>