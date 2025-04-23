# Build stage
FROM gradle:8.4-jdk21 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build -x test

# Production stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/bookstore-*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]