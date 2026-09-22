import { formError } from "./form_error.js"

export function signupForm() {
	return `
		<div class="form-wrapper" id="signup-form-wrapper">
			<h1>Sign up</h1>
			<form class="form" id="signup-form" action="">
				<div>
					<label for="signup-username">Username</label><br>
					<input type="text" class="form-input" id="signup-username" name="user" required><br>
				</div>
				<div>
					<label for="signup-email">Email</label><br>
					<input type="email" class="form-input" id="signup-email" name="email" required><br>
				</div>
				<div>
					<label for="signup-password">Password</label><br>
					<input type="password" class="form-input" id="signup-password" name="pass" required><br>
				</div>
				<div>
					<label for="signup-password-confirm">Confirm Password</label><br>
					<input type="password" class="form-input" id="signup-password-confirm" name="passConfirm" required><br>
				</div>
			</form>
			${formError("signup-error")}
			<input type="submit" value="Sign up" class="button" id="auth-signup-btn" form="signup-form">
		</div>
	`
}
