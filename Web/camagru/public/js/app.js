const modal = document.querySelector("#auth-modal")
const signinBtn = document.querySelector("#signin-btn");
const closeBtn = document.querySelector("#auth-modal-close");
const backBtn = document.querySelector("#auth-back");
const signinFormWrapper = document.querySelector("#signin-form-wrapper");
const signupFormWrapper = document.querySelector("#signup-form-wrapper");
const signupLink = document.querySelector("#signup-link");
const forgotPassFormWrapper = document.querySelector("#forgot-pass-form-wrapper");
const successFormWrapper = document.querySelector("#success-form-wrapper");
const forgotLink = document.querySelector("#forgot-pass-link");
const signupError = document.querySelector("#signup-error");
const signinError = document.querySelector("#signin-error");
const forgotError = document.querySelector("#forgot-error");
const signinLoading = document.querySelector("#signin-loading");
const signupLoading = document.querySelector("#signup-loading");
const forgotLoading = document.querySelector("#forgot-loading");
const signinSubmitBtn = document.querySelector("#signin-submit-btn");
const signupSubmitBtn = document.querySelector("#signup-submit-btn");
const forgotSubmitBtn = document.querySelector("#forgot-submit-btn");
const signoutBtn = document.querySelector("#signout-btn");


const USERNAME_REGEX = /^[\w ]{3,20}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,72}$/;

/* modal display */
function displayForm(form) {
	const hide = "none";
	const display = "flex";
	signinFormWrapper.style.display = form === "signin" ? display : hide;
	signupFormWrapper.style.display = form === "signup" ? display : hide;
	forgotPassFormWrapper.style.display = form === "forgot" ? display : hide;
	successFormWrapper.style.display = form === "success" ? display : hide;
	backBtn.style.display = (form === "signup" || form === "forgot") ? display : hide;
	if (form === "success") {
		closeBtn.focus();
	}
}

signupLink?.addEventListener("click", () => {
	displayForm("signup")
})

forgotLink?.addEventListener("click", () => {
	displayForm("forgot")
})

backBtn?.addEventListener("click", () => {
	displayForm("signin")
})


signinBtn?.addEventListener("click", () => {
	modal.showModal();
	displayStatus(null);
	displayForm("signin");
})

closeBtn?.addEventListener("click", () => {
	modal.close();
})

/* State handler */
function displayStatus(form, status = null, message = "") {
	const error = status === "error" ? message : "";
	const loading = status === "loading" ? message : "";
	const busy = status === "loading";

	signinError.textContent = form === "signin" ? error : "";
	signupError.textContent = form === "signup" ? error : "";
	forgotError.textContent = form === "forgot" ? error : "";

	signinLoading.textContent = form === "signin" ? loading : "";
	signupLoading.textContent = form === "signup" ? loading : "";
	forgotLoading.textContent = form === "forgot" ? loading : "";

	signinSubmitBtn.disabled = form === "signin" && busy;
	signupSubmitBtn.disabled = form === "signup" && busy;
	forgotSubmitBtn.disabled = form === "forgot" && busy;
}

/* signup */
const signupForm = document.querySelector("#signup-form");

signupForm?.addEventListener("submit", async (e) => {
	e.preventDefault();

	const signupFormData = new FormData(signupForm, signupSubmitBtn);
	try {
		const username = signupFormData.get("user")?.trim();
		const email = signupFormData.get("email")?.trim();
		const pass = signupFormData.get("pass");
		const passConfirm = signupFormData.get("passConfirm");
		validateSignupInput(username, email, pass, passConfirm);
		
		displayStatus("signup", "loading", "Signing you up ...");
		const response = await fetch("/api/signup", {
			method: "POST",
			body: new URLSearchParams(signupFormData),
		})
		if (!response.ok) {
			throw new Error(await response.text());
		}
		displayStatus(null);
		displayForm("success");
	} catch (err) {
		displayStatus("signup", "error", err.message);
	}
})

function validateSignupInput(username, email, pass, passConfirm) {
	if (!username || !email || !pass || !passConfirm) {
		throw new Error("Empty field(s)");
	}
	if (!USERNAME_REGEX.test(username)) {
		throw new Error("Username must be between 3 - 20 characters, only alphanumeric, space and _ characters");
	}
	if (!EMAIL_REGEX.test(email)) {
		throw new Error("Invalid email address");
	}
	if (!PASSWORD_REGEX.test(pass)) {
		throw new Error("Password must be between 8 - 72 characters, with 1 lower case, 1 upper case and 1 special character");
	}
	if (passConfirm !== pass) {
		throw new Error("Password confirmation doesn't match");
	}
}


