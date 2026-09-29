import { text } from "node:stream/consumers";
import { returnError, escapeHtml } from "./utils.js";
import { profile } from "../views/sections/profile.js";
import { layout } from "../views/layout.js";
import { checkPassword, updateProfile, deleteExpiredUnverified } from "../models/user.js";
import { updateUserSessions, destroyUserSessions } from "../services/session.js";

const USERNAME_REGEX = /^[\w ]{3,20}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,72}$/;

export function profilePageHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "get") {
		res.setHeader('Allow', 'GET');
		returnError(res, 405, "Only accept GET method");
		return;
	}

	if (!req.user) {
		returnError(res, 401, "User not signed in");
		return;
	}

	const { username, email } = req.user;
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout("Camagru | Profile", "/css/profile.css", "/js/profile.js", profile(escapeHtml(username), escapeHtml(email)), req.user));
}

// PATCH: only the filled fields are updated, the current password is always required
export async function modifyProfileHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "patch") {
		res.setHeader('Allow', 'PATCH');
		returnError(res, 405, "Only accept PATCH method");
		return;
	}

	if (!req.user) {
		returnError(res, 401, "User not signed in");
		return;
	}

	const params = new URLSearchParams(await text(req));
	const pass = params.get("pass");

	// input validation, unchanged values are skipped
	let fields;
	try {
		fields = parseFields(params, req.user);
	} catch (e) {
		returnError(res, 400, e.message);
		return;
	}
	if (!fields.username && !fields.email && !fields.pass) {
		returnError(res, 400, "Nothing to update");
		return;
	}
	if (!pass) {
		returnError(res, 400, "Empty current password field");
		return;
	}

	// check current password
	let valid;
	try {
		valid = await checkPassword(req.user.id, pass);
	} catch (e) {
		console.error(e.message);
		returnError(res, 500, "Something went wrong in the server");
		return;
	}
	if (!valid) {
		returnError(res, 401, "Wrong password");
		return;
	}

	// clean db from expired non activated users
	if (fields.email) {
		try {
			await deleteExpiredUnverified();
		} catch (e) {
			console.error("Error clearing expired signups:", e);
		}
	}

	try {
		await updateProfile(req.user.id, fields);
	} catch (e) {
		if (e.status === 409) {
			returnError(res, 409, e.message);
			return;
		}
		console.error(e.message);
		returnError(res, 500, "Something went wrong in the server");
		return;
	}

	const { username, email } = fields;
	const changed = {};
	if (username) {
		changed.username = username;
	}
	if (email) {
		changed.email = email;
	}
	updateUserSessions(req.user.id, changed);
	if (fields.pass) {
		destroyUserSessions(req.user.id, req.sessionId);
	}

	res.statusCode = 200;
	res.setHeader('Content-type', 'text/plain; charset=utf-8');
	res.end("OK, profile updated successfully");
}

function parseFields(params, user) {
	const fields = {};

	const username = params.get("user")?.trim();
	if (username && username !== user.username) {
		if (!USERNAME_REGEX.test(username)) {
			throw new Error("Username must be between 3 - 20 characters, only alphanumeric, space and _ characters");
		}
		fields.username = username;
	}

	const email = params.get("email")?.trim().toLowerCase();
	if (email && email !== user.email) {
		if (!EMAIL_REGEX.test(email)) {
			throw new Error("Invalid email address");
		}
		fields.email = email;
	}

	const newPass = params.get("newPass");
	const newPassConfirm = params.get("newPassConfirm");
	if (newPass || newPassConfirm) {
		if (!PASSWORD_REGEX.test(newPass ?? "")) {
			throw new Error("Password must be between 8 - 72 characters, with 1 lower case, 1 upper case and 1 special character");
		}
		if (newPassConfirm !== newPass) {
			throw new Error("Password confirmation doesn't match");
		}
		fields.pass = newPass;
	}

	return fields;
}
