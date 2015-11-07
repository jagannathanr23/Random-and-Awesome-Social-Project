<?php
$success=false;
$errors=array();
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	if(!isset($_GET['comment_id'])){
		array_push($errors, "Error 0x01 deleting comment.");
	}else{
		$comment=Comment::find_by_id($_GET['comment_id']);
		if($session->user_id==$comment->user_id){
			if($comment->delete()){
				$success=true;
			}
		}
	}
}
?>
<?php
display_success($success, $errors);
?>