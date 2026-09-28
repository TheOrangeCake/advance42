import { authenticateUser } from "../models/user.js";
import { returnError } from "./utils.js";
import { createSession, destroySession } from "../services/session.js";
import { text } from "node:stream/consumers";

const COOKIE_EXPIRE= 7 * 24 * 60 * 60;  // 7 days

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

	// sign in check
	let result;
	try {
		result = await authenticateUser(user, pass);
	} catch (e) {
		console.error(e.message);
		returnError(res, 500, "Something went wrong in the server");
		return;
	}

	if (result.status === "invalid") {
		returnError(res, 401, "Wrong credential");
		return;
	}
	if (result.status === "inactive") {
		returnError(res, 403, "Active your account or sign up again");
		return;
	}
	
	// add to session table
	destroySession(req.sessionId);
	const sessionId = createSession(result.user);

	// return ok with session cookie
	res.statusCode = 200;
	res.setHeader("Set-Cookie", `sessionId=${sessionId}; HttpOnly; SameSite=Strict; Path=/; Max-Age=${COOKIE_EXPIRE}`)
	res.setHeader('Content-type', 'text/plain; charset=utf-8');
	res.end(`${result.user.username}`);
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
