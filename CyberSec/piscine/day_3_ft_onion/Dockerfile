FROM nginx:alpine

RUN apk add --no-cache openssh tor && ssh-keygen -A

COPY index.html /usr/share/nginx/html
COPY images /usr/share/nginx/html/images
COPY nginx.conf /etc/nginx/nginx.conf
COPY sshd_config /etc/ssh/sshd_config
COPY torrc /etc/tor/torrc
COPY start.sh /start.sh

RUN adduser -D hoannguy && echo "hoannguy:42Lausanne" | chpasswd

RUN mkdir -p /var/lib/tor/hidden_service \
    && chown -R tor:tor /var/lib/tor \
    && chmod 700 /var/lib/tor/hidden_service

RUN chmod +x /start.sh

CMD ["/start.sh"]