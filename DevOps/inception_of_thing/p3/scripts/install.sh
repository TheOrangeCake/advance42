#!/usr/bin/env bash
# Installe tout ce qu'il faut pour la partie 3 : Docker, K3d, kubectl et le CLI Argo CD.
# Supporte Debian 13 (Trixie) et macOS (via Homebrew).
# Relançable : les outils déjà présents sont ignorés.

set -euo pipefail

K3D_VERSION="v5.9.0"
ARGOCD_VERSION="v3.6.2" # même version que p3/confs/bootstrap/kustomization.yaml

has() { command -v "$1" > /dev/null 2>&1; }

# Vérifier le système d'exploitation
check_environment() {
    OS="$(uname -s)"

    # root non permis (et brew refuse de tourner en root)
    if [[ "$EUID" -eq 0 ]]; then
        echo "Error: run this script as a regular user."
        exit 1
    fi

    case "$OS" in
        Linux)  check_debian ;;
        Darwin) check_macos ;;
        *)
            echo "Error: unsupported OS: $OS (Debian 13 or macOS required)."
            exit 1
            ;;
    esac

    # PS: je suis sur un vieux mac donc j'ai mis les deux
    case "$ARCH" in
        amd64|arm64)
            ;;
        *)
            echo "Error: unsupported architecture: $ARCH"
            exit 1
            ;;
    esac

    echo "Architecture: $ARCH"
}

check_debian() {
    source /etc/os-release

    if [[ "$ID" != "debian" ]]; then
        echo "Error: Debian is required."
        exit 1
    fi

    # check Debian 13 (Trixie)
    if [[ "$VERSION_CODENAME" != "trixie" ]]; then
        echo "Error: Debian 13 (Trixie) is required."
        exit 1
    fi

    # Détecte l'architecture
    ARCH="$(dpkg --print-architecture)"

    echo "Operating system: $PRETTY_NAME"

    # pas de mot de passe demandé si sudo n'en a pas besoin (ex: VM Vagrant sans TTY)
    sudo -n true 2> /dev/null || sudo -v
}

check_macos() {
    if ! has brew; then
        echo "Error: Homebrew is required on macOS. Install it from https://brew.sh"
        exit 1
    fi

    case "$(uname -m)" in
        x86_64) ARCH="amd64" ;;
        arm64)  ARCH="arm64" ;;
        *)      ARCH="$(uname -m)" ;;
    esac

    echo "Operating system: macOS $(sw_vers -productVersion)"
}

# ------------------------------------------------------------------ Debian

# Installe les dépendances nécessaires
install_dependencies() {
    echo "[1/6] Installing dependencies..."

    sudo apt-get update
    sudo apt-get install -y \
        ca-certificates \
        curl \
        git \
        make
}

# wil42/playground n'existe qu'en amd64 : sur arm64 il faut l'émulation qemu,
# sinon le pod crashe avec "exec format error"
install_amd64_emulation() {
    if [[ "$ARCH" != "arm64" ]]; then
        return
    fi

    echo "arm64 host: enabling amd64 emulation (qemu-user-binfmt)..."
    sudo apt-get install -y qemu-user-binfmt
}

# Installe et configure Docker depuis son dépôt officiel
install_docker() {
    echo "[2/6] Installing Docker..."

    if has docker; then
        echo "Docker already installed, skipping."
        return
    fi

    sudo install -m 0755 -d /etc/apt/keyrings

    # vérifier la signature du dépôt
    sudo curl -fsSL \
        https://download.docker.com/linux/debian/gpg \
        -o /etc/apt/keyrings/docker.asc

    sudo chmod a+r /etc/apt/keyrings/docker.asc

    #  Ajouter le dépôt officiel Docker en fonction de la version
    sudo tee /etc/apt/sources.list.d/docker.sources \
        > /dev/null <<EOF
Types: deb
URIs: https://download.docker.com/linux/debian
Suites: ${VERSION_CODENAME}
Components: stable
Architectures: ${ARCH}
Signed-By: /etc/apt/keyrings/docker.asc
EOF

    sudo apt-get update

    # Installer Docker
    sudo apt-get install -y \
        docker-ce \
        docker-ce-cli \
        containerd.io

    # Démarrer Docker
    sudo systemctl enable --now docker

    # Ajouter l'utilisateur actuel au groupe Docker
    sudo usermod -aG docker "$(id -un)"
}

# Télécharger et installe K3d
install_k3d() {
    echo "[3/6] Installing K3d..."

    if has k3d; then
        echo "K3d already installed, skipping."
        return
    fi

    # Construire l'URL de téléchargement selon la version
    local url
    url="https://github.com/k3d-io/k3d/releases/download"
    url="${url}/${K3D_VERSION}/k3d-linux-${ARCH}"

    curl -fsSL "$url" -o "${TMP_DIR}/k3d"

    sudo install -m 0755 \
        "${TMP_DIR}/k3d" \
        /usr/local/bin/k3d
}

