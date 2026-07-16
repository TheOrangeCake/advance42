import { dbInit } from './database.js';
import { cleanUp } from './helper.js';
import { print_info, print_error } from './logger.js';

const DEFAULT = 'vaccine.sqlite';

export class Setting {
	x = "GET";
	o = DEFAULT;
	urlRaw;
	urlPath;
	urlQuery;
	db;
}

export function parseAv(av) {
	const setting = new Setting();

	for (let i = 0; i < av.length; i++) {
		let arg = av[i].trim();
		switch (arg) {
			case "-X":
			case "-x":
				const valX = av[++i];
				if (valX === undefined) {
						cleanUp(setting.db, `Missing value for ${arg}`);
				}
				setting.x = parseX(valX.trim(), setting);
				continue;
			case "-O":
			case "-o":
				const valO = av[++i];
				if (valO === undefined) {
						cleanUp(setting.db, `Missing value for ${arg}`);
				}
				parseO(valO.trim(), setting);
				continue;
		}
		if (arg.startsWith("http://") || arg.startsWith("https://")) {
			try {
				setting.urlRaw = new URL(arg);
				if (++i < av.length) {
					print_error(`All arguments after URL are ignored: ${av.slice(i)}`);
				}
				break;
			} catch(error) {
				cleanUp(setting.db, `Invalid URL: ${arg}`);
			}
		}
		cleanUp(setting.db, `Unknow argument: ${av[i]}`);
	}

	if (setting.urlRaw === undefined) {
		cleanUp(setting.db, `Need an Url`);
	}

	if (setting.o === DEFAULT)
		parseO(DEFAULT, setting);

	return setting;
}

function parseX(xOpt, setting) {
	const xOptUpper = xOpt.toUpperCase();
	if (xOptUpper !== "GET" && xOptUpper !== "POST") {
		cleanUp(setting.db, `Unknow x option: ${xOpt}`);
	}
	return xOptUpper;
}

function parseO(oOpt, setting) {
	try {
		setting.db = dbInit(oOpt);
		setting.o = oOpt;
	} catch (err) {
		cleanUp(setting.db, `Cannot open output file '${oOpt}': ${err.message}`);
	}
}

export function urlParser(setting) {
	const url = setting.urlRaw;
	setting.urlPath = url.origin + url.pathname;
	setting.urlQuery = url.searchParams;
	if (setting.urlQuery.size < 1) {
		cleanUp(setting.db, "URL need a valid query to attempt injection");
	}

	print_info("Query parameters:");
	for (const [key, value] of setting.urlQuery.entries()) {
		print_info(`${key} = ${value}`);
	}
}
