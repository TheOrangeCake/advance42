import { dbInit } from './database.js';
import { cleanUpFatal } from './helper.js';
import { printError } from './logger.js';

const DEFAULT = 'vaccine.sqlite';

export class Setting {
	x = "GET";
	o = DEFAULT;
	urlRaw;
	urlPath;
	urlQuery;
	db;
	contextChar;
}

export function parseAv(av) {
	const setting = new Setting();

	for (let i = 0; i < av.length; i++) {
		let arg = av[i].trim();
		switch (arg) {
			case "-X":
			case "-x": {
				const valX = av[++i];
				if (valX === undefined) {
					cleanUpFatal(setting.db, `Missing value for ${arg}`);
				}
				setting.x = parseX(valX.trim(), setting);
				continue;
			}
			case "-O":
			case "-o": {
				const valO = av[++i];
				if (valO === undefined) {
					cleanUpFatal(setting.db, `Missing value for ${arg}`);
				}
				parseO(valO.trim(), setting);
				continue;
			}
		}
		if (arg.startsWith("http://") || arg.startsWith("https://")) {
			try {
				setting.urlRaw = new URL(arg);
				if (++i < av.length) {
					printError(`All arguments after URL are ignored: ${av.slice(i)}`);
				}
				break;
			} catch(error) {
				cleanUpFatal(setting.db, `Invalid URL: ${error.message}`);
			}
		}
		cleanUpFatal(setting.db, `Unknow argument: ${av[i]}`);
	}

	if (setting.urlRaw === undefined) {
		cleanUpFatal(setting.db, `Need an Url`);
	}

	if (setting.o === DEFAULT) {
		parseO(DEFAULT, setting);
	}

	return setting;
}

function parseX(xOpt, setting) {
	const xOptUpper = xOpt.toUpperCase();
	if (xOptUpper !== "GET" && xOptUpper !== "POST") {
		cleanUpFatal(setting.db, `Unknow x option: ${xOpt}`);
	}
	return xOptUpper;
}

function parseO(oOpt, setting) {
	try {
		setting.db = dbInit(oOpt);
		setting.o = oOpt;
	} catch (err) {
		cleanUpFatal(setting.db, `Cannot open output file '${oOpt}': ${err.message}`);
	}
}

export function urlParser(setting) {
	const url = setting.urlRaw;
	setting.urlPath = url.origin + url.pathname;
	setting.urlQuery = url.searchParams;
	if (setting.urlQuery.size < 1) {
		cleanUpFatal(setting.db, "URL need a valid query to attempt injection");
	}
}
