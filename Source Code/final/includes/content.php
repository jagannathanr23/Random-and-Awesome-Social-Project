<?php
class Content{

	static $table_name="content";
	protected static $db_fields=array('content_id','content_text', 'user_id', 'special', 'time', 'type');
	
	public $content_id;
	public $user_id;
	public $content_text;
	public $type;
	public $time;
	public $special;

	public static function find_by_id($id=0)
	{
		global $database;	
		$result_array=self::find_by_sql("SELECT * FROM ".self::$table_name." WHERE content_id=".$database->escape_value($id)." LIMIT 1");
		return !empty($result_array) ? array_shift($result_array) :false;
	}

	public function delete()
	{
		Shared_With::delete_content($this->content_id);
		Like::delete_content($this->content_id);
		Comment::delete_content($this->content_id);
		global $database;
		$sql="DELETE FROM ".self::$table_name." ";
		$sql.="WHERE content_id=".$database->escape_value($this->content_id);
		$sql.=" LIMIT 1";
		$database->query($sql);
		return ($database->affected_rows()==1)?true:false;
	}
	
	//mostly common
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
		$this->time="CURRENT_TIMESTAMP";
		$attributes=$this->sanitised_attributes();
		$sql="INSERT INTO ".self::$table_name." (";
		$sql.= join(", ",array_keys($attributes));
		$sql.=") VALUES (' ";
		$sql.= join("', '", array_values($attributes));
		$sql.="')";
		$sql=str_replace("' '", "NULL", $sql);
		$sql=str_replace("'CURRENT_TIMESTAMP'", "CURRENT_TIMESTAMP", $sql);
		$sql=str_replace("' '", "NULL", $sql);
		if($database->query($sql))
		{
			$this->id=$database->insert_id();
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

	public static function get_feed_for_user($user_id, $limit=10, $offset=0)
	{
		global $database;
		$sql="SELECT * FROM content WHERE user_id IN(";
		$sql.="SELECT DISTINCT membership.user_id FROM membership WHERE circle_id IN (";
		$sql.="SELECT circle_id FROM circle  WHERE user_id=".$user_id."))"; 
		$sql.=" ORDER BY time DESC LIMIT ".$limit." OFFSET ".$offset;
		return self::find_by_sql($sql);
	}

	public static function get_feed_in_circle($circle_id, $limit=10, $offset=0)
	{
		global $database;
		$sql="SELECT * FROM content WHERE user_id IN(";
		$sql.="SELECT DISTINCT membership.user_id FROM membership WHERE circle_id=".$circle_id.")";
		$sql.=" ORDER BY time DESC LIMIT ".$limit." OFFSET ".$offset;
		return self::find_by_sql($sql);
	}

	public function get_like_count(){
		global $database;
		$result_arr=$database->query("SELECT COUNT(*) FROM likes WHERE content_id=".$this->content_id);
		$result=$database->fetch_array($result_arr);
		return $result[0];
	}
	public function has_liked($user_id){
		global $database;
		$result_arr=$database->query("SELECT COUNT(*) FROM likes WHERE content_id=".$this->content_id." AND user_id=".$user_id);
		$result=$database->fetch_array($result_arr);
		return $result[0];
	}
	public function get_comment_count(){
		global $database;
		$result_arr=$database->query("SELECT COUNT(*) FROM comments WHERE content_id=".$this->content_id);
		$result=$database->fetch_array($result_arr);
		return $result[0];
	}

}

//content type,pwd encryption,img resize php lib,post bio,interests;update user.
?>
