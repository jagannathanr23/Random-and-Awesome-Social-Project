<?php
class Circle{

	static $table_name="circle";
	protected static $db_fields=array('circle_id','circle_name', 'user_id');
	public $circle_id;
	public $circle_name;
	public $user_id;

	public static function make($circle_name, $user_id){
		$object=new self;
		$object->user_id=$user_id;
		$object->circle_name=$circle_name;
		return $object;
	}
	public static function find_by_id($id=0)
	{
		global $database;
		$result_array=self::find_by_sql("SELECT * FROM ".self::$table_name." WHERE circle_id=".$database->escape_value($id)." LIMIT 1");
		return !empty($result_array) ? array_shift($result_array) :false;
	}

	public static function find_by_user_id($id){
		global $database;	
		$result_array=self::find_by_sql("SELECT * FROM ".self::$table_name." WHERE user_id=".$database->escape_value($id));
		return $result_array;
	}

	public function delete()
	{
		Membership::delete_all_from_circle($this->circle_id);
		Shared_With::delete_all_from_circle($this->circle_id);
		global $database;
		$sql="DELETE FROM ".self::$table_name." ";
		$sql.="WHERE circle_id=".$database->escape_value($this->circle_id);
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
		$sql.=" WHERE circle_id=".$database->escape_value($this->circle_id);
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
		$attributes=$this->sanitised_attributes();
		$sql="INSERT INTO ".self::$table_name." (";
		$sql.= join(", ",array_keys($attributes));
		$sql.=") VALUES (' ";
		$sql.= join("', '", array_values($attributes));
		$sql.="')";
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