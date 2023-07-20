# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/libs/MOTDify-1.0.3.jar /app/MOTDify.jar
COPY build/libs/lib-1.0.3 /app/lib-1.0.3

CMD ["java", "-jar", "MOTDify.jar"]

