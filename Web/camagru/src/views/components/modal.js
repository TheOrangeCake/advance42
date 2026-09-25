import { signinForm } from "./signin_form.js"
import { signupForm } from "./signup_form.js"
import { forgotPassForm } from "./forgot_pass_form.js"
import { successForm } from "./success_form.js"

export function modal() {
	const signin = signinForm();
	const signup = signupForm();
	const forgot = forgotPassForm();
	const success = successForm();

	return (
		`<dialog id="auth-modal">
			<button type="button" id="auth-modal-close">close</button>
			<button type="button" id="auth-back">back</button>
			<div class="auth-form">
				${signin}
				${signup}
				${forgot}
				${success}
			</div>
		</dialog>`
	)
}
