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
# 1. From the container logs
docker logs ft_onion | grep "Onion address"

# 2. Straight from the file
docker exec ft_onion cat /var/lib/tor/hidden_service/hostname

# 3. Over SSH
cat ~/onion_address
```

## Access Tor
```sh
ONION=$(docker exec ft_onion cat /var/lib/tor/hidden_service/hostname)

# web page through Tor
curl --socks5-hostname 127.0.0.1:9050 "http://$ONION/"

# SSH through Tor                       (password: 42Lausanne)
ssh -o ProxyCommand='nc -X 5 -x 127.0.0.1:9050 %h %p' "hoannguy@$ONION" -p 4242
#   or, if torsocks is installed:  torsocks ssh "hoannguy@$ONION" -p 4242
```

### How the SSH command works

SSH has no built-in SOCKS support, so we tell it to obtain its connection by
running an external program that does. SSH pipes its traffic through that
program's stdin/stdout, then runs its normal encrypted handshake over the pipe —
unaware that Tor sits in the middle.

| Token                  | Meaning                                                                 |
|------------------------|-------------------------------------------------------------------------|
| `-o ProxyCommand='…'`  | Use the command's stdin/stdout as the connection instead of a direct TCP socket. |
| `nc`                   | netcat — opens a raw connection and bridges it to stdin/stdout.         |
| `-X 5`                 | Proxy protocol: `5` = SOCKS5 (so the `.onion` name is resolved by Tor, not locally). |
| `-x 127.0.0.1:9050`    | Proxy address:port — the Tor SOCKS proxy.                               |
| `%h`                   | Placeholder SSH replaces with the target host (the `.onion` address).   |
| `%p`                   | Placeholder SSH replaces with the target port (`4242`).                 |

So netcat opens a SOCKS5 connection through Tor to `<onion>:4242` and hands the
pipe to SSH. The chain is: `ssh → SOCKS5 proxy 127.0.0.1:9050 (Tor) → Tor
network → hidden service`. `.onion` names only resolve inside Tor, which is why
SOCKS5 (`-X 5`) is required — it passes the hostname to Tor instead of leaking
it to a local DNS resolver.

## Check no open ports

```sh
curl localhost                     # connection refused
ssh hoannguy@localhost -p 4242     # connection refused
nmap -p 80,4242 localhost          # both closed
docker port ft_onion               # no published ports
```
