import { formError } from "./form_error.js"
import { formLoading } from "./form_loading.js"

export function forgotPassForm() {
	return `
		<div class="form-wrapper" id="forgot-pass-form-wrapper">
			<h1>Forgot password</h1>
			<p>You will receive an email to reset password</p>
			<form class="form" id="forgot-pass-form" action="/api/forgot" method="post">
				<div>
					<label for="forgot-pass-email">Email</label><br>
					<input type="email" class="form-input" id="forgot-pass-email" name="email" required><br>
				</div>
			</form>
			${formError("forgot-error")}
			${formLoading("forgot-loading")}
			<input type="submit" value="Reset" class="button" id="forgot-submit-btn" form="forgot-pass-form">
		</div>
	`
}
