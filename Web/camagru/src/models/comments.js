import { dbQuery } from "../services/db.js";
import { dateFormat, notFound } from "../controllers/utils.js";

export async function getCommentsByImageIds(imageIds) {
	if (imageIds.length === 0) {
		return [];
	}

	const query = `
		SELECT
			c.id,
			c.image_id AS "imageId",
			c.comment,
			c.created_at AS "createdAt",
			u.username
		FROM comments c
		JOIN users u ON u.id = c.user_id
		WHERE c.image_id = ANY($1)
		ORDER BY c.created_at, c.id
	`;

	const result = await dbQuery(query, [imageIds]);
	return result.rows;
}

export async function insertComment(imageId, comment, user) {
	const query = `
		INSERT INTO comments (image_id, comment, user_id)
		VALUES ($1, $2, $3)
		RETURNING created_at AS "createdAt"
	`;

	let res1;
	try {
		res1 = await dbQuery(query, [imageId, comment, user.id]);
	} catch (e) {
		if (e.code === "23503") {
			throw notFound(`image or user does not exist`);
		}
		throw e;
	}

	const countQuery = `
		SELECT COUNT(*)::int AS "commentCount"
		FROM comments
		WHERE image_id = $1
	`;
	const res2 = await dbQuery(countQuery, [imageId]);

	return {
		username: user.username,
		comment,
		createdAt: dateFormat.format(res1.rows[0].createdAt),
		commentCount: res2.rows[0].commentCount
	};
}
