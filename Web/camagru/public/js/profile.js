/* modify profile (page) */
const profileForm = document.querySelector("#profile-form");
const profileError = document.querySelector("#profile-error");
const profileLoading = document.querySelector("#profile-loading");
const profileSubmitBtn = document.querySelector("#profile-submit-btn");

profileForm?.addEventListener("submit", async (e) => {
	e.preventDefault();

	const { user, email, notification, newPass, newPassConfirm, pass } = profileForm.elements;
	profileError.textContent = "";
	try {
		// only send what changed from the value the page was loaded with
		const body = new URLSearchParams();
		const newUser = user.value.trim();
		const newEmail = email.value.trim().toLowerCase();
		if (newUser !== user.defaultValue) {
			if (!USERNAME_REGEX.test(newUser)) {
				throw new Error("Username must be between 3 - 20 characters, only alphanumeric, space and _ characters");
			}
			body.set("user", newUser);
		}
		if (newEmail !== email.defaultValue) {
			if (!EMAIL_REGEX.test(newEmail)) {
				throw new Error("Invalid email address");
			}
			body.set("email", newEmail);
		}
		if (notification.checked !== notification.defaultChecked) {
			body.set("notification", String(notification.checked));
		}
		if (newPass.value || newPassConfirm.value) {
			validateResetInput(newPass.value, newPassConfirm.value);
			body.set("newPass", newPass.value);
			body.set("newPassConfirm", newPassConfirm.value);
		}
		if (!body.toString()) {
			throw new Error("Nothing to update");
		}
		if (!pass.value) {
			throw new Error("Empty current password field");
		}
		body.set("pass", pass.value);

		profileLoading.textContent = "Saving changes ...";
		profileSubmitBtn.disabled = true;
		const response = await fetch("/api/profile", {
			method: "PATCH",
			body,
		})
		if (!response.ok) {
			throw new Error(await response.text());
		}
		alert(await response.text());

		user.defaultValue = newUser;
		email.defaultValue = newEmail;
		notification.defaultChecked = notification.checked;
		document.querySelectorAll('a[href="/profile"]').forEach(a => a.textContent = newUser);
		profileForm.reset();
	} catch (err) {
		profileError.textContent = err.message;
	} finally {
		profileLoading.textContent = "";
		profileSubmitBtn.disabled = false;
	}
})
