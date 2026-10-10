DEPENDENCY = dependency.txt
PYTHON = venv/bin/python3
PIP = venv/bin/pip
FLAKE = venv/bin/flake8
MYPY = venv/bin/mypy

SRC = src

all: run

install:
	$(PYTHON) -m venv venv
	$(PIP) install -r $(DEPENDENCY)

run:
	echo "TODO"

debug:
	echo "TODO"

clean:
	echo "TODO"

lint:
	$(FLAKE) $(SRC)
	$(MYPY) $(SRC) --warn-return-any --warn-unused-ignores --ignore-missing-imports --disallow-untyped-defs --check-untyped-defs

lint-strict:
	$(FLAKE) $(SRC)
	$(MYPY) $(SRC) --strict

freeze:
	$(PIP) freeze > $(DEPENDENCY)

fclean:
	echo "TODO"

PHONY: all install run debug clean lint line-strict freeze fclean
