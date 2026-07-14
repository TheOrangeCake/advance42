import { DatabaseSync } from 'node:sqlite';

export function db_init(o_opt) {
	const db = new DatabaseSync(o_opt, {open: true});

	db.exec(`pragma foreign_keys = on`);

	// table SCAN -> store scanned vulnerable url
	db.exec(`
		create table if not exists SCAN (
				id integer primary key,
				url text not null,
				method text not null,
				db_engine text,
				created_at text default (datetime('now'))
		)`
	);

	// table VULNERABILITY -> vulnerables per scan
	db.exec(`
		create table if not exists VULNERABILITY (
				id integer primary key,
				scan_id integer not null references SCAN(id),
				parameter text not null,
				payload text not null,
				technique text
		)`
	);

	// table DB_NAME -> Database name per scan
	db.exec(`
		create table if not exists DB_NAME (
				id integer primary key,
				scan_id integer not null references SCAN(id),
				name text not null,
				unique(scan_id, name)
		)`
	);

	// table TBL_NAME -> Table name per database
	db.exec(`
		create table if not exists TBL_NAME (
				id integer primary key,
				db_id integer not null references DB_NAME(id),
				name text not null,
				unique(db_id, name)
		)`
	);

	// table COL_NAME -> Column name per table
	db.exec(`
		create table if not exists COL_NAME (
				id integer primary key,
				tbl_id integer not null references TBL_NAME(id),
				name text not null,
				data_type text,
				unique(tbl_id, name)
		)`
	);

	// table ROW_DUMP -> dumped rows per table (one JSON object per row)
	db.exec(`
		create table if not exists ROW_DUMP (
				id integer primary key,
				tbl_id integer not null references TBL_NAME(id),
				row_index integer not null,
				data text not null
		)`
	);

	return db;
}

export function db_save_scan(db, url, method, db_engine) {
	const statement = db.prepare(
		`INSERT INTO SCAN (url, method, db_engine) 
		VALUES (?, ?, ?)`
	)
	return statement.run(url, method, db_engine).lastInsertRowid;
}

export function db_save_vulnerability(db, scan_id, parameter, payload, technique) {
	const statement = db.prepare(
		`INSERT INTO VULNERABILITY (scan_id, parameter, payload, technique)
		VALUES (?, ?, ?, ?)`
	)
	return statement.run(scan_id, parameter, payload, technique).lastInsertRowid;
}

export function db_save_db_name(db, scan_id, name) {
	db.prepare(
		`INSERT OR IGNORE INTO DB_NAME (scan_id, name)
		VALUES (?, ?)`
	).run(scan_id, name);
	return db.prepare(
			`SELECT id FROM DB_NAME WHERE scan_id = ? AND name = ?`
	).get(scan_id, name).id;
}

export function db_save_tbl_name(db, db_id, name) {
	db.prepare(
		`INSERT OR IGNORE INTO TBL_NAME (db_id, name)
		VALUES (?, ?)`
	).run(db_id, name);
	return db.prepare(
			`SELECT id FROM TBL_NAME WHERE db_id = ? AND name = ?`
	).get(db_id, name).id;
}

export function db_save_col_name(db, tbl_id, name, data_type) {
	db.prepare(
		`INSERT OR IGNORE INTO COL_NAME (tbl_id, name, data_type)
		VALUES (?, ?, ?)`
	).run(tbl_id, name, data_type);
	return db.prepare(
			`SELECT id FROM COL_NAME WHERE tbl_id = ? AND name = ?`
	).get(tbl_id, name).id;
}

export function db_save_row_dump(db, tbl_id, row_index, data) {
	const statement = db.prepare(
		`INSERT INTO ROW_DUMP (tbl_id, row_index, data)
		VALUES (?, ?, ?)`
	)
	return statement.run(tbl_id, row_index, data).lastInsertRowid;
}

export function db_close(db) {
	if (db) {
		db.close();
	}
}
