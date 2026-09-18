const signinModal = document.querySelector("#signin-modal")
const signinBtn = document.querySelector("#signin-btn");
const closeBtn = document.querySelector("#signin-modal-close");

signinBtn.addEventListener("click", () => {
	signinModal.showModal();
})

closeBtn.addEventListener("click", () => {
	signinModal.close();
})
