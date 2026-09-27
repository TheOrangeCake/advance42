export async function signinHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "post") {
		res.setHeader('Allow', 'POST');
		returnError(res, 405, "Only accept POST method");
		return;
	}

	const params = new URLSearchParams(await text(req));
	const user = params.get("user")?.trim();
	const pass = params.get("pass");

	// input validation
	try {
		validateInput(user, pass);
	} catch (e) {
		returnError(res, 400, e.message);
		return;
	}

	// compare with db (match username, match password, check active)
	// add to session table
	// return ok with session cookie
}

function validateInput(username, pass) {
	const USERNAME_REGEX = /^[\w ]{3,20}$/;
	const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,72}$/;

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
