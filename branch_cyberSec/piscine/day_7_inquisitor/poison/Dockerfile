FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

# apt lists are kept (not purged) because the Makefile's `install` target runs
# its own `apt-get install libpcap-dev cmake git`, which needs a populated cache.
RUN apt-get update && apt-get install -y --no-install-recommends \
        sudo ca-certificates \
        g++ make pkg-config \
        iproute2 iputils-ping

WORKDIR /poison

COPY header header
COPY src src
COPY Makefile Makefile

RUN make install && make

CMD ["sleep", "infinity"]
