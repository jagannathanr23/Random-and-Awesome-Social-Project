<?php
//inheritance avoided due to late static binding.
class User{

	static $table_name="users";
	protected static $db_fields=array('user_id','email','pwd','name', 'dob', 'interests','bio','img','activation','salt','img_path');

	public $user_id;
	public $email;
	public $pwd;
	public $name;
	public $dob;
	public $interests;
	public $bio;
	public $img;
	public $activation;
	public $salt;
	public $img_path;
	public $pwd_hash;

	//check login credentials
	public static function authenticate_user($email, $pwd){
		global $database;
		
		$success=0;
		$userobj=new User;
		
		$salt_test=$userobj->retrieve_salt($email);
		
		$pwd_og_hash=$userobj->retrieve_pwd_hash($email);
		
		$pwd_entered_hash=$userobj->better_crypt($pwd, $salt_test);
		
		if($pwd_og_hash==$pwd_entered_hash)
		{
			$success=1;
		}
		
		
		/*$sql="SELECT COUNT(*) FROM users where email='".$email."' AND pwd='".md5($pwd)."' LIMIT 1";
		$result_set=$database->query($sql);
		$result=$database->fetch_array($result_set);
		if($result[0]!=0){
			return true;
		}else{
			return false;
		}*/
		
		if($success==1)
		{return true;}
		else 
		{return false;}
		}

	public static function email_exists($email_id){
		global $database;
		$sql="SELECT COUNT(*) FROM ".self::$table_name." WHERE email='".$email_id."' LIMIT 1";
		$result_set=$database->query($sql);
		$result=$database->fetch_array($result_set);
		if($result[0]!=0){
			return true;
		}else{
			return false;
		}
	}

	public static function find_by_email($email){
		global $database;
		$result_array=self::find_by_sql("SELECT * FROM ".self::$table_name." WHERE email='".$database->escape_value($email)."' LIMIT 1");
		return !empty($result_array) ? array_shift($result_array) :false;
	}

	public static function find_by_id($id=0)
	{
		global $database;	
		$result_array=self::find_by_sql("SELECT * FROM ".self::$table_name." WHERE user_id=".$database->escape_value($id)." LIMIT 1");
		return !empty($result_array) ? array_shift($result_array) :false;
	}
	
	public function delete()
	{
		global $database;
		$sql="DELETE FROM ".self::$table_name." ";
		$sql.="WHERE user_id=".$database->escape_value($this->user_id);
		$sql.=" LIMIT 1";
		$database->query($sql);
		return ($database->affected_rows()==1)?true:false;
	}

	public function update()
	{
		global $database;
		$attributes=$this->sanitised_attributes();
		$attribute_pairs=array();
		foreach($attributes as $key=>$value)
		{
			$attribute_pairs[]="{$key} = '{$value}' ";
		}
		$sql= "UPDATE ".self::$table_name." SET ";
		$sql.=join(", ",$attribute_pairs);
		$sql.=" WHERE user_id=".$database->escape_value($this->user_id);
		$database->query($sql);
		return ($database->affected_rows()==1)?true:false;
	}

	public static function find_users_in_circle($circle_id){
		global $database;
		return self::find_by_sql("SELECT * FROM ".self::$table_name." NATURAL JOIN membership WHERE circle_id=".$database->escape_value($circle_id));
	}

	public static function find_by_search_term($search_term){
		global $database;
		$query="SELECT * FROM ".self::$table_name." WHERE email='".$database->escape_value($search_term)."'";
		$query.=" OR name LIKE '%".$database->escape_value($search_term)."%'";
		$query.=" OR interests LIKE '%".$database->escape_value($search_term)."%'";
		$query.=" OR bio LIKE '%".$database->escape_value($search_term)."%'";
		return self::find_by_sql($query);
	}

	public function remove_from_circles($circle_ids){
		global $database;
		$query="DELETE FROM membership WHERE user_id=".$database->escape_value($this->user_id)." AND circle_id IN (";
		$i=0;
		foreach($circle_ids as $circle_id){
			if($i!=0)
				$query.= ", ";
			$query.=$circle_id;
			$i++;
		}
		$query.=")";
		return $database->query($query);
	}
	public function add_to_circles($circle_ids){
		global $database;
		foreach($circle_ids as $circle_id){
			$query="INSERT INTO membership VALUES(NULL, ".$database->escape_value($this->user_id).", ".$database->escape_value($circle_id).", NULL)";
			$database->query($query);
		}
		return true;
	}

	//mostly common bunch of functions
	public static function instantiate($record)
	{
		$object=new self;
		foreach($record as $attribute=>$value)
		{
			if($object->has_attribute($attribute))
			{
				$object->$attribute=$value;
			}
		}
		return $object;
	}

