<?php
$success=false;
$errors=array();
require('includes/initialize.php');
if(isset($_POST['email'])){
	if(User::email_exists( $_POST['email'] )){
		array_push($errors, 'Email already registered.');
	}
	else{
		if(strlen($_POST['pwd'])<=6){
			array_push($errors, 'Password length should be greater than 6 characters.');
		}
		else{
			$user=User::instantiate($_POST);
			if($user->create()){
				$success=true;
			}
		}
	}
}
echo '{';
echo "\"success\":";
if($success){
	echo "true";
}
else{
	echo "false";
}
echo ",";
echo "\"errors\":{";
$i=0;
foreach($errors as $error){
	if($i!=0){
		echo ",";
	}
	echo "\"".$i."\":";
	echo "\"".$error."\"";
	$i++;
}
echo '}';
echo '}';
?>