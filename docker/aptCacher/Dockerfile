#
# see https://docs.docker.com/examples/apt-cacher-ng/
#
# Build: docker build -t aptCacher .
# Run: docker run -d -p 3142:3142 --name apt-cacher apt-cacher
# 
# Use: include in docker files: RUN  echo 'Acquire::http { Proxy "http://dockerhost:3142"; };' >> /etc/apt/apt.conf.d/01proxy
# ==> This is alredy part of myUbuntu
#

FROM ubuntu:14.04
MAINTAINER Ruedi Steinmann <ruediste@gmail.com>

VOLUME      ["/var/cache/apt-cacher-ng"]
RUN     apt-get update ; apt-get install -yq apt-cacher-ng

EXPOSE      3142
CMD     chmod 777 /var/cache/apt-cacher-ng ; /etc/init.d/apt-cacher-ng start ; tail -f /var/log/apt-cacher-ng/*
