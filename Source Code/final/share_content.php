<?php
$success=false;
$errors=array();
$data=array();
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	if(!isset($_POST['content_id']) || !isset($_POST['special']) || !isset($_POST['type'])){
		array_push($errors, "Error 0x01 creating content.");
	}else{
		$content=Content::instantiate($_POST);
		$content->user_id=$session->user_id;
		if($content->create()){
			//array_push($data, $circle->circle_id);
			$success=true;
		}
	}
}
?>
<?php
display_success($success, $errors, $data);
?>