import { readdir } from "node:fs/promises";
import { Jimp } from "jimp";

const STICKERS_DIR = "/app/assets/";
const NGINX_DIR = "/assets/";

export async function loadStickers() {
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
