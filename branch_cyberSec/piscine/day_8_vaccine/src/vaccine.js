import { parseAv, urlParser } from './setting.js';
import { printFatal, printInfo, printSuccess } from './logger.js';
import { detectFingerprint } from './fingerprint.js';
import { injectable } from './injectable.js';
import { cleanUpFatal } from './helper.js';
import { testPostgresql } from './suitePostgresql.js';
import { testMySql } from './suiteMySql.js';
import { dbClose } from './database.js';

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
	await testPostgresql(setting);
else if (dbEngine === "SQL")
	await testMySql(setting);
else
	cleanUpFatal(setting.db, `Unhandled database engine: ${dbEngine}`);
dbClose(setting.db);
