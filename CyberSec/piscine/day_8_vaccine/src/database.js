import { DatabaseSync } from 'node:sqlite';

export function dbInit(oOpt) {
	const db = new DatabaseSync(oOpt, {open: true});

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

export function dbSaveScan(db, url, method, dbEngine) {
	const statement = db.prepare(
		`INSERT INTO SCAN (url, method, db_engine)
		VALUES (?, ?, ?)`
	)
	return statement.run(url, method, dbEngine).lastInsertRowid;
}

export function dbSaveVulnerability(db, scanId, parameter, payload, technique) {
	const statement = db.prepare(
		`INSERT INTO VULNERABILITY (scan_id, parameter, payload, technique)
		VALUES (?, ?, ?, ?)`
	)
	return statement.run(scanId, parameter, payload, technique).lastInsertRowid;
}

export function dbSaveDbName(db, scanId, name) {
	db.prepare(
		`INSERT OR IGNORE INTO DB_NAME (scan_id, name)
		VALUES (?, ?)`
	).run(scanId, name);
	return db.prepare(
			`SELECT id FROM DB_NAME WHERE scan_id = ? AND name = ?`
	).get(scanId, name).id;
}

export function dbSaveTblName(db, dbId, name) {
	db.prepare(
		`INSERT OR IGNORE INTO TBL_NAME (db_id, name)
		VALUES (?, ?)`
	).run(dbId, name);
	return db.prepare(
			`SELECT id FROM TBL_NAME WHERE db_id = ? AND name = ?`
	).get(dbId, name).id;
}

export function dbSaveColName(db, tblId, name) {
	db.prepare(
		`INSERT OR IGNORE INTO COL_NAME (tbl_id, name)
		VALUES (?, ?)`
	).run(tblId, name);
	return db.prepare(
			`SELECT id FROM COL_NAME WHERE tbl_id = ? AND name = ?`
	).get(tblId, name).id;
}

export function dbSaveRowDump(db, tblId, rowIndex, data) {
	const statement = db.prepare(
		`INSERT INTO ROW_DUMP (tbl_id, row_index, data)
		VALUES (?, ?, ?)`
	)
	return statement.run(tblId, rowIndex, data).lastInsertRowid;
}

export function dbClose(db) {
	if (db) {
		db.close();
	}
}
