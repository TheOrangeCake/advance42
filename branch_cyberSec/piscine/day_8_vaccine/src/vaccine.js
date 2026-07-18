import { parseAv, urlParser } from './setting.js';
import { printFatal, printInfo, printSuccess } from './logger.js';
import { detectFingerprint } from './fingerprint.js';
import { injectable } from './injectable.js';
import { cleanUpFatal } from './helper.js';
import { testPostgresql } from './suitePostgresql.js';
import { testSqlite } from './suiteSqlite.js';

printInfo("Launching Vaccine...");

const av = process.argv.slice(2);
if (av.length < 1) {
	printFatal("Need an URL");
	process.exit(1);
}

const setting = parseAv(av);
urlParser(setting);

printInfo(`Setting -> x: ${setting.x} | o: ${setting.o} | path: ${setting.urlPath} | query: ${setting.urlQuery.toString()}`);

const result = await injectable(setting);
if (!result)
	cleanUpFatal(setting.db, "URL is not injectable");
printSuccess(`${setting.urlRaw} is INJECTABLE!`);

const dbEngine = await detectFingerprint(setting);
if (dbEngine === "POSTGRESQL")
	testPostgresql(setting);
else if (dbEngine === "SQL")
	testSqlite(setting);
else
	cleanUpFatal(setting.db, `Unhandled database engine: ${dbEngine}`);

// Code test suite for each engine, then run them for matching engine
// Persist the data to db after successful test
