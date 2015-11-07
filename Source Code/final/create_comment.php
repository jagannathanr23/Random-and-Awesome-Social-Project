<?php
$success=false;
$errors=array();
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	if(isset($_POST['text']) && isset($_POST['content_id'])){
		$comment_fields['text']=$_POST['text'];
		$comment_fields['content_id']=$_POST['content_id'];
		$comment_fields['user_id']=$session->user_id;
		$comment=Comment::instantiate($comment_fields);
		if($comment->create()){
			$success=true;
		}
	}else{
		array_push($errors, "Error 0x01 creating comment.");
	}
}
?>
<?php
display_success($success, $errors);
?>