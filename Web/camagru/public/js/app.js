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

signupLink.addEventListener("click", () => {
	displayForm("signup")
})

forgotLink.addEventListener("click", () => {
	displayForm("forgot")
})

backBtn.addEventListener("click", () => {
	displayForm("signin")
})


signinBtn.addEventListener("click", () => {
	modal.showModal();
	displayStatus(null);
	displayForm("signin");
})

closeBtn.addEventListener("click", () => {
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

signupForm.addEventListener("submit", async (e) => {
	e.preventDefault();

	const signupFormData = new FormData(signupForm, signupSubmitBtn);
	try {
		const username = signupFormData.get("user").trim();
		const email = signupFormData.get("email").trim();
		const pass = signupFormData.get("pass");
		const passConfirm = signupFormData.get("passConfirm");
		validateInput(username, email, pass, passConfirm);
		
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

function validateInput(username, email, pass, passConfirm) {
	const USERNAME_REGEX = /^[\w ]{3,20}$/;
	const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,72}$/;

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

