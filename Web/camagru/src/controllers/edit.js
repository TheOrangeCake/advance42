import { Jimp } from "jimp";
import { edit } from "../views/sections/edit.js";
import { layout } from "../views/layout.js";
import { returnError } from "./utils.js";
import { getAllStickerName, isStickerExist, getStickerCount, getStickerImage } from "../services/stickers.js";

export function editPageHandler(req, res) {
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
	
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout("Camagru | Edit", "/css/edit.css", "/js/edit.js", edit(getAllStickerName()), req.user));
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

		// compose
		// save the composed to the db
		// send back the composed image and the id (for delete)

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

		const temp = await image.getBase64("image/png");
		res.statusCode = 200;
		res.setHeader('Content-type', 'application/json');
		res.end(JSON.stringify({img: temp}));


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
