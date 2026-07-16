import { dbClose } from "./database.js";
import { printFatal, printError, printSuccess, printInfo } from "./logger.js";

export function cleanUpFatal(db, ...a) {
	dbClose(db);
	printFatal(...a);
	process.exit(1);
}

export function cleanUpInfo(db, ...a) {
	dbClose(db);
	printInfo(...a);
	process.exit(1);
}
