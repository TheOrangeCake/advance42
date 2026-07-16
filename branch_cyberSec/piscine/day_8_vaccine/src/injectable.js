
export function injectable(setting) {
	// run with original query
	// take the first query value, put ' AND '1'='1 or AND 1=1 at the end -> result should be the same
	// still the first query value, put ' AND '1'='2 or AND 1=2 at the end -> result should be different
	// if both tests are correct then it is injectable
	return true;
}
