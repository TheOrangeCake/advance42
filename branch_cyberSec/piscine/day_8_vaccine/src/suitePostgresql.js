import { printInfo } from "./logger.js";
import { sendRequest } from "./http.js";
import { mutateQuery } from "./helper.js";

export function testPostgresql(setting) {
	printInfo("Running Postgresql test suite");

	const query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const c = setting.contextChar;

	getDatabaseName(setting, query, firstParamsName, firstParamsValue, c);

}

async function getDatabaseName(setting, query, name, valueBase, c) {
	// const res = await sendRequest(setting, mutateQuery(query, name, `${valueBase}${c} OR ${expr}-- 42`));
}