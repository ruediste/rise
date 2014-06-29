# Base ubuntu image, used by other containers
# Build: docker build -t my_ubuntu .

FROM ubuntu:14.04
MAINTAINER Ruedi Steinmann <ruediste@gmail.com>

# RUN  echo 'Acquire::http { Proxy "http://dockerhost:3142"; };' >> /etc/apt/apt.conf.d/01proxy
RUN apt-get update ; apt-get install -y mc