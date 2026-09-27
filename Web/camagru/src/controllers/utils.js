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

const URL_BASE = "http://localhost";

export function parseUrl(req) {
	return new URL(req.url, URL_BASE);
}
