import { layout } from "../views/layout.js";
import { signupHandler } from "./signup.js";

export async function handleRequest(req, res) {
	const url = new URL(req.url, "http://localhost");

	if (url.pathname === "/api/signup") {
		await signupHandler(req, res);
	} else {
		console.log(`${req.method} ${url.pathname}`);
		res.statusCode = 200;
		res.setHeader('Content-type', 'text/html; charset=utf-8');
		res.end(layout("Camagru | Gallery", "/css/gallery.css", "/js/app.js"));
	}
}
