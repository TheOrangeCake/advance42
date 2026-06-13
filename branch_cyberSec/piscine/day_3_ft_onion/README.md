# ft_onion

Serve a static web page as a **Tor hidden service**, with SSH access, using
only Nginx. No ports are opened and no firewall rules are set: nginx and sshd
bind to `127.0.0.1` only, so the **only** way to reach them is through the
`.onion` address over Tor.

## Files

| File          | Role                                                             |
|---------------|------------------------------------------------------------------|
| `index.html`  | The static page served by Nginx.                                 |
| `nginx.conf`  | Nginx config — serves the page on `127.0.0.1:80`.                |
| `sshd_config` | OpenSSH config — listens on `127.0.0.1:4242`, root login off.    |
| `torrc`       | Tor config — hidden service, maps 80 and 4242.                   |
| `Dockerfile`  | Builds the image (Nginx + OpenSSH + Tor on Alpine).              |
| `start.sh`    | Entrypoint.                                                      |


## Build & run

```
docker build -t onion .
docker run --name ft_onion onion
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

# 3. Over SSH (once connected) — start.sh drops a readable copy in $HOME
cat ~/onion_address
```

## Access Tor
```sh
ONION=$(docker exec ft_onion cat /var/lib/tor/hidden_service/hostname)

# web page through Tor
curl --socks5-hostname 127.0.0.1:9050 "http://$ONION/"

# SSH through Tor                       (password: 42Lausanne)
torsocks ssh "hoannguy@$ONION" -p 4242
```

## Proof that no ports are open

```sh
curl localhost                     # connection refused
ssh hoannguy@localhost -p 4242     # connection refused
nmap -p 80,4242 localhost          # both closed
docker port ft_onion               # no published ports
```

```sh
docker exec ft_onion netstat -tulpn
#   127.0.0.1:80    nginx
#   127.0.0.1:4242  sshd
#   127.0.0.1:9050  tor (SOCKS, loopback only)
```