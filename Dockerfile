FROM ubuntu:latest
ARG NATIE_FILE=build/native/nativeCompile/catalog-service
COPY ${NATIE_FILE} catalog-service
ENTRYPOINT [ "/bin/sh", "-l", "-c", "--static", "./catalog-service"]