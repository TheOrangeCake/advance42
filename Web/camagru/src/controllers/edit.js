import { writeFile, unlink } from "node:fs/promises";
import path from "node:path";
import { Jimp } from "jimp";
import { edit } from "../views/sections/edit.js";
import { layout } from "../views/layout.js";
import { generateToken, returnError } from "./utils.js";
import { getAllStickerName, isStickerExist, getStickerCount, getStickerImage } from "../services/stickers.js";
import { deleteImageById, getAllImageByUser, persistImage } from "../models/images.js";

const UPLOADS_DIR = "/app/uploads/";
const UPLOADS_URL = "/uploads/";

export async function editPageHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "get") {
		res.setHeader('Allow', 'GET');
		returnError(res, 405, "Only accept GET method");
		return;
	}

	if (!req.user) {
		returnError(res, 401, "User not signed in");
		return;
	}

	let userImages;
	try {
		userImages = await getAllImageByUser(req.user.id);
	} catch (e) {
		console.error(`Fail to get user images: ${e.message}`);
	}
	
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout("Camagru | Edit", "/css/edit.css", "/js/edit.js", edit(getAllStickerName(), userImages ?? null), req.user));
}

export async function composeHandler(req, res) {
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

		if (!data.img || !data.stickers) {
			returnError(res, 400, "No image or stickers");
			return;
		}
		if (!isValidStickerList(data.stickers)) {
			returnError(res, 400, "Some sticker(s) not exist");
			return;
		}
		if (!isValidMeta(data.stickers)) {
			returnError(res, 400, "Some sticker(s) metadata aren't correct");
			return;
		}
		const image = await decodePngDataUrl(data.img);
		if (!image) {
			returnError(res, 400, "Invalid image");
			return;
		}

		const imgHeight = image.height;
		const imgWidth = image.width;
		data.stickers.forEach(sticker => {
			const stickerJIMP = getStickerImage(sticker.name);
			const stickerPosX = sticker.meta.x * imgWidth;
			const stickerPosY = sticker.meta.y * imgHeight;
			const stickerW = Math.round(sticker.meta.w * imgWidth);
			stickerJIMP.resize({ w: stickerW });
			const offsetX = Math.round(stickerPosX - (stickerJIMP.width / 2));
			const offsetY = Math.round(stickerPosY- (stickerJIMP.height / 2));
			image.composite(stickerJIMP, offsetX, offsetY);
		});

		const composedImg = await image.getBuffer("image/png");
		const filename = "compose-" + generateToken() + ".png";
		const filePath = path.join(UPLOADS_DIR, filename);
		try {
			// "wx" fails if the file already exists
			await writeFile(filePath, composedImg, { flag: "wx" });
		} catch (e) {
			console.error(`Fail to write ${filePath}: ${e.message}`);
			returnError(res, 500, "Something wrong in the server");
			return;
		}

		let imgId;
		try {
			imgId = await persistImage(filename, req.user.id);
		} catch (e) {
			console.error(e.message);
			await unlink(filePath).catch(err => console.error(`Fail to remove ${filePath}: ${err.message}`));
			returnError(res, 500, "Something wrong in the server");
			return;
		}

		res.statusCode = 201;
		res.setHeader('Content-type', 'application/json');
		res.end(JSON.stringify({id: imgId, url: UPLOADS_URL + filename}));
	} catch (e) {
		returnError(res, 400, e.message);
		return;
	}
}

const MAX_BODY_SIZE = 10 * 1024 * 1024; // 10 MB

function readJsonBody(req) {
	return new Promise((resolve, reject) => {
		let body = "";
		let size = 0;

		req.on("data", (chunk) => {
			size += chunk.length;
			if (size > MAX_BODY_SIZE) {
				req.destroy();
				reject(new Error("Payload too large"));
				return;
			}
			body += chunk;
		});

		req.on("end", () => {
		if (body === "") {
			resolve({});
			return;
		}
		try {
			resolve(JSON.parse(body));
		} catch (err) {
			reject(new Error("Invalid JSON"));
		}
		});

		req.on("error", reject);
	});
}

function isValidStickerList(stickerList) {
	if (!Array.isArray(stickerList) || stickerList.length <= 0 || stickerList.length > getStickerCount()) {
		return false;
	}
	return stickerList.every(sticker => {
		return sticker !== null && typeof sticker === "object" && isStickerExist(sticker.name);
	});
}


const MAX_WIDTH = 0.6;
const MIN_WIDTH = 0.05;
const MAX_RANGE = 1;
const MIN_RANGE = 0;
function isValidMeta(stickerList) {
	if (!Array.isArray(stickerList) || stickerList.length <= 0) {
		return false;
	}
	return stickerList.every(sticker => {
		const meta = sticker?.meta;
		if (meta === null || typeof meta !== "object") {
			return false;
		}
		return (
			isNumberInRange(meta.x, MIN_RANGE, MAX_RANGE) &&
			isNumberInRange(meta.y, MIN_RANGE, MAX_RANGE) &&
			isNumberInRange(meta.w, MIN_WIDTH, MAX_WIDTH)
		);
	});
}

function isNumberInRange(value, min, max) {
	return typeof value === "number" && value >= min && value <= max;
}

const PNG_DATA_URL_PREFIX = "data:image/png;base64,";
async function decodePngDataUrl(dataUrl) {
	if (typeof dataUrl !== "string" || !dataUrl.startsWith(PNG_DATA_URL_PREFIX)) {
		return null;
	}
	const buffer = Buffer.from(dataUrl.slice(PNG_DATA_URL_PREFIX.length), "base64");
	try {
		const image = await Jimp.read(buffer);
		return image.mime === "image/png" ? image : null;
	} catch {
		return null;
	}
}

export async function deleteHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "delete") {
		res.setHeader('Allow', 'DELETE');
		returnError(res, 405, "Only accept DELETE method");
		return;
	}

	if (!req.user) {
		returnError(res, 401, "User not signed in");
		return;
	}

	try {
		const data = await readJsonBody(req);

		if (!Number.isInteger(data.id)) {
			throw new Error("Bad image id");
		}
		
		let result;
		try {
			result = await deleteImageById(data.id, req.user.id);
			if (!result) {
				returnError(res, 404, "Image was not found or is not belong to user");
				return;
			}
		} catch (e) {
			console.error(`Fail to remove image from db: ${e.message}`);
			returnError(res, 500, "Something wrong with the server");
			return
		}
	
		const filePath = path.join(UPLOADS_DIR, result.file);
		await unlink(filePath).catch(err => console.error(`Fail to remove ${filePath}: ${err.message}`));

		res.statusCode = 204;
		res.end();
	} catch (e) {
		returnError(res, 400, e.message);
		return;
	}
}
