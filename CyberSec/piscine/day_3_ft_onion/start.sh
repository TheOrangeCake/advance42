#!/bin/sh

echo "Starting SSH..."
/usr/sbin/sshd -D &

echo "Starting Tor..."
su -s /bin/sh tor -c "tor -f /etc/tor/torrc" &

echo "Waiting for the .onion hostname..."
while [ ! -f /var/lib/tor/hidden_service/hostname ]; do
	sleep 1
done
cp /var/lib/tor/hidden_service/hostname /home/hoannguy/onion_address
chown hoannguy:hoannguy /home/hoannguy/onion_address
echo "Onion address: $(cat /home/hoannguy/onion_address)"

echo "Starting NGINX..."
exec nginx -g "daemon off;"
