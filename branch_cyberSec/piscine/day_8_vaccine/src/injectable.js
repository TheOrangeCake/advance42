import { sendRequest } from "./http.js";

export async function injectable(setting) {
	// run with original query
	// take the first query value, put ' AND '1'='1 or AND 1=1 at the end -> result should be the same
	// still the first query value, put ' AND '1'='2 or AND 1=2 at the end -> result should be different
	// if both tests are correct then it is injectable
	let query = setting.urlQuery;
	const firstParamsName = [...query.keys()][0];
	const firstParamsValue = query.get(firstParamsName);
	const resOriginal = await sendRequest(setting, query);


	const resTrueString = await sendRequest(setting, mutateQuery(query, firstParamsName,  firstParamsValue + "' AND '1'='1"));
	// then compare with original

	const resTrueNumber = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + " AND 1=1"));
	// then compare with original


	const resFalseString = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + "' AND '1'='2"));
	// then compare with original

	const resFalseNumber = await sendRequest(setting, mutateQuery(query, firstParamsName, firstParamsValue + " AND 1=2"));
	// then compare with original

	// return placeholder
	return true;
}

function mutateQuery(query, name, value) {
	let mutatedQuery = new URLSearchParams(query);
	mutatedQuery.set(name, value);
	return mutatedQuery;
}

function compareRes(original, mutated) {

}
