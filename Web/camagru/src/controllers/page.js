import { layout } from "../views/layout.js";
import { message } from "../views/sections/message.js";

export function returnPage(req, res, code, title, text) {
	res.statusCode = code;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout(`Camagru | ${title}`, "/css/message.css", null, message(title, text), req.user));
}
