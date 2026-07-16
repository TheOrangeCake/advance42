import { print_error, print_info } from "./logger.js";
import { cleanUp } from "./helper.js";

export async function sendRequest(setting, params) {
	const url = setting.urlPath;
	const method = setting.x;
	if (method === "GET")
		return sendGet(url, params);
	else if (method === "POST")
		return sendPost(url, params);
	else
		cleanUp(setting.db, `Invalid HTTP method ${method}`);
}

async function sendGet(url, params) {
	const start = performance.now();
	try {
		const target = new URL(url);
		target.search = new URLSearchParams(params).toString();
		const response = await fetch(target, {
			method: "GET",
		});
		const body = await response.text();
		return { status: response.status, body, time: performance.now() - start };
	} catch (err) {
		print_error(`Fail to send request to ${url} with error: ${err.message}`);
		return null;
	}
}

async function sendPost(url, params) {
	const start = performance.now();
	try {
		const response = await fetch(url, {
			method: "POST",
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: new URLSearchParams(params)
		});
		const body = await response.text();
		return { status: response.status, body, time: performance.now() - start };
	} catch (err) {
		print_error(`Fail to send request to ${url} with error: ${err.message}`);
		return null;
	}
}
