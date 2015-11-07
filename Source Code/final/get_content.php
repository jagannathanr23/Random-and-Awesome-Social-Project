<?php
$success=false;
$errors=array();
$data=array();
$objects=array();
$offset=isset($_GET['offset'])?$_GET['offset']:0;
$limit=isset($_GET['limit'])?$_GET['limit']:10;
require('includes/initialize.php');
if(!$session->is_logged_in()){
	array_push($errors, "Not logged in.");
}else{
	$contents=null;
	$content_arr=array();
	if(isset($_GET['circle_id'])){
		$contents=Content::get_feed_in_circle($_GET['circle_id'], $limit, $offset);
	}elseif(isset($_GET['content_id'])){
		$contents[0]=Content::find_by_id($_GET['content_id']);
	}elseif(isset($_GET['user_id'])){
		$sql="SELECT * FROM content WHERE user_id=".$_GET['user_id']." ORDER BY TIME DESC LIMIT ".$limit." OFFSET ".$offset;;
		$contents=Content::find_by_sql($sql);
	}elseif(isset($_GET['self'])){
		$sql="SELECT * FROM content WHERE user_id=".$session->user_id." ORDER BY TIME DESC LIMIT ".$limit." OFFSET ".$offset;
		$contents=Content::find_by_sql($sql);
	}
	else{
		$contents=Content::get_feed_for_user($session->user_id, $limit, $offset);
	}
	foreach ($contents as $content) {
			$content_arr['content_id']=$content->content_id;
			$content_arr['content_text']=$content->content_text;
			$content_arr['time']=$content->time;
			$content_arr['special']=$content->special;
			$content_arr['type']=$content->type;
			$content_arr['like_count']=$content->get_like_count();
			$content_arr['has_liked']=$content->has_liked($session->user_id)?1:0;
			$content_arr['comment_count']=$content->get_comment_count();
			$content_arr['user_id']=$content->user_id;
			if($user=User::find_by_id($content->user_id)){
					$content_arr['img_path']="http://rasp.jatinhariani.com/final/".$user->img_path;
					$content_arr['name']=$user->name;
					$content_arr['img']=$user->img;
			}
			array_push($objects, $content_arr);
		}
	$success=true;
}
?>
<?php
display_success($success, $errors, $data, $objects);
?>