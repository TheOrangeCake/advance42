import { edit } from "../views/sections/edit.js";
import { layout } from "../views/layout.js";
import { returnError } from "./utils.js";
import { getAllStickerName } from "../services/stickers.js";

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
	
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout("Camagru | Edit", "/css/edit.css", "/js/edit.js", edit(getAllStickerName()), req.user));
}
