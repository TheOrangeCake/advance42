# Inquisitor

ARP-poisoning MITM. Three containers on the Docker bridge `custom_net`
(`10.5.0.0/16`):

| Service      | IP          | Role                                                          |
|--------------|-------------|--------------------------------------------------------------|
| `ftp-server` | `10.5.0.17` | vsftpd, **plaintext** FTP (`ssl_enable=NO`)                  |
| `client`     | `10.5.0.18` | idle box with `lftp` + `/test` files, for driving FTP by hand |
| `poison`     | `10.5.0.19` | ARP-spoofer (NET_RAW/NET_ADMIN, `ip_forward=1`)              |

`poison` sits between the server and client, poisons both ARP caches so their
FTP traffic is routed through it, and prints the filenames from every
`RETR`/`STOR`/`APPE` command it sees. On exit (Ctrl-C) it restores the real
ARP mappings.

## Setup

Everything is orchestrated with Docker Compose via the top-level `Makefile`.

```sh
make            # build + start all containers detached (up -d --build)
make ps         # show container status
make logs       # follow logs of all services (last 200 lines)
make down       # stop and remove containers
make clean      # down + remove volumes
make re         # clean rebuild from scratch
```

Container names are prefixed with the compose project name `inquisitor`
(e.g. `inquisitor-poison-1`). Use `make ps` for the exact names. The
`inquisitor` binary is compiled **inside** the poison image at build time
(`make install && make` in `poison/Dockerfile`), so a `make re` always ships a
fresh binary.

## Getting the victim MACs

Docker assigns MACs at container creation, so they **change on every
`make re`**.

The tool takes the IP **and** MAC of both victims. It auto-detects its *own*
MAC (`poison` box), so you only need the server's and the client's.

**Direct — ask each container for its own MAC:**

```sh
docker exec inquisitor-ftp-server-1 cat /sys/class/net/eth0/address   # server MAC
docker exec inquisitor-client-1     cat /sys/class/net/eth0/address   # client MAC
```

## Running the attack (end-to-end test)

1. **Start the stack** and confirm all three are up:

   ```sh
   make
   make ps
   ```

2. **Grab both victim MACs** (see section above), e.g.:

   ```sh
   docker exec inquisitor-ftp-server-1 cat /sys/class/net/eth0/address
   docker exec inquisitor-client-1     cat /sys/class/net/eth0/address
   ```

3. **Shell into the poison box and launch `inquisitor`.** Argument order is
   `<IP-src> <MAC-src> <IP-target> <MAC-target>` — src = ftp-server,
   target = client:

   ```sh
   docker exec -it inquisitor-poison-1 bash
   ./inquisitor 10.5.0.17 <server-MAC> 10.5.0.18 <client-MAC>
   ```

   It prints the interface info, `Packet capturing started`, then begins
   poisoning silently.

4. **Stop with Ctrl-C.** `inquisitor` sends the real MACs back (restore) and
   exits. Re-checking `ip neigh` on a victim should show the correct MAC again
   within a few seconds.

## Testing each detected FTP command

`inquisitor` prints on three client→server commands — **STOR** (upload),
**RETR** (download) and **APPE** (append). With the attack running (steps 1–4
above), open a shell on the client and trigger each one by hand; each should
produce exactly one line on the poison box.

```sh
docker exec -it inquisitor-client-1 ash
# local files live in /test (hello.txt, evaluator.key, work.md, place.hex)
```

**STOR — upload a file** (`lftp put`):

```sh
lftp -u ftpuser,ftppass 10.5.0.17 -e 'put /test/hello.txt; bye'
```
→ poison box prints: `STOR > hello.txt`

**RETR — download a file** (`lftp get`). The file must already exist on the
server, so STOR it first:

```sh
lftp -u ftpuser,ftppass 10.5.0.17 -e 'get hello.txt -o /tmp/hello.txt; bye'
```
→ poison box prints: `RETR > hello.txt`

**APPE — append to a file.** `lftp` has no APPE verb, so use `curl --append`
(install curl once with `apk add --no-cache curl`):

```sh
curl -T /test/hello.txt --append ftp://ftpuser:ftppass@10.5.0.17/hello.txt
```
→ poison box prints: `APPE > hello.txt`

## Testing the pieces individually

### FTP server

Credentials: `ftpuser` / `ftppass`. Uploads land in `/home/ftpuser` (the user's
chrooted home).

```sh
docker exec -it inquisitor-ftp-server-1 ash
ls -l /home/ftpuser        # expect hello.txt, evaluator.key, work.md, place.hex
```

### Client

The client is an idle box (`sleep infinity`) with `lftp` and the `/test` files
ready. Drive it by hand — mirror the whole `/test` dir, or open an interactive
session:

```sh
docker exec -it inquisitor-client-1 ash
lftp -u ftpuser,ftppass 10.5.0.17 -e 'mirror -R /test; ls; bye'   # one-shot upload
lftp -u ftpuser,ftppass 10.5.0.17                                 # interactive session
```

## Resources
- [PCAP++ documentation](https://pcapplusplus.github.io/api-docs/v25.05/index.html)
- [vsftpd guide](https://dev.to/sahillearninglinux/ultimate-guide-to-vsftpd-configuration-files-commands-and-secure-sftp-migration-170m)
- [lftp guide](https://commandmasters.com/commands/lftp-linux/)
