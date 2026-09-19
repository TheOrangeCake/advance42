export function signupForm() {
	return `
		<div class="form-wrapper" id="signup-form-wrapper">
			<h1>Sign up</h1>
			<form class="form" id="signup-form" action="/api/signup" method="post">
				<div>
					<label for="signup-username">Username</label><br>
					<input type="text" id="signup-username" name="user" required><br>
				</div>
				<div>
					<label for="signup-email">Email</label><br>
					<input type="email" id="signup-email" name="email" required><br>
				</div>
				<div>
					<label for="signup-password">Password</label><br>
					<input type="password" id="signup-password" name="pass" required><br>
				</div>
				<div>
					<label for="signup-password-confirm">Confirm Password</label><br>
					<input type="password" id="signup-password-confirm" name="pass-conf" required><br>
				</div>
			</form>
			<input type="submit" value="Sign up" class="button" form="signup-form">
		</div>
	`
}
