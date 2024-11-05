FROM openjdk:21-jdk-slim as builder
WORKDIR /app
COPY . .
RUN ./gradlew :api:clean :api:build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/api/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "nickname-api.jar"]