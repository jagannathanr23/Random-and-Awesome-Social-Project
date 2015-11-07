<?php
$success=false;
$errors=array();
$data=array();
$objects=array();
$offset=isset($_GET['offset'])?$_GET['offset']:0;
$limit=isset($_GET['limit'])?$_GET['limit']:10000;
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	$contents=null;
	$comment_arr=array();
	if(isset($_GET['content_id'])){
		$comments=Comment::find_by_sql("SELECT * FROM comments WHERE content_id=".$_GET['content_id']." ORDER BY date ASC LIMIT ".$limit." OFFSET ".$offset);
	}
	else{
		$comments=comment::get_feed_for_user($session->user_id, $limit, $offset);
	}
	foreach ($contents as $comment) {
		$comment_arr['comment_id']=$comment->comment_id;
		$comment_arr['text']=$comment->text;
		$comment_arr['date']=$comment->date;
		$comment_arr['comment_count']=$comment->get_comment_count();
		$comment_arr['user_id']=$comment->user_id;
		if($user=User::find_by_id($comment->user_id)){
				$comment_arr['img_path']="http://rasp.jatinhariani.com/final/".$user->img_path;
				$comment_arr['name']=$user->name;
				$comment_arr['img']=$user->img;
		}
		array_push($objects, $comment_arr);
	}
	$success=true;
}
?>
<?php
display_success($success, $errors, $data, $objects);
?>