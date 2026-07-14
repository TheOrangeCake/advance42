import fs from "fs";

const DEFAULT = "vaccine.sqlite";

export class Setting {
	x = "GET";
	o = DEFAULT;
	url = "";
}

export function parse_av(av) {
	const setting = new Setting();

	for (let i = 0; i < av.length; i++) {
		let arg = av[i].trim();
		switch (arg) {
			case "-X":
			case "-x":
				const val_x = av[++i];
				if (val_x === undefined) {
						console.log(`[Error] Missing value for ${arg}`);
						process.exit(1);
				}
				setting.x = parse_x(val_x.trim());
				continue;
			case "-O":
			case "-o":
				const val_o = av[++i];
				if (val_o === undefined) {
						console.log(`[Error] Missing value for ${arg}`);
						process.exit(1);
				}
				setting.o = parse_o(val_o.trim());
				continue;
		}
		if (arg.startsWith("http://")
			|| arg.startsWith("https://")
			|| arg.startsWith("www.")) {
				setting.url = arg;
				if (++i < av.length) {
					console.log(`[INFO] All arguments after URL are ignored: ${av.slice(i)}`);
				}
				break;
		}
		console.log(`[Error] Unknow argument: ${av[i]}`);
		process.exit(1);
	}

	if (setting.url === "") {
		console.log(`[Error] Need an Url`);
		process.exit(1);
	}

	if (setting.o === DEFAULT)
		setting.o = parse_o(DEFAULT);

	return setting;
}

function parse_x(x_opt) {
	const x_opt_upper = x_opt.toUpperCase();
	if (x_opt_upper !== "GET" && x_opt_upper !== "POST") {
		console.log(`[Error] Unknow x option: ${x_opt}`);
		process.exit(1);
	}
	return x_opt_upper;
}

// TODO: Initialize db here
function parse_o(o_opt) {
	try {
		const fd = fs.openSync(o_opt, "w");
		fs.closeSync(fd);	
	} catch (err) {
		console.log(`[Error] Cannot open output file '${o_opt}': ${err.code}`);
		process.exit(1);
	}
	return o_opt;
}
