FROM gradle:9-jdk-21-and-24 AS builder

WORKDIR /app
COPY . .

RUN gradle --no-daemon :frontend:webCopyFrontend
RUN gradle --no-daemon :backend:bootJar

FROM eclipse-temurin:21-alpine

RUN addgroup -g 1000 app
RUN adduser -G app -D -u 1000 -h /app app
USER app:app

WORKDIR /app
COPY --from=builder --chown=app:app /app/backend/build/libs/*.jar app.jar

ENV SERVER_PORT=8080
ENV APP_DB_HOST=postgres-app
ENV APP_DB_PORT=5432
ENV KEYCLOAK_HOST=keycloak
ENV KEYCLOAK_PORT=8080
ENV REDIS_HOST=redis
ENV REDIS_PORT=6379
ENV RABBIT_MQ_HOST=rabbitmq
ENV RABBIT_MQ_PORT=5672

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]
