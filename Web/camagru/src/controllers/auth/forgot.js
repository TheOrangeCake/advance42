import { text } from "node:stream/consumers";
import { returnError } from "../utils.js";
import { sendEmail } from "../../services/mail.js";
import { generateToken, setResetToken } from "../../models/user.js";

export async function forgotHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "post") {
		res.setHeader('Allow', 'POST');
		returnError(res, 405, "Only accept POST method");
		return;
	}

	const params = new URLSearchParams(await text(req));
	const email = params.get("email")?.trim().toLocaleLowerCase();

	// input validation
	const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	if (!email) {
		returnError(res, 400, "Empty field(s)");
		return;
	}
	if (!EMAIL_REGEX.test(email)) {
		returnError(res, 400, "Invalid email address");
		return;
	}

	// store token, only for active accounts
	const token = generateToken();
	let userID;
	try {
		userID = await setResetToken(email, token);
	} catch (e) {
		console.error(e.message);
		returnError(res, 500, "Something went wrong in the server");
		return;
	}

	// send email
	if (userID !== null) {
		try {
			const port = process.env.HTTP_PORT;
			const subject = "Reset password for Camagru";
			const content = `Reset your password here: http://localhost:${port}/reset?id=${userID}&token=${token}`
			await sendEmail(email, subject, content);
		} catch (e) {
			console.error("Error while sending email:", e);
			returnError(res, 500, "Something went wrong in the server");
			return;
		}
	}

	res.statusCode = 200;
	res.setHeader('Content-type', 'text/plain; charset=utf-8');
	res.end("If this email belongs to an account, a reset link has been sent");
}
