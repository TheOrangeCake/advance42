CREATE TABLE users (
	id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	username text UNIQUE NOT NULL,
	email text UNIQUE NOT NULL,
	password text NOT NULL,
	active boolean NOT NULL DEFAULT FALSE,
	mail_token text UNIQUE,
	mail_token_exp TIMESTAMPTZ,
	created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
