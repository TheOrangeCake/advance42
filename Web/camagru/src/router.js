import { parseUrl, parseCookie } from "./controllers/utils.js";
import { getSession } from "./services/session.js";
import { signupHandler } from "./controllers/auth/signup.js";
import { signinHandler } from "./controllers/auth/signin.js";
import { verifyHandler } from "./controllers/auth/verify.js";
import { galleryHandler } from "./controllers/gallery.js";
import { signoutHandler } from "./controllers/auth/signout.js";
import { forgotHandler } from "./controllers/auth/forgot.js";
import { resetPageHandler, resetHandler } from "./controllers/auth/reset.js";
import { profilePageHandler, modifyProfileHandler } from "./controllers/profile.js";
import { editPageHandler } from "./controllers/edit.js";

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
	} else if (url.pathname === "/api/forgot") {
		await forgotHandler(req, res);
	} else if (url.pathname === "/api/reset") {
		await resetHandler(req, res);
	} else if (url.pathname === "/reset") {
		await resetPageHandler(req, res);
	} else if (url.pathname === "/profile") {
		profilePageHandler(req, res);
	} else if (url.pathname === "/api/profile") {
		await modifyProfileHandler(req, res);
	} else if (url.pathname === "/edit") {
		editPageHandler(req, res);
	} else if (url.pathname === "/" || url.pathname === "/gallery") {
		await galleryHandler(req, res);
	} else {
		res.statusCode = 404;
		res.setHeader('Content-type', 'text/plain; charset=utf-8');
		res.end("Page not found");
	}
}