	public static function find_by_sql($sql="")
	{
		global $database;
		$result_set=$database->query($sql);
		$object_array=array();
		while($row=$database->fetch_array($result_set))
		{
			$object_array[]=self::instantiate($row);
		}
		return $object_array;
	}

	public static function find_all()
	{
		return self::find_by_sql("SELECT * FROM ".self::$table_name);
	}

	public function create(){
		global $database;
	
		//password-hashing using blowfish
		$salt=$this->generate_salt();
		$pwd_hash=$this->better_crypt($_POST['pwd'],$salt);
		$this->pwd=$pwd_hash;
		$this->salt=$salt;

		//Generate 32-bit MD5 hash.
		$activation=md5(uniqid(rand(), true));
		$this->activation = $activation;
		
		//upload image
		$this->upload_image();

	
		$attributes=$this->sanitised_attributes();
		$sql="INSERT INTO ".self::$table_name." (";
		$sql.= join(", ",array_keys($attributes));
		$sql.=") VALUES (' ";
		$sql.= join("', '", array_values($attributes));
		$sql.="')";
		$sql=str_replace("' '", "NULL", $sql);
		if($database->query($sql))
		{
			$this->id=$database->insert_id();
			$email=$this->email;
			$this->sendmail($email);
			return true;
		}
		else
		{
			return false;
		}
	}

	protected function attributes()
	{
		$attributes=array();
		foreach(self::$db_fields as $field)
		{
			$attributes[$field]=$this->$field;
		}
		return $attributes;
	}

	protected function sanitised_attributes()
	{
		global $database;
		$clean_attributes=array();
		foreach($this->attributes() as $key=>$value)
		{
			$clean_attributes[$key]=$database->escape_value($value);
		}
		return $clean_attributes;
	}

	private function has_attribute($attribute)
	{
		//returns an associative array with all attributes
		$object_vars=get_object_vars($this);
		//return true or false
		return array_key_exists($attribute,$object_vars);
	}
	
	public function is_activated($email)
	{
		global $database;
		$sql="SELECT activation FROM ".self::$table_name." WHERE email='".$email."' LIMIT 1";
		$result_set=$database->query($sql);
		$result=$database->fetch_array($result_set);
		if($result[0]==1){
			return true;
		}else{
			return false;
		}
	}

	public function sendmail($email)
	{
		$message = " To activate your account, please click on this link:\n\n";
		$message .=  'http://rasp.jatinhariani.com/final/activate.php?email=' . urlencode($email) . "&activation=$this->activation";
		if(mail($email, 'Registration Confirmation', $message))
			return true;
		else
			return false;
	}
	
	public function update_activation($email, $key){
		global $database;
		$sql="UPDATE ".self::$table_name." ";
		$sql.="SET activation='1'"." WHERE email='".$database->escape_value($email)."' AND activation='".$database->escape_value($key)."' LIMIT 1";
		$database->query($sql);
		return ($database->affected_rows()==1)?true:false;
		}
		
	public function generate_salt()
		{
		$salt = "";
    $salt_chars = array_merge(range('A','Z'), range('a','z'), range(0,9));
    for($i=0; $i < 22; $i++) {
      $salt .= $salt_chars[array_rand($salt_chars)];
    }	
	return $salt;
			}
	
	public function better_crypt($input, $salt){
return hash('sha512',$input.$salt);
}

public function retrieve_salt($email){
global $database;
		$sql="SELECT salt FROM ".self::$table_name." WHERE email='".$email."' LIMIT 1";
		$result_set=$database->query($sql);
		$result=$database->fetch_array($result_set);
		//echo $result[0];
		return $result[0];
}

public function retrieve_pwd_hash($email){
global $database;
		$sql="SELECT pwd FROM ".self::$table_name." WHERE email='".$email."' LIMIT 1";
		$result_set=$database->query($sql);
		$result=$database->fetch_array($result_set);
		//echo $result[0];
		return $result[0];
}

public function upload_image(){
	$uploadDir = 'images/';
		$fileName = $_FILES['Photo']['name'];
		$tmpName  = $_FILES['Photo']['tmp_name'];
		$fileSize = $_FILES['Photo']['size'];
		$fileType = $_FILES['Photo']['type'];
		$img = $uploadDir . $fileName;
		//$this->img_path=$img;
		$result = move_uploaded_file($tmpName, $img);
			if (!$result) {
		echo "Error uploading file";
		exit;
		}
if(!get_magic_quotes_gpc())
{
    $fileName = addslashes($fileName);
	$img = addslashes($img);
}
$this->img=$img;
	$this->img_path=$img;
	//$this->img_path=$img;


	}
	
}
?>