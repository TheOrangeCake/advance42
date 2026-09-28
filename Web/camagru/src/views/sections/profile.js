import { formError } from "../components/form_error.js"
import { formLoading } from "../components/form_loading.js"

export function profile(username, email) {
	return (
		`<section id="body-wrapper">
			<div class="form-wrapper" id="profile-form-wrapper">
				<h1>Profile</h1>
				<form class="form" id="profile-form" action="/api/profile" method="post">
					<fieldset class="profile-group">
						<legend>Account</legend>
						<div class="profile-field">
							<label for="profile-username">Username</label>
							<input type="text" class="form-input" id="profile-username" name="user" value="${username}" autocomplete="username">
						</div>
						<div class="profile-field">
							<label for="profile-email">Email</label>
							<input type="email" class="form-input" id="profile-email" name="email" value="${email}" autocomplete="email">
						</div>
					</fieldset>
					<fieldset class="profile-group">
						<legend>Change password</legend>
						<p class="profile-hint">Leave blank to keep your current password.</p>
						<div class="profile-field">
							<label for="profile-new-password">New password</label>
							<input type="password" class="form-input" id="profile-new-password" name="newPass" autocomplete="new-password">
						</div>
						<div class="profile-field">
							<label for="profile-new-password-confirm">Confirm new password</label>
							<input type="password" class="form-input" id="profile-new-password-confirm" name="newPassConfirm" autocomplete="new-password">
						</div>
					</fieldset>
					<fieldset class="profile-group profile-group-confirm">
						<legend>Current password</legend>
						<div class="profile-field">
							<label for="profile-password">Required to save any change</label>
							<input type="password" class="form-input" id="profile-password" name="pass" autocomplete="current-password" required>
						</div>
					</fieldset>
				</form>
				${formError("profile-error")}
				${formLoading("profile-loading")}
				<input type="submit" value="Save changes" class="button" id="profile-submit-btn" form="profile-form">
			</div>
		</section>`
	)
}
