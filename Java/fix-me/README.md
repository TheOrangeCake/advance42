# Fix-me
A multi-component FIX 4.4 trading simulator: a router, brokers, and markets that talk over TCP, built with Java and Hibernate.<br>
The project demonstrates **multithreaded network programming** with the **Java executor framework**, a **resilient messaging platform** with persistent fail-over, and end-to-end **FIX 4.4 message validation** (body length, required tags, checksum).<br>

<br>
Created by Nguyen NGUYEN (hoannguy) from 42 Lausanne.

---

## Features
- Router (Message) accepts brokers on port 5000 and markets on port 5001
- Router assigns each client a unique persistent 6-digit UID and tracks it in a routing table
- Brokers send Buy / Sell orders (FIX `35=D`) and receive Executed / Rejected statuses (FIX `35=8` / `35=3`)
- Markets process orders against an instrument list with per-symbol stock and price, returning Executed or Rejected
- FIX 4.4 messages validated end-to-end: required tags, field order, body length, and checksum
- All transactions persisted to a shared H2 database (broker orders, market orders, message router UID counter)
- Fail-over: any component can crash and reconnect; PENDING transactions are replayed and the system reaches a consistent state without operator action
- Idempotent market processing: re-sending the same order returns the same stored response
- Human-readable FIX translator next to every raw message printed on screen, for easier debugging
- H2 web console at `http://localhost:8082` for browsing the DB live

---

## Tech Stack

- Java 25
- Hibernate ORM for persistence
- H2 in shared TCP mode for the database
- Maven multi-module build (7 modules)

---

## Getting Started

Clean and build:
<pre>./mvnw clean package</pre>

Then start the four components, **in this order**, each in its own terminal:

1. Database — starts the shared H2 server on `4242` and the web console on `8082`:<pre>java -jar database_module/target/database.jar</pre>
2. Message (router) — listens on `5000` for brokers and `5001` for markets:<pre>java -jar message_module/target/message.jar</pre>
3. Market — connects to the router, prompts for the server IP, then for a UID (enter `new`, `none`, or leave blank for a fresh UID):<pre>java -jar market_module/target/market.jar</pre>
4. Broker — same UID prompt; then prompts for Buy/Sell, instrument, quantity, price, and target market UID:<pre>java -jar broker_module/target/broker.jar</pre>

You can start as many brokers and markets as you like; the router will assign each its own UID.

### Watching the database

Open `http://localhost:8082` in a browser and connect with:
- **JDBC URL:** `jdbc:h2:tcp://localhost:4242/file:./data/h2/fixMeDb`
- **User:** `sa`
- **Password:** _(blank)_

### Try the fail-over

1. Start everything as above and submit a few orders from the broker.
2. Kill any component (broker, market, or router) mid-session with `Ctrl+C`.
3. Restart it — on logon it queries the DB for `PENDING` transactions belonging to its UID and replays them. The market deduplicates by order ID, so the broker eventually gets the original response.

---

### Resources

- FIX 4.4 tag dictionary: [onixs.biz/fix-dictionary/4.4](https://www.onixs.biz/fix-dictionary/4.4/fields_by_tag.html)
