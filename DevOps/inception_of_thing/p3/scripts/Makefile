CLUSTER := argo-lab

# p3/ (parent of this Makefile's directory), whatever directory make is run from
SELF   := $(abspath $(lastword $(MAKEFILE_LIST)))
P3_DIR := $(abspath $(dir $(SELF))..)
CONFS  := $(P3_DIR)/confs

# what you'd find in .kube/config but only for this cluster (a good way to separate cluster configs)
# P3_KUBECONFIG can be overridden (the p3 VM sets it, so the VM and the Mac don't overwrite each other's file
# in the shared repo). Not reading KUBECONFIG itself: `make cluster` overwrites the file, it must never be ~/.kube/config
P3_KUBECONFIG ?= $(P3_DIR)/.kubeconfig
export KUBECONFIG := $(P3_KUBECONFIG)
# GITHUB_USER / GITHUB_ACCESS_TOKEN: Argo CD needs them to read this (private) repo
-include $(P3_DIR)/.env
# exported and read as $$VAR in recipes, so the token never appears in make's expanded commands (make -n)
export GITHUB_USER GITHUB_ACCESS_TOKEN

# installs docker, k3d, kubectl and the argocd CLI (Debian 13 or macOS)
setup:
	bash $(P3_DIR)/scripts/install.sh

# @ before the command silence, would leak secret otherwise
# the repo is private: without the token, the root app fails with "Repository not found"
repo-secret:
	@if [ -z "$$GITHUB_ACCESS_TOKEN" ]; then \
		echo "WARNING: no GITHUB_ACCESS_TOKEN in $(P3_DIR)/.env, skipping repo secret."; \
		echo "         Argo CD can't read the private repo without it (root app: 'Repository not found')."; \
	else \
		kubectl create secret generic platform-repo -n argocd \
		  --from-literal=type=git \
		  --from-literal=url=https://github.com/TheOrangeCake/Inception_of_Things.git \
		  --from-literal=username="$$GITHUB_USER" \
		  --from-literal=password="$$GITHUB_ACCESS_TOKEN" --dry-run=client -o yaml | kubectl apply -f - && \
		kubectl label secret platform-repo -n argocd argocd.argoproj.io/secret-type=repository --overwrite; \
	fi

cluster:
	k3d cluster create --config $(CONFS)/cluster/k3d_config.yaml
	k3d kubeconfig get $(CLUSTER) > $(KUBECONFIG)

# can't run repo-secret command as dependency as it needs the cluster to be applied
bootstrap:
# server-side is bc argocd files are too large to be loaded on some small nodes so you don't download the yml
# force-conflicts is standard so your config you're applying always overwrites
	kubectl apply -k $(CONFS)/bootstrap/ --server-side --force-conflicts
	kubectl wait --for=condition=available deploy --all -n argocd --timeout=300s
	$(MAKE) -f $(SELF) repo-secret
	kubectl apply -f $(CONFS)/bootstrap/root-app.yaml

up: cluster bootstrap

# initial admin password for the Argo CD UI (https://localhost:9090, user: admin)
password:
	@kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' | base64 -d; echo

down:
	k3d cluster delete $(CLUSTER)
	rm -f $(KUBECONFIG)

.PHONY: setup repo-secret cluster bootstrap up password down
