import { layout } from "../views/layout.js";
import { signupHandler } from "./signup.js";
import { verifyHandler } from "./verify.js";
import { gallery } from "../views/sections/gallery.js";

export async function handleRequest(req, res) {
	const url = new URL(req.url, "http://localhost");
	
	console.log(`${req.method} ${url.pathname}`);

	if (url.pathname === "/api/signup") {
		await signupHandler(req, res);
	} else if (url.pathname === "/api/verify") {
		await verifyHandler(req, res);
	} else if (url.pathname === "/") {
		res.statusCode = 200;
		res.setHeader('Content-type', 'text/html; charset=utf-8');
		res.end(layout("Camagru | Gallery", "/css/gallery.css", "/js/app.js", gallery()));
	} else {
		res.statusCode = 404;
		res.setHeader('Content-type', 'text/plain; charset=utf-8');
		res.end("Page not found");
	}
}
