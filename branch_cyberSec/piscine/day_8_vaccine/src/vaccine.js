import {parse_av, Setting} from './setting.js';

console.log("Launching Vaccine...");

const av = process.argv.slice(2);

if (av.length < 1) {
	console.log("[Error] Need an URL");
	process.exit(1);
}

const setting = parse_av(av);

console.log(`[Setting] x: ${setting.x} | o: ${setting.o} | url: ${setting.url}`);
