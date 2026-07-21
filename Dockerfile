FROM eclipse-temurin:21-jdk

LABEL maintainer="Akram Shaik"

WORKDIR /app

COPY target/cinebase-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]