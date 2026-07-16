import { printError, printInfo } from "./logger.js";
import { cleanUpFatal } from "./helper.js";

export async function sendRequest(setting, params) {
	const url = setting.urlPath;
	const method = setting.x;
	try {
		if (method === "GET") {
			return sendGet(url, params);
		}
		else if (method === "POST")
			return sendPost(url, params);
		else
			cleanUpFatal(setting.db, `Invalid HTTP method ${method}`);
	} catch (err) {
		cleanUpFatal(setting.db, `Fail to send request to ${url} with error: ${err.message}`);
	}
}

async function sendGet(url, params) {
	const start = performance.now();
	const target = new URL(url);
	target.search = new URLSearchParams(params).toString();
	const response = await fetch(target, {
		method: "GET",
	});
	const body = await response.text();
	return { status: response.status, body, time: performance.now() - start };
}

async function sendPost(url, params) {
	const start = performance.now();
	const response = await fetch(url, {
		method: "POST",
		headers: { "Content-Type": "application/x-www-form-urlencoded" },
		body: new URLSearchParams(params)
	});
	const body = await response.text();
	return { status: response.status, body, time: performance.now() - start };
}
