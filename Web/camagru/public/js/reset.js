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
