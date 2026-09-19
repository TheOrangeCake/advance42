COMPOSE_FILE := camagru.yaml
COMPOSE      := docker compose -f $(COMPOSE_FILE)

.DEFAULT_GOAL := help

all: up

up:
	$(COMPOSE) up -d
	@$(MAKE) --no-print-directory ps

build:
	$(COMPOSE) build

rebuild:
	$(COMPOSE) build --no-cache

down:
	$(COMPOSE) down --remove-orphans

re: down rebuild up

ps:
	$(COMPOSE) ps

logs:
	$(COMPOSE) logs -f --tail=100

clean: down

fclean:
	@echo "!! removing containers, volumes and built images"
	$(COMPOSE) down -v --remove-orphans --rmi local

help:
	@echo "Camagru targets:"
	@echo "  up        start all services in the background"
	@echo "  down      stop and remove containers"
	@echo "  re        down + rebuild + up"
	@echo "  build     build images"
	@echo "  ps        list services"
	@echo "  logs      follow logs"
	@echo "  clean     alias for down"
	@echo "  fclean    down + DELETE volumes and built images"

.PHONY: all up build rebuild down re ps logs clean fclean help
