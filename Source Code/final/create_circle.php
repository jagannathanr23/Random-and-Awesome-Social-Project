<?php
$success=false;
$errors=array();
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	if(!isset($_POST['circle_name'])){
		array_push($errors, "Error 0x01 creating circle.");
	}else{
		$circle=Circle::make($_POST['circle_name'], $session->user_id);
		if($circle->create()){
			$success=true;
		}
	}
}
?>
<?php
display_success($success, $errors);
?>