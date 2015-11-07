<?php
$success=false;
$errors=array();
$data=array();
$objects=array();
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	$circles=Circle::find_by_user_id($session->user_id);
	foreach ($circles as $circle) {
		$circle_arr=array();
		$circle_arr['circle_id']=$circle->circle_id;
		$circle_arr['circle_name']=$circle->circle_name;
		array_push($objects, $circle_arr);
	}
	$success=true;
}
?>
<?php
display_success($success, $errors, $data, $objects);
?>