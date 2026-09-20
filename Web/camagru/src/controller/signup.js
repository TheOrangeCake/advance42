import { text } from "node:stream/consumers";
import { returnError } from "./utils.js";

export async function signupHandler(req, res) {
	const url = req.url
	const method = req.method;
	console.log(`${method} ${url}`)

	if (method.toLowerCase() !== "post") {
		res.setHeader('Allow', 'POST')
		returnError(res, 405, "Only accept POST method");
		return;
	}

	const params = new URLSearchParams(await text(req));
	const user = params.get("user");
	const email = params.get("email");
	const pass = params.get("pass");
	const passConfirm = params.get("passConfirm");

	if (!user || !email || !pass || !passConfirm) {
		returnError(res, 400, "Bad field values");
		return;
	}

	// place holder 
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(`<p>User: ${user}<br>Email: ${email}<br>Password: ${pass}<br>Confirm: ${passConfirm}</p>`);
}
