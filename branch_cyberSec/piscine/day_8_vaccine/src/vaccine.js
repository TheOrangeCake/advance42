import {parse_av, Setting} from './setting.js';

console.log("Launching Vaccine...");

const av = process.argv.slice(2);

if (av.length < 1) {
	console.log("[Error] Need an URL");
	process.exit(1);
}

const setting = parse_av(av);

console.log(`[Setting] x: ${setting.x} | o: ${setting.o} | url: ${setting.url}`);


// Recode the -o to open sqlite db
// Code send GET (build query) and POST (build payload form-urlencoded)
// Url parser to parse the params for base url and params list
// Code db fingerprint dectection by generic sql injection and check response
// check if response match engine
// Code test suite for each engine, then run them for matching engine
// Persist the data to db
