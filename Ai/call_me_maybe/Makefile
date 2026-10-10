SRC = src

all: run

install:
	@uv sync

run:
	@uv run python3 -m $(SRC)

debug:
	echo "TODO"

clean:
	@echo "Cleaning ..."
	@find . -path ./.venv -prune -o -type d -name __pycache__ -prune -exec rm -rf {} +
	@rm -rf .mypy_cache
	@echo "Done"

lint:
	uv run flake8 $(SRC)
	uv run mypy $(SRC) --warn-return-any --warn-unused-ignores --ignore-missing-imports --disallow-untyped-defs --check-untyped-defs

lint-strict:
	uv run flake8 $(SRC)
	uv run mypy $(SRC) --strict

.PHONY: all install run debug clean lint lint-strict
