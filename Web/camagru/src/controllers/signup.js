import { text } from "node:stream/consumers";
import { returnError } from "./utils.js";
import { sendEmail } from "../services/mail.js";
import { hashPassword, createUser, generateToken } from "../models/user.js";

export async function signupHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "post") {
		res.setHeader('Allow', 'POST');
		returnError(res, 405, "Only accept POST method");
		return;
	}

	const params = new URLSearchParams(await text(req));
	const user = params.get("user")?.trim();
	const email = params.get("email")?.trim().toLocaleLowerCase();
	const pass = params.get("pass");
	const passConfirm = params.get("passConfirm");

	// input validation
	try {
		validateInput(user, email, pass, passConfirm);
	} catch (e) {
		returnError(res, 400, e.message);
		return;
	}

	// password hash
	let hashedPass;
	try {
		hashedPass = await hashPassword(pass);
	} catch (e) {
		console.error(e.message);
		returnError(res, 500, "Something went wrong in the server");
		return;
	}

	// generate token
	const token = generateToken();

	// store in db
	let userID;
	try {
		userID = await createUser(user, email, hashedPass, token);
	} catch (e) {
		if (e.status === 409) {
			returnError(res, 409, e.message);
			return;
		}
		console.error(e.message);
		returnError(res, 500, "Something went wrong in the server");
		return;
	}

	// send email
	try {
		const port = process.env.HTTP_PORT;
		const subject = "Activation Link for Camagru";
		const content = `Activate here: http://localhost:${port}/api/verify?id=${userID}&token=${token}`
		await sendEmail(email, subject, content);
	} catch (e) {
		console.error("Error while sending email:", e);
		returnError(res, 500, "Something went wrong in the server");
		return;
	}
	
	// return OK
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/plain; charset=utf-8');
	res.end("OK, please check email for activation link");
}

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
