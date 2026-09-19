import { createServer } from 'node:http';
import { layout } from './views/layout.js';

const hostname = '0.0.0.0';
const port = process.env.PORT || 3000;

const server = createServer((req, res) => {
	res.statusCode = 200;
	res.setHeader('Content-type', 'text/html; charset=utf-8');
	res.end(layout("Camaru | Gallery", "/css/gallery.css", "/js/app.js"));
})

server.listen(port, hostname, () => {
	console.log(`Server running at http://${hostname}:${port}/`);
})
