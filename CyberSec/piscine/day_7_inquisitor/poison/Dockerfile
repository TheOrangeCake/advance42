FROM ubuntu:22.04

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
