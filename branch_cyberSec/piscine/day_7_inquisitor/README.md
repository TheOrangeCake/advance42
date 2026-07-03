# Run
- Start
```sh
make
```
- Get in client
```sh
docker container exec -it [client] ash
lftp -u ftpuser,ftppass 10.5.0.17
```

- Get in server
```sh
docker container exec -it [server] ash
// file uploaded is in home/user
```

- Get in poison
```sh
docker container exec -it [poison] ash
./inquisitor <IP-src> <MAC-src> <IP-target> <MAC-target>
```

# Ressources
- [PCAP++ documentation](https://pcapplusplus.github.io/api-docs/v25.05/index.html)
- [vsftpd guide](https://dev.to/sahillearninglinux/ultimate-guide-to-vsftpd-configuration-files-commands-and-secure-sftp-migration-170m)
- [lftp guide](https://commandmasters.com/commands/lftp-linux/)