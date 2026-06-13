FROM nginx:alpine

RUN apk add --no-cache openssh \
 && ssh-keygen -A

COPY index.html /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
COPY sshd_config /etc/ssh/sshd_config
COPY start.sh /start.sh

RUN adduser -D hoannguy \
 && echo "hoannguy:42Lausanne" | chpasswd

RUN chmod +x /start.sh

EXPOSE 80 4242

CMD ["/start.sh"]