<?php
class Shared_With{
	static $table_name="shared_with";
	protected static $db_fields=array('shared_with_id', 'circle_id', 'content_id');
	public $circle_id;
	public $shared_with_id;
	public $content_id;

	public static function delete_all_from_circle($circle_id){
		global $database;
		$sql="DELETE FROM ".self::$table_name." ";
		$sql.="WHERE circle_id=".$database->escape_value($circle_id);
		return $database->query($sql);
	}
	public static function delete_content($content_id){
		global $database;
		$sql="DELETE FROM ".self::$table_name." ";
		$sql.="WHERE content_id=".$database->escape_value($content_id);
		return $database->query($sql);
	}
}
?>