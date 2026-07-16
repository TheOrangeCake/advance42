# Vaccine

SQL injection exercise. Written in JavaScript (Node.js, `node:sqlite`).

## Database schema

Results are persisted to a SQLite file (`vaccine.sqlite` by default). A scan owns
its vulnerabilities and the enumerated backend schema (databases → tables →
columns → dumped rows).

```mermaid
erDiagram
    SCAN ||--o{ VULNERABILITY : "has"
    SCAN ||--o{ DB_NAME : "enumerates"
    DB_NAME ||--o{ TBL_NAME : "contains"
    TBL_NAME ||--o{ COL_NAME : "contains"
    TBL_NAME ||--o{ ROW_DUMP : "dumps"

    SCAN {
        integer id PK
        text url
        text method
        text db_engine
        text created_at
    }
    VULNERABILITY {
        integer id PK
        integer scan_id FK
        text parameter
        text payload
        text technique
    }
    DB_NAME {
        integer id PK
        integer scan_id FK
        text name
    }
    TBL_NAME {
        integer id PK
        integer db_id FK
        text name
    }
    COL_NAME {
        integer id PK
        integer tbl_id FK
        text name
        text data_type
    }
    ROW_DUMP {
        integer id PK
        integer tbl_id FK
        integer row_index
        text data
    }
```

`ROW_DUMP.data` holds one dumped record per row as a JSON object
(`{"id":"1","user":"admin",...}`).

## Ressources
- [Detect database fingerprint](https://www.sqlinjection.net/database-fingerprinting/)
