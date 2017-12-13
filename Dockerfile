FROM debian:stretch AS ytdl
RUN apt-get update
RUN apt-get install --yes --no-install-recommends ca-certificates wget
RUN wget https://yt-dl.org/downloads/latest/youtube-dl -O youtube-dl
RUN chmod a+x youtube-dl

FROM openjdk:8-jre-slim
RUN set -ex \
    && export DEBIAN_FRONTEND=noninteractive \
    && apt-get update \
    && apt-get install --yes --no-install-recommends python ffmpeg \
    ; \
    apt-get autoremove --yes \
    && rm -rf /var/lib/apt/* /var/cache/apt/* /var/tmp/* /tmp/*

COPY --from=ytdl youtube-dl /usr/local/bin/youtube-dl
ADD build/libs/mytube-dl.jar /app/bin/main.jar
ADD src/docker/start.sh /app/bin/start.sh

VOLUME /app/etc
VOLUME /app/var
EXPOSE 8080

CMD [ "/app/bin/start.sh" ]
