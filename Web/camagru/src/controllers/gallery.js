import { gallery } from "../views/sections/gallery.js";
import { layout } from "../views/layout.js";

export async function galleryHandler(req, res) {
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout("Camagru | Gallery", "/css/gallery.css", "/js/app.js", gallery(), req.user));
}
