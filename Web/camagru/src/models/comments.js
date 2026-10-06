import { dbQuery } from "../services/db.js";

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
