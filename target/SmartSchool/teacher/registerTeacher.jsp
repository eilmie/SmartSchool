<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    // This ensures the message ONLY appears if the Servlet specifically sent it
    String error = request.getParameter("error");
    if (error != null && error.equals("fail")) {
%>
    <p style="color: red; text-align: center;">Registration failed. Please check if the email is already used or if the database is connected.</p>
<% } %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Register Teacher</title>
<link rel="stylesheet" href="../assets/css/register.css">
</head>

<body>

	<div class="container">

		<h1 class="title">CREATE ACCOUNT</h1>
		<p class="subtitle">It is quick and easy.</p>

		<div class="register-box">
			<form action="../RegisterServlet" method="post">

				<input type="text" name="TeacherName" placeholder="Teacher Name"
					required> <input type="text" name="CertNumber"
					placeholder="Certificate Number" required> 
				<input type="text" name="TeacherPhoneNum" placeholder="Phone Number" required>

				<input type="email" name="email" placeholder="Email" required>

				<div class="password-wrap">
					<input type="password" name="password" placeholder="Password"
						required> <span class="toggle-icon">👁️</span>
				</div>

				<button type="submit" class="register-btn">Register</button>

				<a href="teacherLogin.html" class="login-link">Already have an account?</a>
			</form>
		</div>
	</div>

</body>
</html>
