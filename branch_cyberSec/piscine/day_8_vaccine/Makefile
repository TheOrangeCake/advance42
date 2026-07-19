POSTGRESQL_COMPOSE = docker compose -f vuln-bank/docker-compose.yml

.PHONY: postgresql-run postgresql-up postgresql-down build postgresql-clean postgresql-re

run:
	$(POSTGRESQL_COMPOSE) up -d --build

postgresql-run:
	$(POSTGRESQL_COMPOSE) up -d --build
	
postgresql-up:
	$(POSTGRESQL_COMPOSE) up -d

postgresql-down:
	$(POSTGRESQL_COMPOSE) down

postgresql-build:
	$(POSTGRESQL_COMPOSE) build

postgresql-clean:
	$(POSTGRESQL_COMPOSE) down -v

postgresql-re: postgresql-clean postgresql-run