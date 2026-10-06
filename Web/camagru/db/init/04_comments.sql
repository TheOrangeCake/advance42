CREATE TABLE comments (
	id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	user_id int NOT NULL REFERENCES users(id) ON DELETE CASCADE,
	image_id int NOT NULL REFERENCES images(id) ON DELETE CASCADE,
	comment text NOT NULL,
	created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ON comments (image_id);
