# Inquisitor — Day 7

ARP-poisoning. Three containers on the Docker bridge `custom_net`
(`10.5.0.0/16`):

| Service      | IP          | Role                                                        |
|--------------|-------------|-------------------------------------------------------------|
| `ftp-server` | `10.5.0.17` | vsftpd, **plaintext** FTP (`ssl_enable=NO`)                 |
| `client`     | `10.5.0.18` | loops `lftp` login + upload to the server every 5s          |
| `poison`     | `10.5.0.19` | ARP-spoofer (NET_RAW/NET_ADMIN, `ip_forward=1`) — *WIP stub* |

> The `poison` ARP-spoofer is still under implementation. This README covers
> the working **client** and **ftp-server** setup and how to test them.

## Setup

Everything is orchestrated with Docker Compose via the `Makefile`.

```sh
make            # build + start all containers detached (up -d --build)
make ps         # show container status
make logs       # follow logs (last 200 lines)
make down       # stop and remove containers
make clean      # down + remove volumes
make re         # clean rebuild from scratch
```

Container names are prefixed with the compose project name `inquisitor`
(e.g. `inquisitor-client-1`). Use `make ps` to get the exact names.

## Testing the FTP server

Credentials: `ftpuser` / `ftppass`. Uploaded files land in `/home/ftpuser`
(the user's chrooted home).

Shell into the server and inspect what the client uploaded:

```sh
docker container exec -it inquisitor-ftp-server-1 ash
ls -l /home/ftpuser        # expect hello.txt, evaluator.key, work.md, place.hex
```

## Testing the client

The client automatically runs, every 5 seconds:

```sh
lftp -u ftpuser,ftppass 10.5.0.17 -e 'mirror -R /test; ls; bye'
```

which mirrors its local `/test` directory (`hello.txt`, `evaluator.key`,
`work.md`, `place.hex`) up to the server.

Follow the client to see login + upload succeed:

```sh
docker compose logs -f client
```

Or drive it manually:

```sh
docker container exec -it inquisitor-client-1 ash
lftp -u ftpuser,ftppass 10.5.0.17     # interactive session
# ls / put / bye ...
```

## Testing the poison box (WIP)

`inquisitor` takes the IP + MAC of both victims:

```sh
docker container exec -it inquisitor-poison-1 bash   # Ubuntu image → bash, not ash
./inquisitor <IP-src> <MAC-src> <IP-target> <MAC-target>
# src = ftp-server (10.5.0.17), target = client (10.5.0.18)
```

### Finding the victim MACs

Docker assigns MACs at container creation, so they **change on every
`make re`** — look them up fresh each run, never hardcode.

From the **poison** box, ping both victims to populate the ARP cache, then read
it:

```sh
docker container exec -it inquisitor-poison-1 bash
ping -c1 10.5.0.17 && ping -c1 10.5.0.18
ip neigh          # 10.5.0.17 ... lladdr <server MAC>, 10.5.0.18 ... lladdr <client MAC>
```

Or ask each container for its own MAC directly:

```sh
docker container exec inquisitor-ftp-server-1 cat /sys/class/net/eth0/address   # server MAC
docker container exec inquisitor-client-1     cat /sys/class/net/eth0/address   # client MAC
docker container exec inquisitor-poison-1     cat /sys/class/net/eth0/address   # your own MAC
```

(`ip link show eth0` works too where `iproute2` is installed — the poison image
has it; the Alpine boxes need `ip -o link` or the `/sys` path above.)

Once the spoofer is functional, run it here and watch the plaintext FTP
credentials cross the wire as the client authenticates.

## Resources
- [PCAP++ documentation](https://pcapplusplus.github.io/api-docs/v25.05/index.html)
- [vsftpd guide](https://dev.to/sahillearninglinux/ultimate-guide-to-vsftpd-configuration-files-commands-and-secure-sftp-migration-170m)
- [lftp guide](https://commandmasters.com/commands/lftp-linux/)
