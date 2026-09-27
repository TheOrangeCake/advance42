import bcrypt from "bcrypt";
import { dbQuery } from "../services/db.js";
import { conflict } from "../controllers/utils.js";
import crypto from "node:crypto";

// signup
export async function hashPassword(pass) {
	const saltRounds = 12;
	return bcrypt.hash(pass, saltRounds);
}

export function generateToken() {
	return crypto.randomBytes(32).toString("hex");
}

export async function createUser(username, email, hashedPass, token) {
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

// sign up verification
export async function verifyUser(id, token) {
	// check if user exist
	const lookupQuery = `
		SELECT active, mail_token, mail_token_exp > now() AS token_valid
		FROM users
		WHERE id = $1`;

	const found = await dbQuery(lookupQuery, [id]);
	if (found.rowCount === 0) {
		return "invalid";
	}

	const { active, mail_token, token_valid } = found.rows[0];
	if (active) {
		return "already-active";
	}

	// check if token valid
	if (!token_valid || !tokenMatches(mail_token, token)) {
		return "invalid";
	}

	const activateQuery = `
		UPDATE users
		SET active = TRUE,
			mail_token = NULL,
			mail_token_exp = NULL
		WHERE id = $1 AND active = FALSE`;

	await dbQuery(activateQuery, [id]);
	return "verified";
}

function tokenMatches(stored, given) {
	if (typeof stored !== "string" || typeof given !== "string") {
		return false;
	}
	const a = Buffer.from(stored);
	const b = Buffer.from(given);
	if (a.length !== b.length) {
		return false;
	}
	return crypto.timingSafeEqual(a, b);
}

// cleanup
export async function deleteExpiredUnverified() {
	const query = `
		DELETE FROM users
		WHERE active = FALSE
			AND mail_token_exp < now()`;

	await dbQuery(query, []);
}


//sign in
const DUMMY_HASH = "$2b$12$ZAR3MZQLwoOKlFNfv5yrpOm6x3xN4Cb9nn6DdMBSgBq8wWT7WefXK";

export async function authenticateUser(username, pass) {
	const query = `
		SELECT id, username, password, active
		FROM users
		WHERE username = $1`;

	const found = await dbQuery(query, [username]);
	if (found.rowCount === 0) {
		await bcrypt.compare(pass, DUMMY_HASH);
		return { status: "invalid" };
	}

	const { id, password, active } = found.rows[0];
	if (!await bcrypt.compare(pass, password)) {
		return { status: "invalid" };
	}
	if (!active) {
		return { status: "inactive" };
	}

	return { status: "ok", user: { id, username: found.rows[0].username } };
}
