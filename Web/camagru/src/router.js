import { parseUrl, parseCookie } from "./controllers/utils.js";
import { getSession } from "./services/session.js";
import { signupHandler } from "./controllers/signup.js";
import { signinHandler } from "./controllers/signin.js";
import { verifyHandler } from "./controllers/verify.js";
import { galleryHandler } from "./controllers/gallery.js";
import { signoutHandler } from "./controllers/signout.js";

export async function handleRequest(req, res) {
	const url = parseUrl(req);
	
	console.log(`${req.method} ${url.pathname}`);

	req.sessionId = parseCookie(req)["sessionId"];
	req.user = getSession(req.sessionId);

	if (url.pathname === "/api/signup") {
		await signupHandler(req, res);
	} else if (url.pathname === "/api/verify") {
		await verifyHandler(req, res);
	} else if (url.pathname === "/api/signin") {
		await signinHandler(req, res);
	} else if (url.pathname === "/api/signout") {
		signoutHandler(req, res);
	} else if (url.pathname === "/" || url.pathname === "/gallery") {
		await galleryHandler(req, res);
	} else {
		res.statusCode = 404;
		res.setHeader('Content-type', 'text/plain; charset=utf-8');
		res.end("Page not found");
	}
}
