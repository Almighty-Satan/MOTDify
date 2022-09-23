# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/libs/MOTDify-1.0.0.jar /app/
COPY build/libs/lib-1.0.0 /app/lib-1.0.0

CMD ["java", "-jar", "MOTDify-1.0.0.jar"]

