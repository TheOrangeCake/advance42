import { dbClose } from "./database.js";
import { printFatal, printInfo } from "./logger.js";

export function cleanUpFatal(db, ...a) {
	dbClose(db);
	printFatal(...a);
	process.exit(1);
}

export function cleanUpInfo(db, ...a) {
	dbClose(db);
	printInfo(...a);
	process.exit(0);
}


export function mutateQuery(query, name, value) {
	let mutatedQuery = new URLSearchParams(query);
	mutatedQuery.set(name, value);
	return mutatedQuery;
}

export function isSameRes(original, mutated) {
	if (!original || !mutated) {
		return false;
	}
	// console.log(`O: ${original.status} | M: ${mutated.status}`);
	if (original.status !== mutated.status) {
		return false;
	}
	// console.log(`O: ${original.body} | M: ${mutated.body}`);
	if (original.body === mutated.body) {
		return true;
	}
	return similarity(original.body, mutated.body);
}

function similarity(original, mutated) {
    if (original.length === 0 && mutated.length === 0) {
		return true;
	}
    const longer = Math.max(original.length, mutated.length);
    const shorter = Math.min(original.length, mutated.length);
    return (shorter / longer) >= 0.95;
}
