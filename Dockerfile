FROM ubuntu:latest
LABEL authors="montel"

ENTRYPOINT ["top", "-b"]