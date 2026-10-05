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

export async function getAllImageByUser(userId) {
	const query = `
		SELECT id, filename
		FROM images
		WHERE user_id = $1
		ORDER BY created_at desc
	`;

	const found = await dbQuery(query, [userId]);
	return found.rows;
}

export async function deleteImageById(imageId, userId) {
	const query = `
		DELETE FROM images
		WHERE id = $1 AND user_id = $2
		RETURNING filename
	`;

	const result = await dbQuery(query, [imageId, userId]);
	if (result.rowCount === 0) {
		return null;
	}
	return {file: result.rows[0].filename};
}

const ITEM_PER_PAGE = 5;
export async function getImagesByPage(page) {
	const offset = (page - 1) * ITEM_PER_PAGE;
	const query = `
		SELECT id, filename
		FROM images
		ORDER BY created_at desc, id desc
		LIMIT $1
		OFFSET $2
	`;

	const result = await dbQuery(query, [ITEM_PER_PAGE + 1, offset]);
	let hasNext = false;
	let rows = result.rows;
	if (result.rowCount > ITEM_PER_PAGE) {
		hasNext = true;
		rows = rows.slice(0, ITEM_PER_PAGE);
	}
	return {images: rows, hasNext};
}
