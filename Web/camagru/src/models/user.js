import bcrypt from "bcrypt";
import { dbQuery } from "../services/db.js";
import { conflict } from "../controllers/utils.js";
import crypto from "node:crypto";

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
