#!/bin/bash

# Install k3s
# --node-ip -> set the node ip
# --write-kubeconfig-mode=644 -> run without sudo
curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC="--node-ip=192.168.56.110 --write-kubeconfig-mode=644" sh -

# Check readiness -> "NotReady" or "Ready"
until kubectl get nodes 2>/dev/null | grep -q " Ready"; do sleep 1; done

# Apply all configs
kubectl apply -f /vagrant/confs/
