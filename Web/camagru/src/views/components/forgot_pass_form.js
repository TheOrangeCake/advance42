export function forgotPassForm() {
	return `
		<div class="form-wrapper" id="forgot-pass-form-wrapper">
			<h1>Forgot password</h1>
			<p>You will receive an email to reset password</p>
			<form class="form" id="forgot-pass-form" action="/api/forgot" method="post">
				<div>
					<label for="forgot-pass-email">Email</label><br>
					<input type="email" id="forgot-pass-email" name="email" required><br>
				</div>
			</form>
			<input type="submit" value="Reset" class="button" form="forgot-pass-form">
		</div>
	`
}
