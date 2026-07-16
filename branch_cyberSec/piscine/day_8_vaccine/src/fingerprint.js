
export function detectFingerprint(setting) {
	// take the first query value, split in 2 half (edge case 1 single letter)
	// send first request with original query intact
	// send second request with splitted query and concatenate operator
	// compare result
	// same result -> record engine
	// different result -> next engine
}
