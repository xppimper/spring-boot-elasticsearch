FROM eclipse-temurin:21-jdk-alpine

RUN addgroup -S app && adduser -S app -G app

USER app

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]