#!/usr/bin/env bash
# Equivalent of `make setup && make up`:
#   1. install vagrant + virtualbox (macOS via brew, Debian-based Linux via apt)
#   2. bring up the server VM, check the join token, then bring up the worker VM
set -eo pipefail

# Run from p1/ (the Vagrantfile's directory) wherever the script is called from
P1_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$P1_DIR"

VAGRANT="${VAGRANT:-vagrant}"
SERVER="${SERVER:-mturgeonS}"
WORKER="${WORKER:-mturgeonSW}"
TOKEN_FILE="$P1_DIR/token"
VBOX_PKG="${VBOX_PKG:-virtualbox-7.1}"

UNAME_S="$(uname -s)"
UNAME_M="$(uname -m)"

case "$UNAME_M" in
	x86_64)        ARCH=amd64 ;;
	arm64|aarch64) ARCH=arm64 ;;
	*) echo "Unsupported architecture: $UNAME_M" >&2; exit 1 ;;
esac

has() { command -v "$1" >/dev/null 2>&1; }

# ---------------------------------------------------------------- setup

setup_macos() {
	# make (for scripts/Makefile) comes with the Xcode Command Line Tools
	if has make; then echo "[ok]   make already installed"
	else echo "[inst] make (Xcode Command Line Tools: accept the dialog, then rerun)"; xcode-select --install || true; exit 1; fi
	if has vagrant; then echo "[ok]   vagrant already installed"
	else echo "[inst] vagrant"; brew install --cask vagrant; fi
	if has VBoxManage; then echo "[ok]   virtualbox already installed"
	else echo "[inst] virtualbox"; brew install --cask virtualbox; fi
	echo "Note: macOS may ask you to allow Oracle's system extension in"
	echo "System Settings > Privacy & Security, then require a reboot."
}

deb_make() {
	if has make; then echo "[ok]   make already installed"; return; fi
	echo "[inst] make"
	sudo apt-get update
	sudo apt-get install -y make
}

deb_vagrant() {
	if has vagrant; then echo "[ok]   vagrant already installed"; return; fi
	echo "[inst] vagrant (HashiCorp apt repository)"
	sudo apt-get update
	sudo apt-get install -y ca-certificates curl gnupg
	curl -fsSL https://apt.releases.hashicorp.com/gpg \
		| sudo gpg --dearmor --yes -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
	echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $OS_CODENAME main" \
		| sudo tee /etc/apt/sources.list.d/hashicorp.list >/dev/null
	sudo apt-get update
	sudo apt-get install -y vagrant
}

deb_virtualbox() {
	if has VBoxManage; then echo "[ok]   virtualbox already installed"; return; fi
	echo "[inst] $VBOX_PKG (Oracle apt repository)"
	sudo apt-get update
	sudo apt-get install -y ca-certificates curl gnupg
	curl -fsSL https://www.virtualbox.org/download/oracle_vbox_2016.asc \
		| sudo gpg --dearmor --yes -o /usr/share/keyrings/oracle-virtualbox-2016.gpg
	echo "deb [arch=$ARCH signed-by=/usr/share/keyrings/oracle-virtualbox-2016.gpg] https://download.virtualbox.org/virtualbox/debian $OS_CODENAME contrib" \
		| sudo tee /etc/apt/sources.list.d/virtualbox.list >/dev/null
	sudo apt-get update
	sudo apt-get install -y "linux-headers-$(uname -r)" dkms "$VBOX_PKG"
}

setup() {
	case "$UNAME_S" in
		Darwin) setup_macos ;;
		Linux)
			# ID and ID_LIKE cover Debian itself and derivatives (Ubuntu, Mint, ...)
			OS_ID="$(. /etc/os-release 2>/dev/null && echo "$ID $ID_LIKE")"
			OS_CODENAME="$(. /etc/os-release 2>/dev/null && echo "$VERSION_CODENAME")"
			case " $OS_ID " in
				*" debian "*|*" ubuntu "*) deb_make; deb_vagrant; deb_virtualbox ;;
				*) echo "Only Debian-based Linux is supported (found: $OS_ID)" >&2; exit 1 ;;
			esac
			;;
		*) echo "Unsupported OS: $UNAME_S" >&2; exit 1 ;;
	esac
}

# ------------------------------------------------------------------- up

up() {
	has "$VAGRANT"   || { echo "vagrant not found after setup" >&2; exit 1; }
	has VBoxManage   || { echo "VirtualBox not found after setup" >&2; exit 1; }
	# The server must exist before the worker reads /vagrant/token: keep sequential.
	"$VAGRANT" up "$SERVER" --provider=virtualbox
	[ -s "$TOKEN_FILE" ] || { echo "Server did not write $TOKEN_FILE, aborting" >&2; exit 1; }
	"$VAGRANT" up "$WORKER" --provider=virtualbox
}

setup
up
