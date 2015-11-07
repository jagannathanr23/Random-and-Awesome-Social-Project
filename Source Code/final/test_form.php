<!doctype html>
<html>
<head>
	<title>Test Forms</title>
</head>
<body>
	<form action="create_user.php" enctype="multipart/form-data" method="post">
		<h3>Create User</h3>
		<input type="text" name="email" placeholder="email"><br>
		<input type="text" name="name" placeholder="name"><br>
		<input type="text" name="pwd" placeholder="pwd"><br>
		<input type="text" name="bio" placeholder="bio"><br>
		<input type="text" name="dob" placeholder="dob"><br>
		<input type="text" name="interests" placeholder="interests"><br>
		<input type="file" name="Photo" size="2000000" accept="image/*"
 				placeholder="img"><br>
		<input type="submit"><br>
	</form>
	<form action="login.php" method="post">
		<h3>Login</h3>
		<input type="text" name="email" placeholder="email"><br>
		<input type="password" name="pwd" placeholder="pwd"><br>
		<input type="submit" name="submit">
	</form>
	<form action="create_circle.php" method="post">
		<h3>Create Circle</h3>
		<input type="text" name="circle_name" placeholder="circle name"/><br>
		<input type="submit" name="submit" />
	</form>
	<form action="delete_circle.php" method="post">
		<h3>Delete Circle</h3>
		<input type="text" name="circle_id" placeholder="circle_id" /><br>
		<input type="submit" name="submit" />
	</form>
	<form action="create_content.php" method="post">
		<h3>Create Content</h3>
		<input type="hidden" name="type" value="1" />
		<input type="text" name="special" placeholder="title" /><br>
		<input type="text" name="content_text" placeholder="content text" /><br>
		<input type="submit" name="submit" />
	</form>
	<form action="create_comment.php" method="post">
		<h3>Create Comment</h3>
		<input type="hidden" name="content_id" value="1" />
		<input type="text" name="text" placeholder="content text" /><br>
		<input type="submit" name="submit" />
	</form>
</body>
</html>