import { createServer } from 'node:http';
import { handleRequest } from './controller/router.js';
import { returnError } from './controller/utils.js';

const hostname = '0.0.0.0';
const port = process.env.PORT || 3000;

const server = createServer(async (req, res) => {
	try {
		await handleRequest(req, res);
	} catch (error) {
		console.error(`Error: ${error}`);
		if (!res.headersSent) {
			returnError(res, 500, "Something went wrong");
		} else {
			res.destroy();
		}
	}
})

server.listen(port, hostname, () => {
	console.log(`Server running at http://${hostname}:${port}/`);
})
