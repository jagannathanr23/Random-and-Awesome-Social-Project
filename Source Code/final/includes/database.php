<?php
class Database{

	public $connection;
	public $last_query;
	private $magic_quotes_active;
	private $real_escape_string_exists;

	function __construct()
	{
		$this->open_connection();
		$this->magic_quotes_active=get_magic_quotes_gpc();
		$this->real_escape_string_exists= function_exists("mysql_real_escape_string"); //php>=4.3
	}

	function open_connection(){
		if(!$this->connection = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME))
			return;
		// $result_set=mysqli_query($this->connection, "SELECT * FROM users");
		// print_r(mysqli_fetch_array($result_set));
	}

	function query($query){
		$this->last_query=$query;
		return mysqli_query($this->connection, $query);
	}

	//escape value function copied from lynda!
	public function escape_value($value)
	{
		if($this->real_escape_string_exists)
		{
			if($this->magic_quotes_active)
			{
				$value=stripslashes($value);
			}
			$value=mysqli_real_escape_string($this->connection,$value);
		}
		else
		{
			if(!$this->magic_quotes_active)
			{
				$value=addslashes($value);
			}
		}
		return $value;
	}

	public function fetch_array($result_set)
	{
		if(is_a($result_set, 'mysqli_result'))
			return mysqli_fetch_array($result_set);
		else
			return false;
	}

	public function num_rows($result_set)
	{
		return mysqli_num_rows($result_set);
	}
	
	public function insert_id()
	{
		return mysqli_insert_id($this->connection);
	}
	
	public function affected_rows()
	{
		return mysqli_affected_rows($this->connection);
	}

}
$db=new Database();
$database =& $db;
?>