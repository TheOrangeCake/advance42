import { returnError, parseUrl } from "../utils.js";
import { verifyUser } from "../../models/user.js";

export async function verifyHandler(req, res) {
	const method = req.method;
	if (method.toLowerCase() !== "get") {
		res.setHeader('Allow', 'GET');
		returnError(res, 405, "Only accept GET method");
		return;
	}

	const params = parseUrl(req).searchParams;
	const rawId = params.get("id");
	const token = params.get("token");

	if (!rawId || !/^\d+$/.test(rawId) || !token) {
		returnError(res, 400, "Invalid activation link");
		return;
	}
	const id = Number(rawId);
	if (id < 1 || id > 2147483647) {
		returnError(res, 400, "Invalid activation link");
		return;
	}

	let result;
	try {
		result = await verifyUser(id, token);
	} catch (e) {
		console.error(`Error verifying user ${id}:`, e);
		returnError(res, 500, "Something went wrong in the server");
		return;
	}

	if (result === "invalid") {
		returnError(res, 400, "Invalid or expired activation link, please sign up again");
		return;
	}

	console.log(`Account ${id} activated`);
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/plain; charset=utf-8');
	res.end("Account activated, you can now log in");
}
