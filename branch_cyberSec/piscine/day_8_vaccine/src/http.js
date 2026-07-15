import { print_error, print_info } from "./logger.js";
import { cleanUp } from "./helper.js";

export function sendRequest(setting, data) {
	const url = setting.urlBase;
	const method = setting.x;
	if (method === "GET")
		return sendGet(url, data);
	else if (method === "POST")
		return sendPost(url, data);
	else
		cleanUp(setting.db, `Invalid HTTP method ${method}`);
}

function sendGet(url, data) {

}

// TODO: continue
async function sendPost(url, data) {
	try {
		const response = fetch(url, {
			method: "POST",
			body: new URLSearchParams(data)
		});

		const result = (await response).text();

	} catch (err) {
		print_error(`Fail to send request to ${url}`);
		return null;
	}
}
