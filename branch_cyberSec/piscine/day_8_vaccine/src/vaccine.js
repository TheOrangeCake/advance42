import { parseAv, Setting, urlParser } from './setting.js';
import { sendRequest } from './http.js';
import { print_fatal, print_info } from './logger.js';
import { dbClose } from './database.js';

console.log("Launching Vaccine...");

const av = process.argv.slice(2);

if (av.length < 1) {
	print_fatal("Need an URL");
	process.exit(1);
}
const setting = parseAv(av);
urlParser(setting);

print_info(`Setting -> x: ${setting.x} | o: ${setting.o} | path: ${setting.urlPath} | query: ${setting.urlQuery.toString()}`);

// Code send GET (build query) and POST (build payload form-urlencoded)
// Url parser to parse the params for base url and params list
// Code db fingerprint dectection by generic sql injection and check response
// check if response match engine
// Code test suite for each engine, then run them for matching engine
// Persist the data to db
