import { text } from "node:stream/consumers";
import { returnError, parseUrl, isValidId } from "../utils.js";
import { resetPassword, isResetTokenValid } from "../../models/user.js";
import { destroyUserSessions } from "../../services/session.js";
import { layout } from "../../views/layout.js";
import { reset } from "../../views/sections/reset.js";
import { returnPage } from "../page.js";

export async function resetPageHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "get") {
		res.setHeader('Allow', 'GET');
		returnError(res, 405, "Only accept GET method");
		return;
	}

	const params = parseUrl(req).searchParams;
	const rawId = params.get("id");
	const token = params.get("token");
	const id = parseId(rawId);

	// validation
	if (!id || !isTokenFormat(token)) {
		returnPage(req, res, 400, "Invalid link", "This reset link is invalid");
		return;
	}

	// check token against db so an expired or used link doesn't show the form
	let valid;
	try {
		valid = await isResetTokenValid(id, token);
	} catch (e) {
		console.error(`Error checking reset token of user ${id}:`, e);
		returnPage(req, res, 500, "Server error", "Something went wrong in the server");
		return;
	}

	if (!valid) {
		returnPage(req, res, 400, "Invalid link", "Invalid or expired reset link, please ask for a new one");
		return;
	}

	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout("Camagru | Reset password", "/css/reset.css", "/js/reset.js", reset(rawId, token), req.user));
}

// form submit from the reset page
export async function resetHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "post") {
		res.setHeader('Allow', 'POST');
		returnError(res, 405, "Only accept POST method");
		return;
	}

	const params = new URLSearchParams(await text(req));
	const id = parseId(params.get("id"));
	const token = params.get("token");
	const pass = params.get("pass");
	const passConfirm = params.get("passConfirm");

	if (!id || !isTokenFormat(token)) {
		returnError(res, 400, "Invalid reset link");
		return;
	}

	// input validation
	try {
		validateInput(pass, passConfirm);
	} catch (e) {
		returnError(res, 400, e.message);
		return;
	}

	let result;
	try {
		result = await resetPassword(id, token, pass);
	} catch (e) {
		console.error(`Error resetting password of user ${id}:`, e);
		returnError(res, 500, "Something went wrong in the server");
		return;
	}

	if (result === "invalid") {
		returnError(res, 400, "Invalid or expired reset link, please ask for a new one");
		return;
	}

	destroyUserSessions(id);

	res.statusCode = 200;
	res.setHeader('Content-type', 'text/plain; charset=utf-8');
	res.end("Password updated, you can now sign in");
}

function parseId(rawId) {
	if (!rawId || !/^\d+$/.test(rawId)) {
		return null;
	}
	const id = Number(rawId);
	if (!isValidId(id)) {
		return null;
	}
	return id;
}

function isTokenFormat(token) {
	return typeof token === "string" && /^[0-9a-f]{64}$/.test(token);
}

function validateInput(pass, passConfirm) {
	const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,72}$/;

	if (!pass || !passConfirm) {
		throw new Error("Empty field(s)");
	}
	if (!PASSWORD_REGEX.test(pass)) {
		throw new Error("Password must be between 8 - 72 characters, with 1 lower case, 1 upper case and 1 special character");
	}
	if (passConfirm !== pass) {
		throw new Error("Password confirmation doesn't match");
	}
}
