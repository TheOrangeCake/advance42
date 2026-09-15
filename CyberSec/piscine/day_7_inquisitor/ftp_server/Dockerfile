FROM alpine:3.14

RUN apk add --no-cache vsftpd

RUN adduser -D -h /home/ftpuser ftpuser \
    && echo "ftpuser:ftppass" | chpasswd

COPY vsftpd.conf /etc/vsftpd/vsftpd.conf

RUN mkdir -p /var/run/vsftpd/empty

CMD ["vsftpd", "/etc/vsftpd/vsftpd.conf"]
