# ===== BUILD =====
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY producer ./producer
COPY common-lib ./common-lib
COPY single-message-consumer ./single-message-consumer
COPY batch-message-consumer ./batch-message-consumer

RUN mvn clean package -DskipTests

# ===== RUNTIME =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/producer/target/producer-*.jar producer.jar
COPY --from=build /app/single-message-consumer/target/single-message-consumer-*.jar single-message-consumer.jar
COPY --from=build /app/batch-message-consumer/target/batch-message-consumer-*.jar batch-message-consumer.jar