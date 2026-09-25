import { Pool } from "pg";

const pool = new Pool({
	host: process.env.PGHOST,
	port: Number(process.env.PGPORT),
	database: process.env.PGDATABASE,
	user: process.env.PGUSER,
	password: process.env.PGPASSWORD,
	max: 10,
	idleTimeoutMillis: 10000,
})

pool.on("error", (err) => {
	console.error("Unexpected error on idle client", err);
})

export async function dbQuery(query, params) {
	const client = await pool.connect();
	const queryObject = {text: query, values: params}
	try {
		return await client.query(queryObject);
	} catch (e) {
		client.release(e);
	}
}
