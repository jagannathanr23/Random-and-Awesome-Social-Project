<?php

class Session{

	public $logged_in=false;
	public $user_id;

	function __construct()
	{
		session_start();
		//$this->check_message();
		$this->check_login();
		if($this->logged_in)
		{
		}
		else
		{
		}
	}

	public function is_logged_in()
	{
		return $this->logged_in;
	}

	public function login($user)
	{
		if($user)
		{
			$this->user_id=$_SESSION['user_id']=$user->user_id;
			$this->logged_in=true;
		}
	}

	public function logout()
	{
		if(isset($this->user_id))
		{
		unset($_SESSION['user_id']);
		unset($this->user_id);
		$this->logged_in=false;
		}
	}

	private function check_login()
	{
		if(isset($_SESSION['user_id']))
		{
			$this->user_id=$_SESSION['user_id'];
			$this->logged_in=true;
		}
		else
		{
		unset($this->user_id);
		$this->logged_in=false;
		}
	}

}

$session=new Session();

?>