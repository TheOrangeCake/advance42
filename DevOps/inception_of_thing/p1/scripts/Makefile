SHELL       := /bin/bash
.SHELLFLAGS := -eo pipefail -c
# The server must exist before the worker reads /vagrant/token: never parallelize.
.NOTPARALLEL:
 
# p1/ (parent of this Makefile's directory), whatever directory make is run from
P1_DIR     := $(abspath $(dir $(lastword $(MAKEFILE_LIST)))..)
# Tell vagrant where the Vagrantfile is (and where to keep .vagrant/)
export VAGRANT_CWD := $(P1_DIR)

VAGRANT    ?= vagrant
SERVER     ?= mturgeonS
WORKER     ?= mturgeonSW
TOKEN_FILE := $(P1_DIR)/token
VBOX_PKG   ?= virtualbox-7.1
 
UNAME_S := $(shell uname -s)
UNAME_M := $(shell uname -m)
 
ifeq ($(UNAME_M),x86_64)
  ARCH := amd64
else ifneq ($(filter arm64 aarch64,$(UNAME_M)),)
  ARCH := arm64
else
  $(error Unsupported architecture: $(UNAME_M))
endif
 
ifeq ($(UNAME_S),Darwin)
  OS_SETUP := setup-macos
else ifeq ($(UNAME_S),Linux)
  # ID and ID_LIKE cover Debian itself and derivatives (Ubuntu, Mint, ...)
  OS_ID       := $(shell . /etc/os-release 2>/dev/null && echo "$$ID $$ID_LIKE")
  OS_CODENAME := $(shell . /etc/os-release 2>/dev/null && echo "$$VERSION_CODENAME")
  ifneq ($(filter debian ubuntu,$(OS_ID)),)
    OS_SETUP := setup-debian
  else
    $(error Only Debian-based Linux is supported (found: $(OS_ID)))
  endif
else
  $(error Unsupported OS: $(UNAME_S))
endif

setup: $(OS_SETUP)
 
setup-macos:
	@if command -v vagrant >/dev/null 2>&1; then echo "[ok]   vagrant already installed"; \
	else echo "[inst] vagrant"; brew install --cask vagrant; fi
	@if command -v VBoxManage >/dev/null 2>&1; then echo "[ok]   virtualbox already installed"; \
	else echo "[inst] virtualbox"; brew install --cask virtualbox; fi
	@echo "Note: macOS may ask you to allow Oracle's system extension in"
	@echo "System Settings > Privacy & Security, then require a reboot."
 
setup-debian: deb-vagrant deb-virtualbox
 
deb-vagrant:
	@if command -v vagrant >/dev/null 2>&1; then echo "[ok]   vagrant already installed"; else \
		echo "[inst] vagrant (HashiCorp apt repository)"; \
		sudo apt-get update; \
		sudo apt-get install -y ca-certificates curl gnupg; \
		curl -fsSL https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor --yes -o /usr/share/keyrings/hashicorp-archive-keyring.gpg; \
		echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(OS_CODENAME) main" \
			| sudo tee /etc/apt/sources.list.d/hashicorp.list >/dev/null; \
		sudo apt-get update; \
		sudo apt-get install -y vagrant; \
	fi
 
deb-virtualbox:
	@if command -v VBoxManage >/dev/null 2>&1; then echo "[ok]   virtualbox already installed"; else \
		echo "[inst] $(VBOX_PKG) (Oracle apt repository)"; \
		sudo apt-get update; \
		sudo apt-get install -y ca-certificates curl gnupg; \
		curl -fsSL https://www.virtualbox.org/download/oracle_vbox_2016.asc | sudo gpg --dearmor --yes -o /usr/share/keyrings/oracle-virtualbox-2016.gpg; \
		echo "deb [arch=$(ARCH) signed-by=/usr/share/keyrings/oracle-virtualbox-2016.gpg] https://download.virtualbox.org/virtualbox/debian $(OS_CODENAME) contrib" \
			| sudo tee /etc/apt/sources.list.d/virtualbox.list >/dev/null; \
		sudo apt-get update; \
		sudo apt-get install -y linux-headers-$$(uname -r) dkms $(VBOX_PKG); \
	fi
 
up:
	@command -v $(VAGRANT) >/dev/null 2>&1 || { echo "vagrant not found, run 'make setup'"; exit 1; }
	@command -v VBoxManage >/dev/null 2>&1 || { echo "VirtualBox not found, run 'make setup'"; exit 1; }
	$(VAGRANT) up $(SERVER) --provider=virtualbox
	@test -s $(TOKEN_FILE) || { echo "Server did not write $(TOKEN_FILE), aborting"; exit 1; }
	$(VAGRANT) up $(WORKER) --provider=virtualbox
 
down:
	$(VAGRANT) halt
 
clean:
	$(VAGRANT) destroy -f $(WORKER) $(SERVER)
	@rm -f $(TOKEN_FILE)
 
fclean: clean
	@rm -rf $(P1_DIR)/.vagrant
 
re: fclean up

.PHONY: setup setup-macos setup-debian deb-vagrant deb-virtualbox up down clean fclean re