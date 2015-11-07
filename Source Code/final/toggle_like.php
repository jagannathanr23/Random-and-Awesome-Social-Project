<?php
$success=false;
$errors=array();
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	if(!isset($_GET['content_id'])){
		array_push($errors, "Error 0x01 liking.");
	}else{
		$result_arr=$database->query("SELECT * FROM likes WHERE content_id=".$_GET['content_id']." AND user_id=".$session->user_id);
		if($result=$database->fetch_array($result_arr)){
			if($database->query("DELETE FROM likes WHERE user_id=".$database->escape_value($session->user_id)." AND content_id=".$database->escape_value($_GET['content_id']))){
				$success=true;
			}
		}else{
			if($database->query("INSERT INTO likes VALUES(NULL, ".$database->escape_value($session->user_id).", ".$_GET['content_id'].")")){
				$success=true;
			}
		}
	}
}
?>
<?php
display_success($success, $errors);
?>