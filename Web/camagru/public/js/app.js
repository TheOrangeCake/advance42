const modal = document.querySelector("#auth-modal")
const signinBtn = document.querySelector("#signin-btn");
const closeBtn = document.querySelector("#auth-modal-close");
const backBtn = document.querySelector("#auth-back");
const signinFormWrapper = document.querySelector("#signin-form-wrapper");
const signupFormWrapper = document.querySelector("#signup-form-wrapper");
const signupLink = document.querySelector("#signup-link");
const forgotPassFormWrapper = document.querySelector("#forgot-pass-form-wrapper");
const forgotLink = document.querySelector("#forgot-pass-link");

const USERNAME_REGEX = /^[\w ]{3,20}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$/;

/* modal display */
function displayForm(form) {
	const hide = "none";
	const display = "flex";
	signinFormWrapper.style.display = form === "signin" ? display : hide;
	signupFormWrapper.style.display = form === "signup" ? display : hide;
	forgotPassFormWrapper.style.display = form === "forgot" ? display : hide;
	backBtn.style.display = (form === "signup" || form === "forgot") ? display : hide;
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
	displayForm("signin");
})

closeBtn.addEventListener("click", () => {
	modal.close();
})

/* signup */
const signupForm = document.querySelector("#signup-form");
const signupBtn = document.querySelector("#auth-signup-btn");

signupForm.addEventListener("submit", (e) => {
	e.preventDefault();

	const signupFormData = new FormData(signupForm, signupBtn);
	try {
		const username = signupFormData.get("user").trim();
		validateUsername(username);
		const email = signupFormData.get("email").trim();
		validateEmail(email);
		const pass = signupFormData.get("pass");
		validatePass(pass);
		const passConfirm = signupFormData.get("passConfirm");
		if (passConfirm !== pass) {
			throw new Error("Password confirmation doesn't match");
		}
		// then fetch with data
	} catch (err) {
		const errorField = document.querySelector("#signup-error");
		errorField.textContent = err.message;
	}
})

function validateUsername(username) {
	if (!USERNAME_REGEX.test(username)) {
		throw new Error("Username must be between 3 - 20 characters, only alphanumeric and _ characters")
	}
}

function validateEmail(email) {
	if (!EMAIL_REGEX.test(email)) {
		throw new Error("Invalid email address")
	}
}

function validatePass(pass) {
	if (!PASSWORD_REGEX.test(pass)) {
		throw new Error("Password must be mininum 8 characters, 1 lower case, 1 upper case and 1 special character")
	}
}
