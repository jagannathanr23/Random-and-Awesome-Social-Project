<?php
class Comment{

	static $table_name="comments";
	protected static $db_fields=array('comment_id','content_id', 'user_id', 'text', 'date');	
	public $comment_id;
	public $user_id;
	public $content_id;
	public $date;
	public $text;

	public static function find_by_id($id=0)
	{
		global $database;
		$result_array=self::find_by_sql("SELECT * FROM ".self::$table_name." WHERE comment_id=".$database->escape_value($id)." LIMIT 1");
		return !empty($result_array) ? array_shift($result_array) :false;
	}

	public function delete(){
		global $database;
		$sql="DELETE FROM ".self::$table_name." ";
		$sql.="WHERE comment_id=".$database->escape_value($this->comment_id);
		$sql.=" LIMIT 1";
		$database->query($sql);
		return ($database->affected_rows()==1)?true:false;
	}

	public static function delete_content($content_id){
		global $database;
		$sql="DELETE FROM ".self::$table_name." ";
		$sql.="WHERE comment_id=".$database->escape_value($content_id);
		return $database->query($sql);
	}

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
		$this->date="CURRENT_TIMESTAMP";
		$attributes=$this->sanitised_attributes();
		$sql="INSERT INTO ".self::$table_name." (";
		$sql.= join(", ",array_keys($attributes));
		$sql.=") VALUES (' ";
		$sql.= join("', '", array_values($attributes));
		$sql.="')";
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
}
?>