/* signin */
const signinForm = document.querySelector("#signin-form");

signinForm?.addEventListener("submit", async (e) => {
	e.preventDefault();

	const signinFormData = new FormData(signinForm, signinSubmitBtn);
	try {
		const username = signinFormData.get("user")?.trim();
		const pass = signinFormData.get("pass");
		validateSigninInput(username, pass);
		
		displayStatus("signin", "loading", "Signing in ...");
		const response = await fetch("/api/signin", {
			method: "POST",
			body: new URLSearchParams(signinFormData),
		})
		if (!response.ok) {
			throw new Error(await response.text());
		}

		// the reset link is single-use, so don't reload it; replace() also keeps it out of history
		if (location.pathname === "/reset") {
			location.replace("/");
		} else {
			location.reload();
		}
	} catch (err) {
		displayStatus("signin", "error", err.message);
	}
})

function validateSigninInput(username, pass) {
	if (!username || !pass) {
		throw new Error("Empty field(s)");
	}
	if (!USERNAME_REGEX.test(username)) {
		throw new Error("Invalid username");
	}
	if (!PASSWORD_REGEX.test(pass)) {
		throw new Error("Invalid password");
	}
}

/* forgot password (form) */
const forgotForm = document.querySelector("#forgot-pass-form");

forgotForm?.addEventListener("submit", async (e) => {
	e.preventDefault();

	const forgotFormData = new FormData(forgotForm, forgotSubmitBtn);
	try {
		const email = forgotFormData.get("email")?.trim();
		if (!email) {
			throw new Error("Empty field(s)");
		}
		if (!EMAIL_REGEX.test(email)) {
			throw new Error("Invalid email address");
		}

		displayStatus("forgot", "loading", "Sending reset link ...");
		const response = await fetch("/api/forgot", {
			method: "POST",
			body: new URLSearchParams(forgotFormData),
		})
		if (!response.ok) {
			throw new Error(await response.text());
		}
		displayStatus(null);
		displayForm("success");
	} catch (err) {
		displayStatus("forgot", "error", err.message);
	}
})

/* reset password (page) */
const resetForm = document.querySelector("#reset-form");
const resetError = document.querySelector("#reset-error");
const resetLoading = document.querySelector("#reset-loading");
const resetSubmitBtn = document.querySelector("#reset-submit-btn");

resetForm?.addEventListener("submit", async (e) => {
	e.preventDefault();

	const resetFormData = new FormData(resetForm, resetSubmitBtn);
	resetError.textContent = "";
	try {
		const pass = resetFormData.get("pass");
		const passConfirm = resetFormData.get("passConfirm");
		validateResetInput(pass, passConfirm);

		resetLoading.textContent = "Updating password ...";
		resetSubmitBtn.disabled = true;
		const response = await fetch("/api/reset", {
			method: "POST",
			body: new URLSearchParams(resetFormData),
		})
		if (!response.ok) {
			throw new Error(await response.text());
		}
		alert(await response.text());
		location.replace("/");
	} catch (err) {
		resetError.textContent = err.message;
	} finally {
		resetLoading.textContent = "";
		resetSubmitBtn.disabled = false;
	}
})

function validateResetInput(pass, passConfirm) {
	if (!pass || !passConfirm) {
		throw new Error("Empty field(s)");
	}
	if (!PASSWORD_REGEX.test(pass)) {
		throw new Error("Password must be between 8 - 72 characters, with 1 lower case, 1 upper case and 1 special character");
	}
	if (passConfirm !== pass) {
		throw new Error("Password confirmation doesn't match");
	}
}

/* modify profile (page) */
const profileForm = document.querySelector("#profile-form");
const profileError = document.querySelector("#profile-error");
const profileLoading = document.querySelector("#profile-loading");
const profileSubmitBtn = document.querySelector("#profile-submit-btn");

profileForm?.addEventListener("submit", async (e) => {
	e.preventDefault();

	const { user, email, newPass, newPassConfirm, pass } = profileForm.elements;
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
		document.querySelectorAll('a[href="/profile"]').forEach(a => a.textContent = newUser);
		profileForm.reset();
	} catch (err) {
		profileError.textContent = err.message;
	} finally {
		profileLoading.textContent = "";
		profileSubmitBtn.disabled = false;
	}
})

/* signout */
signoutBtn?.addEventListener("click", async () => {
	try {
		const response = await fetch("/api/signout", {
			method: "POST",
		})
		if (!response.ok) {
			throw new Error(await response.text());
		}
		location.assign("/");
	} catch (err) {
		alert(err.message);
	}
})
