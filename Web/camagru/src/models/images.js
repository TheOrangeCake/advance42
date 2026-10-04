import { conflict } from "../controllers/utils.js";
import { dbQuery } from "../services/db.js";

export async function persistImage(fileName, userId) {
	const query = `
		INSERT INTO images (user_id, filename)
		VALUES ($1, $2)
		RETURNING id
	`;

	let result;
	try {
		result = await dbQuery(query, [userId, fileName]);
	} catch (e) {
		if (e.code === "23505") {
			throw conflict(`filename ${fileName} already exist`);
		}
		throw e;
	}
	return result.rows[0].id;
}
