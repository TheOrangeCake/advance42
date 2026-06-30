COMPOSE = docker compose -f docker-compose.yml

.PHONY: rebuild up down restart logs ps build clean

rebuild:
	$(COMPOSE) up -d --build
	
up:
	$(COMPOSE) up -d

down:
	$(COMPOSE) down

restart:
	$(COMPOSE) down
	$(COMPOSE) up -d --build

logs:
	$(COMPOSE) logs -f --tail=200

ps:
	$(COMPOSE) ps

build:
	$(COMPOSE) build

clean:
	$(COMPOSE) down -v
