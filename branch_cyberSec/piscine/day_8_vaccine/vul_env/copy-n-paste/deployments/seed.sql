-- ---------------------------------------------------------------------------
-- Extra tables for copy-n-paste (MariaDB database `a1db`)
--
-- Purpose: give the SQLi tool (`vaccine`) something worth dumping. The app only
-- ever creates an empty `Users` table, so this adds a few sensitive-looking
-- tables (credit cards, API keys, secrets, a CTF-style flag, benign products)
-- to make table/column enumeration and full dumps interesting.
--
-- The `Users` table itself is NOT seeded here — seed.sh registers every user
-- through the app's /register endpoint so they all get genuine bcrypt hashes
-- and are actually loginable. We only ensure the table exists (matching the
-- app schema: ID/Username/Password) so a fresh DB doesn't error.
--
-- Extra tables are DROP + CREATE so re-running the seed stays clean (idempotent).
-- ---------------------------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

-- ── App table: ensure it exists (schema must match the app) ────────────────
CREATE TABLE IF NOT EXISTS Users (
  ID       INT NOT NULL AUTO_INCREMENT,
  Username VARCHAR(20),
  Password VARCHAR(80),
  PRIMARY KEY (ID)
);

-- ── credit_cards: high-value dump target ───────────────────────────────────
DROP TABLE IF EXISTS credit_cards;
CREATE TABLE credit_cards (
  id          INT NOT NULL AUTO_INCREMENT,
  username    VARCHAR(20),
  card_number VARCHAR(19),
  cvv         VARCHAR(4),
  expiry      VARCHAR(5),
  card_holder VARCHAR(64),
  PRIMARY KEY (id)
);
INSERT INTO credit_cards (username, card_number, cvv, expiry, card_holder) VALUES
 ('alice',   '4539 1488 0343 6467', '312', '11/27', 'Alice Anderson'),
 ('bob',     '5500 0055 5555 5559', '908', '02/26', 'Bob Baker'),
 ('charlie', '4024 0071 5376 3455', '145', '07/28', 'Charlie Clark'),
 ('dave',    '6011 0009 9013 9424', '733', '09/25', 'Dave Davis'),
 ('grace',   '3782 822463 10005',   '4021','01/29', 'Grace Green'),
 ('olivia',  '4111 1111 1111 1111', '999', '12/30', 'Olivia Owens'),
 ('walter',  '5105 1051 0510 5100', '256', '05/27', 'Walter White');

-- ── api_keys: secrets/credentials dump target ──────────────────────────────
DROP TABLE IF EXISTS api_keys;
CREATE TABLE api_keys (
  id         INT NOT NULL AUTO_INCREMENT,
  service    VARCHAR(40),
  api_key    VARCHAR(80),
  secret     VARCHAR(80),
  created_at DATE,
  PRIMARY KEY (id)
);
-- NOTE: values below are DEFANGED fakes (FAKE-<service>-...) so GitHub secret
-- scanning / push protection won't match a real provider key format. They are
-- still perfectly good dump targets for the SQLi demo.
INSERT INTO api_keys (service, api_key, secret, created_at) VALUES
 ('stripe',   'FAKE-stripe-live-0000111122223333', 'FAKE-stripe-whsec-4444555566667777', '2025-01-14'),
 ('aws',      'FAKE-aws-akid-EXAMPLEKEYID000000',  'FAKE-aws-secret-abcdEFGH1234ijkl5678', '2024-11-02'),
 ('sendgrid', 'FAKE-sendgrid-aB1cD2eF3gH4iJ5kL6', 'FAKE-sendgrid-secret-p8Q9r0S1t2U3v4', '2025-03-21'),
 ('github',   'FAKE-github-pat-16C7e42F292c6912', 'n/a',                                 '2025-06-08'),
 ('internal', 'root-panel-token',                 'sup3rS3cr3t_admin_2026',              '2026-02-01');

-- ── secrets: freeform sensitive notes ──────────────────────────────────────
DROP TABLE IF EXISTS secrets;
CREATE TABLE secrets (
  id      INT NOT NULL AUTO_INCREMENT,
  title   VARCHAR(80),
  content VARCHAR(255),
  PRIMARY KEY (id)
);
INSERT INTO secrets (title, content) VALUES
 ('db_root',        'MariaDB root password is "root" (change before prod!)'),
 ('vpn',            'VPN preshared key: p$k-7731-corp-tunnel'),
 ('backup_bucket',  's3://cnp-prod-backups/ (public-read misconfigured)'),
 ('admin_recovery', 'Recovery email: security@copy-n-paste.local / code 884213');

-- ── flags: CTF-style marker to prove a full dump succeeded ─────────────────
DROP TABLE IF EXISTS flags;
CREATE TABLE flags (
  id   INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(40),
  flag VARCHAR(80),
  PRIMARY KEY (id)
);
INSERT INTO flags (name, flag) VALUES
 ('sqli_dump', 'FLAG{c0py_n_p4ste_uni0n_based_dump_complete}'),
 ('boolean',   'FLAG{bl1nd_b00lean_extraction_works}');

-- ── products: benign filler so the schema looks like a real app ────────────
DROP TABLE IF EXISTS products;
CREATE TABLE products (
  id    INT NOT NULL AUTO_INCREMENT,
  name  VARCHAR(60),
  price DECIMAL(8,2),
  stock INT,
  PRIMARY KEY (id)
);
INSERT INTO products (name, price, stock) VALUES
 ('USB-C Cable 2m',       9.99,  340),
 ('Mechanical Keyboard',  89.90, 42),
 ('Noise-cancel Headset', 149.00, 17),
 ('Webcam 1080p',         59.50, 88),
 ('Laptop Stand',         34.25, 120),
 ('4K Monitor 27"',       299.99, 9);

SET FOREIGN_KEY_CHECKS = 1;
