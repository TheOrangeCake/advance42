import { gallery } from "../views/sections/gallery.js";
import { layout } from "../views/layout.js";
import { getImagesByPage } from "../models/images.js";
import { returnError, parseUrl, readJsonBody } from "./utils.js";
import { getCommentsByImageIds } from "../models/comments.js";
import { updateLike } from "../models/likes.js";

export async function galleryPageHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "get") {
		res.setHeader('Allow', 'GET');
		returnError(res, 405, "Only accept GET method");
		return;
	}
	
	const params = parseUrl(req).searchParams;
	const rawPage = params.get("page");

	let page;
	page = Math.floor(Number(rawPage));
	if (!Number.isInteger(page) || page < 1) {
		page = 1;
	}
	if (page > 9999) {
		page = 9999;
	}

	let data;
	try {
		const images = await getImagesByPage(page, req.user?.id ?? null);

		const imageIds = images.images.map(image => {
			return image.id;
		});
		const comments = await getCommentsByImageIds(imageIds);

		const commentsByImage = new Map();
		for (const comment of comments) {
			if (!commentsByImage.has(comment.imageId)) {
				commentsByImage.set(comment.imageId, []);
			}
			commentsByImage.get(comment.imageId).push(comment);
		}
		for (const image of images.images) {
			image.comments = commentsByImage.get(image.id) ?? [];
		}

		data = { images: images.images, hasNext: images.hasNext, page };
	} catch (e) {
		console.error(`Fail to retrieve gallery page ${page}: ${e.message}`);
		returnError(res, 500, "Something wrong in the server");
		return;
	}

	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout(`Camagru | Gallery ${page}`, "/css/gallery.css", "/js/gallery.js", gallery(data, req.user), req.user));
}

export async function likeHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "post") {
		res.setHeader('Allow', 'POST');
		returnError(res, 405, "Only accept POST method");
		return;
	}

	if (!req.user) {
		returnError(res, 401, "User not signed in");
		return;
	}

	try {
		const data = await readJsonBody(req);

		if (!Number.isInteger(data.id)) {
			returnError(res, 400, "Bad image id");
			return;
		}
		if (typeof data.like !== "boolean") {
			returnError(res, 400, "Bad like boolean");
			return;
		}

		try {
			const result = await updateLike(data.id, req.user.id, data.like);

			res.statusCode = 200;
			res.setHeader('Content-type', 'application/json');
			res.end(JSON.stringify(result));
		} catch (e) {
			if (e.status === 404) {
				returnError(res, 404, e.message);
				return;
			}
			console.error(`Fail to like an image: ${e.message}`);
			returnError(res, 500, "Something wrong with the server");
			return;
		}

	} catch (e) {
		returnError(res, 400, e.message);
		return;
	}

}

export async function commentHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "post") {
		res.setHeader('Allow', 'POST');
		returnError(res, 405, "Only accept POST method");
		return;
	}

	if (!req.user) {
		returnError(res, 401, "User not signed in");
		return;
	}

	res.statusCode = 200;
	res.end();
}
