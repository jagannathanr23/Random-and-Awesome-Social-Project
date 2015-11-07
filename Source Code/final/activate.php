<?php
require('includes/initialize.php');
    $email = $_GET['email'];
    $key = $_GET['activation'];
if (isset($email) && isset($key))
{
    $user=User::find_by_email($email);
    if($user->update_activation($email, $key)){
        echo 'Your account is now active. You may now Log in from the app.';
    }
	
	else {
        echo 'Oops !Your account could not be activated. Please recheck the link or contact the system administrator.';
    }
} 
else {
        echo 'Error Occured.';
}
?>