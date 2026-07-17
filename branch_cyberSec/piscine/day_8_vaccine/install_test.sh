# !bin/sh

git clone https://github.com/cr0hn/vulnerable-node.git vulnerable-node
cd vulnerable-node/
docker compose build && docker compose up
