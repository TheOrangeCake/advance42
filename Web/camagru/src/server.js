import { createServer } from 'node:http';
import { handleRequest } from './router.js';
import { returnError } from './controllers/utils.js';
import { loadStickers } from './services/stickers.js';

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

await loadStickers();

server.listen(port, hostname);
