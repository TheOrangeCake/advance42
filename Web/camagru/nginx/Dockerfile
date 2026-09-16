FROM alpine:3.24

RUN apk upgrade --no-cache && apk add nginx

COPY nginx/nginx.conf /etc/nginx/http.d/default.conf
COPY src /src

EXPOSE 80

CMD ["/usr/sbin/nginx", "-g", "daemon off;"]
