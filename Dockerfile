FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*-all.jar /app/server.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/server.jar"]