<?php
$success=false;
$errors=array();
$data=array();
$objects=array();
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	if(isset($_GET['user_id'])){
		$user=User::find_by_id($_GET['user_id']);
		$circles=Circle::find_by_user_id($session->user_id);
		$og_circle_ids=array();
		foreach($circles as $circle){
			array_push($og_circle_ids, $circle->circle_id);
		}
		$user->remove_from_circles($og_circle_ids);
		$user->add_to_circles($_GET['circle_ids']);
		$success=true;
	}
}
?>
<?php
display_success($success, $errors, $data, $objects);
?>