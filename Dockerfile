# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/MOTDify-1.1.0.jar /app/MOTDify.jar
COPY build/libs/lib-1.1.0 /app/lib-1.1.0

CMD ["java", "-jar", "MOTDify.jar"]