# # Installe une version de kubectl compatible K3d est par default K3s
install_kubectl() {
    echo "[4/6] Installing kubectl..."

    if has kubectl; then
        echo "kubectl already installed, skipping."
        return
    fi

    local k3s_version
    local kube_version
    local base_url

    k3s_version="$(
        k3d version | awk '/k3s version/ {print $3}'
    )"

    # Supprime les suffixes propres à K3s
    # Exemple : v1.31.5-k3s1 devient v1.31.5.
    kube_version="${k3s_version%%+*}"
    kube_version="${kube_version%%-*}"

    # Vérifier que la version obtenue respecte le format vX.Y.Z
    if [[ ! "$kube_version" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo "Error: unable to determine Kubernetes version."
        exit 1
    fi

    echo "Installing kubectl ${kube_version}"

    base_url="https://dl.k8s.io/release"
    base_url="${base_url}/${kube_version}/bin/linux/${ARCH}"

    curl -fsSL \
        "${base_url}/kubectl" \
        -o "${TMP_DIR}/kubectl"

    curl -fsSL \
        "${base_url}/kubectl.sha256" \
        -o "${TMP_DIR}/kubectl.sha256"

    # Compare l'empreinte du fichier téléchargé à celle fournie
    (
        cd "$TMP_DIR"

        echo "$(cat kubectl.sha256)  kubectl" \
            | sha256sum --check
    )

    sudo install -m 0755 \
        "${TMP_DIR}/kubectl" \
        /usr/local/bin/kubectl
}

# Installe le CLI Argo CD (pratique pour la défense : argocd app list, etc.)
install_argocd_cli() {
    echo "[5/6] Installing Argo CD CLI..."

    if has argocd; then
        echo "Argo CD CLI already installed, skipping."
        return
    fi

    curl -fsSL \
        "https://github.com/argoproj/argo-cd/releases/download/${ARGOCD_VERSION}/argocd-linux-${ARCH}" \
        -o "${TMP_DIR}/argocd"

    sudo install -m 0755 \
        "${TMP_DIR}/argocd" \
        /usr/local/bin/argocd
}

install_debian() {
    install_dependencies
    install_amd64_emulation
    install_docker
    install_k3d
    install_kubectl
    install_argocd_cli
}

# ------------------------------------------------------------------- macOS

# brew install sans upgrade si déjà présent
brew_install() {
    local pkg
    for pkg in "$@"; do
        if brew list --formula "$pkg" > /dev/null 2>&1 || has "$pkg"; then
            echo "$pkg already installed, skipping."
        else
            brew install "$pkg"
        fi
    done
}

# Docker Desktop : installé via cask, puis démarré si le daemon ne répond pas
install_docker_macos() {
    echo "[2/6] Installing Docker Desktop..."

    if has docker; then
        echo "Docker already installed, skipping."
    else
        brew install --cask docker
    fi

    if ! docker info > /dev/null 2>&1; then
        echo "Starting Docker Desktop..."
        open -a Docker

        local i
        for i in $(seq 1 60); do
            docker info > /dev/null 2>&1 && break
            sleep 2
        done
    fi
}

install_macos() {
    echo "[1/6] Checking Homebrew..."
    brew --version | head -1

    # make et git viennent avec les Command Line Tools d'Xcode
    if ! has make || ! has git; then
        echo "Installing Xcode Command Line Tools (make, git): accept the dialog, then rerun this script."
        xcode-select --install || true
        exit 1
    fi

    install_docker_macos

    echo "[3/6] Installing K3d..."
    brew_install k3d

    echo "[4/6] Installing kubectl..."
    brew_install kubectl

    echo "[5/6] Installing Argo CD CLI..."
    brew_install argocd
}

# ------------------------------------------------------------------ verify

# Vérifie que tous les outils ont été correctement installés.
verify_installation() {
    echo "[6/6] Verifying installation..."

    docker --version
    if [[ "$OS" == "Linux" ]]; then
        sudo docker info > /dev/null
    fi

    k3d version
    kubectl version --client
    argocd version --client --short
    git --version
    make --version | head -1

    echo ""
    echo "Installation completed successfully."

    if docker info > /dev/null 2>&1; then
        echo "Docker is ready."
    elif [[ "$OS" == "Linux" ]]; then
        echo "Reconnect to activate Docker permissions."
        echo "Alternatively, run: newgrp docker"
    else
        echo "Error: Docker daemon not reachable, start Docker Desktop."
        exit 1
    fi
}

main() {
    check_environment

    TMP_DIR="$(mktemp -d)"
    trap 'rm -rf "$TMP_DIR"' EXIT

    if [[ "$OS" == "Darwin" ]]; then
        install_macos
    else
        install_debian
    fi

    verify_installation
}

main "$@"
