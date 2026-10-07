import crypto from "node:crypto";

export function returnError(res, code, message) {
	res.statusCode = code;
	res.setHeader('Content-type', 'text/plain; charset=utf-8');
	res.end(message)
}

export function conflict(message) {
	const err = new Error(message);
	err.status = 409;
	return err;
}

export function notFound(message) {
	const err = new Error(message);
	err.status = 404;
	return err;
}

export function internalError(message, cause) {
	const err = new Error(message, { cause });
	err.status = 500;
	return err;
}

const URL_BASE = "http://localhost";
export function parseUrl(req) {
	return new URL(req.url, URL_BASE);
}

export function parseCookie(req) {
	const cookies = {};
	const cookiesHeader = req.headers.cookie;
	if (!cookiesHeader) {
		return cookies;
	}
	cookiesHeader.split(`;`).forEach(cookie => {
		const parts = cookie.match(/(.*?)=(.*)$/);
		if (!parts) {
			return;
		}
		cookies[ parts[1].trim() ] = parts[2].trim();
	});
	return cookies;
}

const HTML_ESCAPES = { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" };
export function escapeHtml(str) {
	return String(str).replace(/[&<>"']/g, c => HTML_ESCAPES[c]);
}

export const dateFormat = new Intl.DateTimeFormat("en-GB", {
	day: "numeric",
	month: "short",
	year: "numeric",
	hour: "2-digit",
	minute: "2-digit",
});

export function generateToken() {
	return crypto.randomBytes(32).toString("hex");
}


const MAX_BODY_SIZE = 10 * 1024 * 1024; // 10 MB

export function readJsonBody(req) {
	return new Promise((resolve, reject) => {
		let body = "";
		let size = 0;

		req.on("data", (chunk) => {
			size += chunk.length;
			if (size > MAX_BODY_SIZE) {
				req.destroy();
				reject(new Error("Payload too large"));
				return;
			}
			body += chunk;
		});

		req.on("end", () => {
		if (body === "") {
			resolve({});
			return;
		}
		try {
			resolve(JSON.parse(body));
		} catch (err) {
			reject(new Error("Invalid JSON"));
		}
		});

		req.on("error", reject);
	});
}
