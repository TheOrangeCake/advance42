import { formError } from "../components/form_error.js"
import { formLoading } from "../components/form_loading.js"

export function reset(id, token) {
	return (
		`<section id="body-wrapper">
			<div class="form-wrapper" id="reset-form-wrapper">
				<h1>Reset password</h1>
				<form class="form" id="reset-form" action="/api/reset" method="post">
					<input type="hidden" name="id" value="${id}">
					<input type="hidden" name="token" value="${token}">
					<div>
						<label for="reset-password">New password</label><br>
						<input type="password" class="form-input" id="reset-password" name="pass" required autofocus><br>
					</div>
					<div>
						<label for="reset-password-confirm">Confirm new password</label><br>
						<input type="password" class="form-input" id="reset-password-confirm" name="passConfirm" required><br>
					</div>
				</form>
				${formError("reset-error")}
				${formLoading("reset-loading")}
				<input type="submit" value="Reset password" class="button" id="reset-submit-btn" form="reset-form">
			</div>
		</section>`
	)
}
