import { notFound } from "../controllers/utils.js";
import { dbQuery } from "../services/db.js";

export async function updateLike(imageId, userId, isLike) {
	let query;
	if (isLike) {
		query = `
			INSERT INTO likes (image_id, user_id)
			VALUES ($1, $2)
			ON CONFLICT (image_id, user_id) DO NOTHING
		`;
	} else {
		query = `
			DELETE FROM likes
			WHERE image_id = $1 AND user_id = $2
		`;
	}

	try {
		await dbQuery(query, [imageId, userId]);
	} catch (e) {
		if (e.code === "23503") {
			throw notFound(`image or user does not exist`);
		}
		throw e;
	}

	const countQuery = `
		SELECT COUNT(*)::int AS "likeCount"
		FROM likes
		WHERE image_id = $1
	`;
	const result = await dbQuery(countQuery, [imageId]);
	return {
		like: isLike,
		likeCount: result.rows[0].likeCount
	};
}
