import { formError } from "./form_error.js"
import { formLoading } from "./form_loading.js"

export function signinForm() {
	return `
		<div class="form-wrapper" id="signin-form-wrapper">
			<h1>Sign in</h1>
			<form class="form" id="signin-form" action="/api/signin" method="post">
				<div>
					<label for="signin-username">Username</label><br>
					<input type="text" class="form-input" id="signin-username" name="user" required autofocus><br>
				</div>
				<div>
					<label for="signin-password">Password</label><br>
					<input type="password" class="form-input" id="signin-password" name="pass" required>
				</div>
			</form>
			<input type="submit" value="Sign in" class="button" id="signin-submit-btn" form="signin-form">
			${formError("signin-error")}
			${formLoading("signin-loading")}
			<div class="link-button-wrapper">
				<button type="button" class="link-button" id="signup-link">No account? Sign up</button>
				<button type="button" class="link-button" id="forgot-pass-link">Forgot password</button>
			</div>
		</div>
	`
}
