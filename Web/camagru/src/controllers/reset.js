import { text } from "node:stream/consumers";
import { returnError, parseUrl } from "./utils.js";
import { resetPassword } from "../models/user.js";
import { destroyUserSessions } from "../services/session.js";
import { layout } from "../views/layout.js";
import { reset } from "../views/sections/reset.js";

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

	// validation
	if (!parseId(rawId) || !isTokenFormat(token)) {
		returnError(res, 400, "Invalid reset link");
		return;
	}

	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout("Camagru | Reset password", "/css/reset.css", "/js/app.js", reset(rawId, token), req.user));
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

	console.log(`Password of user ${id} reset`);
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/plain; charset=utf-8');
	res.end("Password updated, you can now sign in");
}

function parseId(rawId) {
	if (!rawId || !/^\d+$/.test(rawId)) {
		return null;
	}
	const id = Number(rawId);
	if (id < 1 || id > 2147483647) {
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
