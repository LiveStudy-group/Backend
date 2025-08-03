# Step 1: 빌드
FROM gradle:8.14.3-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Step 2: 실행
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]