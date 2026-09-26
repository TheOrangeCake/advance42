import { text } from "node:stream/consumers";
import { returnError } from "./utils.js";
import bcrypt from "bcrypt";
import crypto from "node:crypto";
import { dbQuery } from "../models/db.js";

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
	const token = crypto.randomBytes(32).toString("hex");

	// store in db
	try {
		await insertUser(user, email, hashedPass, token);
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

	// return result
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

async function hashPassword(pass) {
	const saltRounds = 12;
	return bcrypt.hash(pass, saltRounds);
}

async function insertUser(username, email, hashedPass, token) {
	const query = `
		INSERT INTO users (username, email, password, mail_token, mail_token_exp)
		VALUES ($1, $2, $3, $4, now() + interval '5 minutes')
		ON CONFLICT (email) DO UPDATE
			SET username = EXCLUDED.username,
				password = EXCLUDED.password,
				mail_token = EXCLUDED.mail_token,
				mail_token_exp = EXCLUDED.mail_token_exp,
				created_at = now()
			WHERE users.active = FALSE
		RETURNING id`;

	let result;
	try {
		result = await dbQuery(query, [username, email, hashedPass, token]);
	} catch (e) {
		// 23505 is UNIQUE constraint violation
		if (e.code === "23505" && e.constraint === "users_username_key") {
			throw conflict("Username already taken");
		}
		throw e;
	}

	if (result.rowCount === 0) {
		throw conflict("Email already registered");
	}
	return result.rows[0].id;
}

function conflict(message) {
	const err = new Error(message);
	err.status = 409;
	return err;
}
