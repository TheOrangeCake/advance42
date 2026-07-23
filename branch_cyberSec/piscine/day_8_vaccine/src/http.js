import { cleanUpFatal } from "./helper.js";
// import { printInfo } from "./logger.js";

const TIMEOUT_MS = 10000;

export async function sendRequest(setting, params) {
	const url = setting.urlPath;
	const method = setting.x;
	try {
		if (method === "GET") {
			return await sendGet(url, params);
		}
		else if (method === "POST")
			return await sendPost(url, params);
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
	// printInfo(`GET ${target.toString()}`);
	const response = await fetch(target, {
		method: "GET",
		signal: AbortSignal.timeout(TIMEOUT_MS),
	});
	const body = await response.text();
	return { status: response.status, body, time: performance.now() - start };
}

async function sendPost(url, params) {
	const start = performance.now();
	const payload = JSON.stringify(Object.fromEntries(params));
	// printInfo(`POST ${url} ${payload}`);
	const response = await fetch(url, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: payload,
		signal: AbortSignal.timeout(TIMEOUT_MS),
	});
	const body = await response.text();
	return { status: response.status, body, time: performance.now() - start };
}
