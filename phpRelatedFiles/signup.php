<!DOCTYPE html>
<html>
<head>
	<title></title>
	<link href="https://codd.cs.gsu.edu/~lhenry23/Asg03/nerdieluv.css" type="text/css" rel="stylesheet">
</head>

	<!--the form that will pass onto signup-submit.php-->
	<form action="signup-submit.php" method="post" enctype="multipart/form-data">
		<fieldset>
			<legend>New User Signup:</legend>
			<strong>User Name:</strong>
				<input type="text" name="user" size="25"><br><br>
			<strong>Pass Word:</strong>
				<input type="text" name="pw" size="25"><br><br>
			<strong>Gender:</strong>
				<label><input type="radio" name="gender" value="Male"> Male </label>       <!--label tag makes text clickable-->
 				<label><input type="radio" name="gender" value="Female"> Female</label> <br><br>
			<strong>Age:</strong>
				<input type="text" name="age" size="6" maxlength="2" ><br><br>
			<strong>Native Language:</strong>
				<select name="natlang"> 
				<option selected> English </option>
				<option> Spanish </option>
				<option> French </option>
				<option> German </option>
				</select> <br><br>
			<strong>BIO:</strong>
				<input type="text" name="bio" size="25"><br><br>
			<strong>Photo:</strong>
				<input type="file" name="profilePic">
			<input type="submit" value="Sign Up">
		</fieldset>
	</form>
	<br><br><br>

</body>
</html>