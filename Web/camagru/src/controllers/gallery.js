import { gallery } from "../views/sections/gallery.js";
import { layout } from "../views/layout.js";
import { getImagesByPage } from "../models/images.js";
import { returnError, parseUrl } from "./utils.js";
import { getCommentsByImageIds } from "../models/comments.js";

export async function galleryHandler(req, res) {
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
	res.end(layout(`Camagru | Gallery ${page}`, "/css/gallery.css", "/js/gallery.js", gallery(data), req.user));
}
