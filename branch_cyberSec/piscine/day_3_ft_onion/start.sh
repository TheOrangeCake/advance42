#!/bin/sh

echo "Starting SSH..."
/usr/sbin/sshd -D &

echo "Starting NGINX..."
exec nginx -g "daemon off;"