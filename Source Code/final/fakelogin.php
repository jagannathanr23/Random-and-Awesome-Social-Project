<?php
session_start();
$_SESSION['logged_in']=true;
$_SESSION['user_id']=1;
echo '{"success":true,"errors":{"0":""},"data":[]}';
?>