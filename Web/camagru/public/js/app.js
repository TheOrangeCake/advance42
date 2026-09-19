const modal = document.querySelector("#auth-modal")
const signinBtn = document.querySelector("#signin-btn");
const closeBtn = document.querySelector("#auth-modal-close");
const backBtn = document.querySelector("#auth-back");
const signinForm = document.querySelector("#signin-form-wrapper");
const signupForm = document.querySelector("#signup-form-wrapper");
const signupLink = document.querySelector("#signup-link");
const forgotPassForm = document.querySelector("#forgot-pass-form-wrapper");
const forgotLink = document.querySelector("#forgot-pass-link");

function displayForm(form) {
	const hide = "none";
	const display = "flex";
	signinForm.style.display = form === "signin" ? display : hide;
	signupForm.style.display = form === "signup" ? display : hide;
	forgotPassForm.style.display = form === "forgot" ? display : hide;
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
})

closeBtn.addEventListener("click", () => {
	modal.close();
})
