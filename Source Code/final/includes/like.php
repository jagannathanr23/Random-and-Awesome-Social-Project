<?php
class Like{
	static $table_name="likes";
	protected static $db_fields=array('like_id', 'content_id', 'user_id');
	public $content_id;
	public $like_id;
	public $user_id;

	public static function delete_content($content_id){
		global $database;
		$sql="DELETE FROM ".self::$table_name." ";
		$sql.="WHERE content_id=".$database->escape_value($content_id);
		return $database->query($sql);
	}

	public static function like_toggle($content, $user){
				
	}
}
?>