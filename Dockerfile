# -------- Build Stage --------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# -------- Runtime Stage --------
FROM eclipse-temurin:21-jre

LABEL maintainer="Akram Shaik"

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Railway provides the PORT environment variable
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]