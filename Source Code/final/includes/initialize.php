<?php
defined('DS')?null:define('DS',DIRECTORY_SEPARATOR);
defined('LIB_PATH')?null:define('LIB_PATH', dirname(__FILE__));
defined('SITE_ROOT')?null:define('SITE_ROOT', $_SERVER['DOCUMENT_ROOT']);

//general functions
require_once(LIB_PATH.DS.'functions.php');

//essentials
require_once(LIB_PATH.DS.'config.php');
require_once(LIB_PATH.DS.'database.php');
require_once(LIB_PATH.DS.'session.php');


//objects
require_once(LIB_PATH.DS.'user.php');
require_once(LIB_PATH.DS.'circle.php');
require_once(LIB_PATH.DS.'content.php');

//relationships
require_once(LIB_PATH.DS.'membership.php');
require_once(LIB_PATH.DS.'shared_with.php');


?>