<?php
$success=false;
$errors=array();
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	if(!isset($_POST['circle_id'])){
		array_push($errors, "Error 0x01 editing circle.");
	}else{
		$circle=Circle::find_by_id($_POST['circle_id']);
		if($circle->user_id==$session->user_id);
			$circle->circle_name=$_POST['circle_name'];
		if($circle->update())
			$success=true;
	}
}
?>
<?php
display_success($success, $errors);
?>