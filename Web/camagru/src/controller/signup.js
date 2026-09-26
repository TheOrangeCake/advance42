import { text } from "node:stream/consumers";
import { returnError } from "./utils.js";

export async function signupHandler(req, res) {
	const url = req.url
	const method = req.method;
	console.log(`${method} ${url}`)

	if (method.toLowerCase() !== "post") {
		res.setHeader('Allow', 'POST')
		returnError(res, 405, "Only accept POST method");
		return;
	}

	const params = new URLSearchParams(await text(req));
	const user = params.get("user").trim();
	const email = params.get("email").trim();
	const pass = params.get("pass");
	const passConfirm = params.get("passConfirm");

	// input validation
	try {
		validateInput(user, email, pass), passConfirm;
	} catch (e) {
		returnError(res, 400, e.message);
		return;
	}

	// password hash

	
	// generate token
	// store in db
	// return result
	
}

function validateInput(username, email, pass, passConfirm) {
	const USERNAME_REGEX = /^[\w ]{3,20}$/;
	const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$/;

	if (!username || !email || !pass || !passConfirm) {
		throw new Error("Empty field(s)");
	}
	if (!USERNAME_REGEX.test(username)) {
		throw new Error("Username must be between 3 - 20 characters, only alphanumeric and _ characters");
	}
	if (!EMAIL_REGEX.test(email)) {
		throw new Error("Invalid email address");
	}
	if (!PASSWORD_REGEX.test(pass)) {
		throw new Error("Password must be mininum 8 characters, 1 lower case, 1 upper case and 1 special character");
	}
	if (passConfirm !== pass) {
		throw new Error("Password confirmation doesn't match");
	}
}
