import { dbClose } from "./database.js";
import { print_fatal } from "./logger.js";

export function cleanUp(db, ...a) {
	dbClose(db);
	print_fatal(...a);
	process.exit(1);
}
