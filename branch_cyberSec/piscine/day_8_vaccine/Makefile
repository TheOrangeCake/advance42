POSTGRESQL_COMPOSE = docker compose -f vul_env/vuln-bank/docker-compose.yml

.PHONY: clean mysql postgresql mysql-down postgresql postgresql-up postgresql-down build postgresql-clean postgresql-re

all: mysql postgresql

clean: mysql-down postgresql-clean

mysql:
	cd ./vul_env/copy-n-paste && make install

mysql-down:
	cd ./vul_env/copy-n-paste && make compose-down

postgresql:
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

postgresql-re: postgresql-clean postgresql
