import { returnError, parseUrl, isValidId } from "../utils.js";
import { verifyUser } from "../../models/user.js";
import { returnPage } from "../page.js";

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
		returnPage(req, res, 400, "Invalid link", "This activation link is invalid");
		return;
	}
	const id = Number(rawId);
	if (!isValidId(id)) {
		returnPage(req, res, 400, "Invalid link", "This activation link is invalid");
		return;
	}

	let result;
	try {
		result = await verifyUser(id, token);
	} catch (e) {
		console.error(`Error verifying user ${id}:`, e);
		returnPage(req, res, 500, "Server error", "Something went wrong in the server");
		return;
	}

	if (result === "invalid") {
		returnPage(req, res, 400, "Invalid link", "Invalid or expired activation link, please sign up again");
		return;
	}

	if (result === "already-active") {
		returnPage(req, res, 200, "Already activated", "This account is already active, you can sign in");
		return;
	}

	returnPage(req, res, 200, "Account activated", "Your account is active, you can now sign in");
}
