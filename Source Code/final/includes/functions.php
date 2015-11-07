<?php

//header redirect
function redirect_to($location=NULL)
{
	if($location !=NULL)
	{
		header("Location: {$location}");
		exit;
	}
}

//autoload class
function __autoload($class_name)
{
	$class_name=strtolower($class_name);
	$path=LIB_PATH.DS."{$class_name}.php";
	if(file_exists($path))
	{
		require_once($path);
	}
	else
	{
		die("the file ".$class_name.".php could not be found");
	}
}
function display_success($success, $errors, $data=array(), $objects=array()){
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
	echo ",";
	echo "\"data\":[";
	$i=0;
	foreach($objects as $object){
		if($i!=0)
			echo ",";
		echo "{";
		$j=0;
		foreach ($object as $key => $value) {
			if($j!=0)
				echo ",";
			echo "\"".$key."\":\"".$value."\"";
			$j++;
		}
		echo "}";
		$i++;
	}
	echo ']';
	echo '}';
}
?>