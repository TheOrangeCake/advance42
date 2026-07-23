#!/usr/bin/env bash
#
# Seed the copy-n-paste MariaDB (`a1db`) with data so the `vaccine` SQLi tool
# has something worth dumping.
#
#   - Every user is created through the app's /register endpoint so they get a
#     genuine bcrypt hash (cost 14) and are ALL loginable.
#   - Extra tables (credit_cards, api_keys, secrets, flags, products) are loaded
#     from seed.sql via `docker exec` into the mysqldb container (avoids the
#     broken 3307 host port mapping).
#
# Run automatically by `make install`/`make seed`, or standalone:
#     ./deployments/seed.sh
#
# Requires the stack to be running (docker compose up / `make compose`).

set -euo pipefail

DB_CONTAINER="mysqldb"
DB_ROOT_PW="root"
DB_NAME="a1db"
APP_URL="http://localhost:10001"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# user:password pairs — all registered via the app, all loginable.
USERS=(
  "admin:password123"     "jdoe:letmein"        "operator:hunter2"
  "alice:alicePw!1"       "bob:bobPw!2"         "charlie:charliePw!3"
  "dave:davePw!4"         "erin:erinPw!5"       "frank:frankPw!6"
  "grace:gracePw!7"       "heidi:heidiPw!8"     "ivan:ivanPw!9"
  "judy:judyPw!10"        "mallory:malloryPw!11" "niaj:niajPw!12"
  "olivia:oliviaPw!13"    "peggy:peggyPw!14"    "rupert:rupertPw!15"
  "sybil:sybilPw!16"      "trent:trentPw!17"    "victor:victorPw!18"
  "walter:walterPw!19"    "wendy:wendyPw!20"
)

# ── 1. Wait for MariaDB to accept connections ──────────────────────────────
echo "==> Waiting for MariaDB container '${DB_CONTAINER}'..."
if ! docker ps --format '{{.Names}}' | grep -qx "${DB_CONTAINER}"; then
  echo "!!  Container '${DB_CONTAINER}' is not running. Start the stack first:" >&2
  echo "      make compose   (or: docker compose -f deployments/docker-compose.yml up -d --build)" >&2
  exit 1
fi

for i in $(seq 1 60); do
  if docker exec "${DB_CONTAINER}" mysqladmin ping -uroot -p"${DB_ROOT_PW}" --silent >/dev/null 2>&1; then
    break
  fi
  if [ "${i}" -eq 60 ]; then
    echo "!!  MariaDB did not become ready in time." >&2
    exit 1
  fi
  sleep 1
done
echo "    MariaDB is up."

# ── 2. Load extra tables (also ensures Users exists) ───────────────────────
echo "==> Loading seed.sql (extra tables)..."
docker exec -i "${DB_CONTAINER}" mysql -uroot -p"${DB_ROOT_PW}" "${DB_NAME}" < "${SCRIPT_DIR}/seed.sql"

# ── 3. Register every user through the app (real bcrypt hashes) ────────────
echo "==> Waiting for app at ${APP_URL}..."
for i in $(seq 1 60); do
  if curl -s -f "${APP_URL}" >/dev/null 2>&1; then
    break
  fi
  if [ "${i}" -eq 60 ]; then
    echo "!!  App not reachable at ${APP_URL}. Is it still starting?" >&2
    exit 1
  fi
  sleep 1
done

echo "==> Registering ${#USERS[@]} users via the app (bcrypt cost 14, this takes a bit)..."
register() {
  local user="$1" pass="$2"
  curl -s -o /dev/null -X POST "${APP_URL}/register" \
    -H 'Content-Type: application/json' \
    -d "{\"user\":\"${user}\",\"pass\":\"${pass}\",\"passcheck\":\"${pass}\"}" \
    && echo "    + ${user} / ${pass}"
}
for pair in "${USERS[@]}"; do
  register "${pair%%:*}" "${pair#*:}"
done

# ── 4. Summary ─────────────────────────────────────────────────────────────
echo "==> Row counts:"
docker exec "${DB_CONTAINER}" mysql -uroot -p"${DB_ROOT_PW}" "${DB_NAME}" -N -e "
  SELECT 'Users',        COUNT(*) FROM Users
  UNION ALL SELECT 'credit_cards', COUNT(*) FROM credit_cards
  UNION ALL SELECT 'api_keys',     COUNT(*) FROM api_keys
  UNION ALL SELECT 'secrets',      COUNT(*) FROM secrets
  UNION ALL SELECT 'flags',        COUNT(*) FROM flags
  UNION ALL SELECT 'products',     COUNT(*) FROM products;" \
  | sed 's/^/    /'

echo "==> Done. All users loginable (e.g. admin / password123)."
