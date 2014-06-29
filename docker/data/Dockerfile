# container exposing a data directory which is typically mapped to the host
# and backed up. Used by the other containers to store their data.
# Build: docker build -t data .
# Run: docker run -d --name data-run data

FROM my_ubuntu
MAINTAINER Ruedi Steinmann <ruediste@gmail.com>

VOLUME      ["/data"]