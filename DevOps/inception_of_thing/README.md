# Inception_of_Things
Inception of Things implementation for 42 Advance cursus

# Part 1

because of a Vagrant bug on MacOS, Vagrant thinks all ports are already in use on the host machine when port forwarding, which means the setup fails when specifying port forwarding.This is due to Vagrant's port check that has the bug. We can still ssh into the machines through `vagrant ssh` but we don't know the ports exactly. Should work on linux (try `ssh -p <name> 22`). Solution to this is to write a small Ruby override of Vagrant's port check OR to add the port forwarding through virtual box: `vb.customize ["modifyvm", :id, "--natpf1", "web,tcp,127.0.0.1,8080,,80"]`. This binds port forwarding to virtual box. 

### diagnosis commands
- `vagrant ssh <server or worker>`
- `kubectl get nodes -o wide` (not on worker, only the server holds `kubeconfig`)
- `sudo ifconfig eth1` on MacOS, `ip a show eth1` on Linux


## Part 2
We will tackle 3 concepts in part 2: `Deployment`, `Service` and `Ingress`.

### Deployment
This component will create and manage the Pods. It specifies the desired state and number of pods. In this configuration we will define the number of replicas, what to deploy and some configurations like ports, etc.

### Ingress
This component will configure the routing. Client request will first arrive here, then forwarded to matching `Service`. To show Ingress:
```sh
kubectl get ingress
kubectl describe ingress app-ingress
```

### Service
This component is the layer between `Ingress` and Pods created by `Deployment`. Since Pods can be replaced, `Service` provides a stable identity to a group of Pods and also acts as a load balancer.

### Test
- Test with different host name:
```sh
vagrant up --provision
curl -H "Host: app1.com" http://192.168.56.110
curl -H "Host: app2.com" http://192.168.56.110
curl -H "Host: app3.com" http://192.168.56.110
curl http://192.168.56.110
```
- Test with browser without setting host name manually by running the command below then type 
`http://app1.com`
```sh
echo "192.168.56.110 app1.com app2.com app3.com" | sudo tee -a /etc/hosts
```
- Test replica by refreshing the app2 page several time, it should get different pod from time to time.



## Part 3
K3d cluster + Argo CD, deploying [wil42/playground](https://hub.docker.com/r/wil42/playground) from the public app repo
[mturgeon_argocd_lab](https://github.com/Maxkirit/mturgeon_argocd_lab) into the `dev` namespace.

- `p3/scripts/install.sh`: installs Docker, k3d, kubectl and the argocd CLI (Debian 13 or macOS with Homebrew)
- `p3/scripts/Makefile`: `setup`, `up` (cluster + Argo CD + root app), `password`, `down`
- `p3/confs/cluster`: k3d config (ports 8080 -> Traefik, 9090 -> Argo CD UI, 8888 -> app)
- `p3/confs/bootstrap`: Argo CD install (kustomize) + root app ("app of apps") pointing to `p3/confs/argocd`
- `p3/confs/argocd`: AppProject `apps` and the `wil42-app` Application

### Test
```sh
make -C p3/scripts setup up
export KUBECONFIG=$PWD/p3/.kubeconfig
kubectl get ns                      # argocd, dev
kubectl get pods -n dev
curl http://localhost:8888/         # {"status":"ok", "message": "v1"}
make -C p3/scripts password         # Argo CD UI: https://localhost:9090 (user admin)
```
Switch `wil42/playground:v1` to `v2` in the app repo's `wil42_app/deployment.yaml`, push to `main`, and Argo CD syncs it (about 3 minutes, or hit Refresh in the UI). Details: `docs/k3s_k3d.md`.

## Resources
- [Installation guide for K3S](https://docs.k3s.io/installation/configuration)
- [Command kubectl documentation](https://kubernetes.io/docs/reference/kubectl/)
- [Anatomy of kubernetes yaml](https://k3s.guide/docs/kubernetes/anatomy-of-kubernetes-yaml/)
- [Part 2 docker image](https://hub.docker.com/r/paulbouwer/hello-kubernetes)
