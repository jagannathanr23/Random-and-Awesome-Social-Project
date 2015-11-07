<?php
$success=false;
$errors=array();
$data=array();
$objects=array();
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	$users=User::find_by_sql("SELECT * FROM users WHERE user_id!=".$session->user_id." ORDER BY Rand() LIMIT 10");
	foreach($users as $user){
		$user_arr=array();
		$user_arr['user_id']=$user->user_id;
		$user_arr['name']=$user->name;
		$user_arr['dob']=$user->dob;
		$user_arr['interests']=$user->interests;
		$user_arr['bio']=$user->bio;
		$user_arr['img']=$user->img;
		$user_arr['email']=$user->email;
		array_push($objects, $user_arr);
	}
	$success=true;
}
?>
<?php
display_success($success, $errors, $data, $objects);
?>