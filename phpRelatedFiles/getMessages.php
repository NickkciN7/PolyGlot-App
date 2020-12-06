<?php



	require 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

    
	$userID = $_POST["userID"];
	$partnerID = $_POST["partnerID"];
	//*/

	/*
	$userID = 9;
	$partnerID = 3;
	$conID;
	//*/


	//user1 and user2 is not supposed to matter in terms of order for conversation ID so check if there is a conversation where the user and partner are either user1 or user2 
	$conversationQuery = mysqli_query($db->gCon, "SELECT conversationID FROM conversations WHERE (user1 = $userID AND user2 = $partnerID) OR (user1 = $partnerID AND user2 = $userID)"); 


	if ($conversationQuery->num_rows == 1) {  //means there was a unique conversation already created
		$conID = mysqli_fetch_assoc($conversationQuery)['conversationID']; //gets conversation id of conversation just found
		//echo $conID;

		$messagesQuery = mysqli_query($db->gCon, "SELECT * FROM messages WHERE conversationID = $conID ORDER BY messageID ASC"); //message must exist since conversation exists

		$lastMessage;
		while($row = $messagesQuery->fetch_assoc()) { 
			echo $row["userFrom"]."userFromDel".$row["message"]."messageDel";
			$lastMessage = $row["messageID"];
	  	}
	  	echo $lastMessage;
	} 
	else {
		echo "conversation not found";
	}
?>