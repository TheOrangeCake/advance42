# ft_onion

Serve a static web page as a **Tor hidden service**, with SSH access, using
only Nginx. No extra ports are opened and no firewall rules are set.

## Files

| File          | Role                                                             |
|---------------|------------------------------------------------------------------|
| `index.html`  | The static page served by Nginx.                                 |
| `nginx.conf`  | Nginx config — serves the page on port 80.                       |
| `sshd_config` | OpenSSH config — listens on port 4242, root login disabled.      |
| `torrc`       | Tor config — hidden service, maps 80 and 4242.                   |
| `Dockerfile`  | Builds the image (Nginx + OpenSSH + Tor on Alpine).              |
| `start.sh`    | Entrypoint.                                                      |


## Build & run

```
docker build -t onion .
docker run --name ft_onion -p 80:80 -p 4242:4242 onion
```

## Utility command

```
docker container rm ft_onion 
docker exec ft_onion netstat -tulpn
docker port ft_onion 
```

## Get the .onion address

Tor generates the address on first start. Three ways to read it:

```sh
# 1. From the container logs (start.sh prints it)
docker logs ft_onion | grep "Onion address"

# 2. Straight from the file (runs as root inside the container)
docker exec ft_onion cat /var/lib/tor/hidden_service/hostname

# 3. Over SSH — start.sh copies a readable copy into the user's home
ssh hoannguy@localhost -p 4242        # password: 42Lausanne
cat ~/onion_address
```