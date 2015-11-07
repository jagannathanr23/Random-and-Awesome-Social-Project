<?php
$success=false;
$errors=array();
require('includes/initialize.php');
if($session->is_logged_in()){
	//already logged in
	$session->logout();
}
if(isset($_POST['email']) && isset($_POST['pwd'])){
	if(User::authenticate_user($_POST['email'], $_POST['pwd'])){
		$user=User::find_by_email($_POST['email']);
		if($user->is_activated($_POST['email'])){
			$session->login($user);
			$success=true;
		}
		else{
		array_push($errors,'Please activate your email account.');
		}
}
	else{
		array_push($errors, 'Invalid username and password combination.');
	}
}
?>
<?php
display_success($success, $errors);
?>