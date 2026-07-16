import { parseAv, Setting, urlParser } from './setting.js';
import { printFatal, printInfo, printSuccess } from './logger.js';
import { detectFingerprint } from './fingerprint.js';
import { injectable } from './injectable.js';
import { cleanUpFatal } from './helper.js';
import { testOracle } from './suiteOracle.js';
import { testMySQL } from './suiteSQL.js';

console.log("Launching Vaccine...");

const av = process.argv.slice(2);

if (av.length < 1) {
	printFatal("Need an URL");
	process.exit(1);
}
const setting = parseAv(av);
urlParser(setting);

printInfo(`Setting -> x: ${setting.x} | o: ${setting.o} | path: ${setting.urlPath} | query: ${setting.urlQuery.toString()}`);

const result = await injectable(setting);
if (!result || !result.injectable)
	cleanUpFatal(setting.db, "URL is not injectable");
printSuccess(`${setting.urlRaw} is INJECTABLE!`);

const dbEngine = detectFingerprint(setting);
if (dbEngine === "Oracle")
	testOracle(setting);
else if (dbEngine === "SQL")
	testMySQL(setting);
else
	cleanUpFatal(setting.db, "Unrecognized database engine");

// Code test suite for each engine, then run them for matching engine
// Persist the data to db after successful test
