import { destroySession } from "../../services/session.js";
import { returnError } from "../utils.js";

export function signoutHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "post") {
		res.setHeader('Allow', 'POST');
		returnError(res, 405, "Only accept POST method");
		return;
	}

	destroySession(req.sessionId);

	if (req.user) {
		console.log(`User ${req.user?.id} ${req.user?.username} signed out`);
	}
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/plain; charset=utf-8');
	res.setHeader("Set-Cookie", `sessionId=; HttpOnly; SameSite=Strict; Path=/; Max-Age=0`);
	res.end(`Signed out successfully`);
}
