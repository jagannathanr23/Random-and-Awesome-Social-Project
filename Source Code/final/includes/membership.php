<?php
class Membership{
	static $table_name="membership";
	protected static $db_fields=array('membership_id', 'circle_id', 'user_id');
	public $circle_id;
	public $membership_id;
	public $user_id;

	public static function delete_all_from_circle($circle_id){
		global $database;
		$sql="DELETE FROM ".self::$table_name." ";
		$sql.="WHERE circle_id=".$database->escape_value($circle_id);
		return $database->query($sql);
	}

	public static function add_user_to_circle(){

	}

	public static function remove_user_from_circle(){

	}
}
?>