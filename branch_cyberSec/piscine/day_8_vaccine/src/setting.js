import { dbInit } from './database.js';
import { cleanUp } from './helper.js';
import { print_info, print_error } from './logger.js';

const DEFAULT = 'vaccine.sqlite';

export class Setting {
	x = "GET";
	o = DEFAULT;
	urlRaw = "";
	urlBase = "";
	queryBase;
	query;
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
				setting.urlRaw = arg;
				if (++i < av.length) {
					print_error(`All arguments after URL are ignored: ${av.slice(i)}`);
				}
				break;
		}
		cleanUp(setting.db, `Unknow argument: ${av[i]}`);
	}

	if (setting.urlRaw === "") {
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
	const urlString = setting.urlRaw;
	const index = urlString.indexOf('?');
	setting.urlBase  = index === -1 ? urlString : urlString.slice(0, index);
	setting.queryBase = index === -1 ? undefined  : urlString.slice(index + 1);

	if (setting.queryBase === undefined) {
		cleanUp(setting.db, "URL need a query to attempt injection");
	}

	setting.query = new URLSearchParams(setting.queryBase);
	print_info("Query parameters:");
	for (let pair of setting.query.entries()) {
		print_info(`${pair[0]} = ${pair[1]}`);
	}
}
