import { readdir } from "node:fs/promises";
import { Jimp } from "jimp";

const STICKERS_DIR = "/app/assets/";
const NGINX_DIR = "/assets/";

const stickerTable = new Map();

async function readAllFiles() {
	// return the list of file in the directory
	return readdir(STICKERS_DIR)
		.then(filenames => {
			// resolve all promise
			return Promise.all(
				// filter PNG only
				filenames.filter(n => n.toLowerCase().endsWith(".png"))
				.map(async (filename) => {
					const url = NGINX_DIR + filename;
					const image = await Jimp.read(STICKERS_DIR + filename);
					return {filename, url, image};
				})
			)
		})
}

export async function loadStickers() {
	const files = await readAllFiles();
	files.forEach(file => {
		const url = file.url;
		const image = file.image;
		stickerTable.set(file.filename, {url, image});
	})
}

export function getSticker(fileName) {
	const file = stickerTable.get(fileName);
	return file ?? null;
}

export function getAllStickerName() {
	return [... stickerTable.keys()];
}

export function getStickerUrl(fileName) {
	const file = stickerTable.get(fileName);
	return file?.url ?? null;
}

export function getStickerImage(fileName) {
	const file = stickerTable.get(fileName);
	return file?.image.clone() ?? null;
}
