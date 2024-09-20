FROM ubuntu:latest
LABEL authors="wtc"

ENTRYPOINT ["top", "-b"]