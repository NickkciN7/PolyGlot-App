<?php

	require 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

    
	$userID = $_POST["userID"];
	$partnerID = $_POST["partnerID"];
	$lastMessageID = $_POST["lastMessageID"];
	//*/

	/*
	$userID = 9;
	$partnerID = 3;
	$conID;
	//*/


	//user1 and user2 is not supposed to matter in terms of order for conversation ID so check if there is a conversation where the user and partner are either user1 or user2 
	$conversationQuery = mysqli_query($db->gCon, "SELECT conversationID, lastMessageID FROM conversations WHERE (user1 = $userID AND user2 = $partnerID) OR (user1 = $partnerID AND user2 = $userID)"); 

	if ($conversationQuery->num_rows == 1) {  //means there was a unique conversation already created
		$array = mysqli_fetch_assoc($conversationQuery);
		$conID = $array['conversationID']; //gets conversation id of conversation just found
		$conversationLastMessageID = $array['lastMessageID'];
		//echo $conID."<br>";
		//echo $conversationLastMessageID;

		if($conversationLastMessageID > $lastMessageID){ //means partner sent a message since the message activity was opened
			$messagesQuery = mysqli_query($db->gCon, "SELECT * FROM messages WHERE conversationID = $conID AND messageID > $lastMessageID AND userTo = $userID ORDER BY messageID ASC"); //get new messages from partner. Needed to specify that $userTo was the user signed in. There was some weird circumstance where the message just sent would show up as being from your partner as well. I think because the lastMessageId sent to this php was less than the new lastMessageID created when a new message is sent in that short ammount of time
			while($row = $messagesQuery->fetch_assoc()) { 
				echo $row["message"]."messageDel";  //echos last message sent 
	  		}
	  		echo $conversationLastMessageID;   //use to update last message ID on android
		}
		else{
			echo "no new messages";
		}

	} 
	else {
		echo "conversation not found";
	}
?>