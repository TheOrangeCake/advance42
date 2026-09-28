import { gallery } from "../views/sections/gallery.js";
import { parseCookie } from "./utils.js";
import { layout } from "../views/layout.js";
import { getSession } from "../services/session.js";

export async function galleryHandler(req, res) {
	const sessionId = parseCookie(req)["sessionId"];
	const user = getSession(sessionId);

	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout("Camagru | Gallery", "/css/gallery.css", "/js/app.js", gallery(), user));
}
