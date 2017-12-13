#!/bin/sh
exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.config.location=/app/etc/ -Dspring.profiles.active=docker -jar /app/bin/main.jar