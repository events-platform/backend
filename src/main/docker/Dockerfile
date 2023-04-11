FROM bellsoft/liberica-openjre-alpine:17 as jre-build

COPY build/libs/*.jar /app/app.jar
WORKDIR /app

COPY src/main/resources/templates /app/templates

EXPOSE 8000
ENTRYPOINT [ "java", "-jar", "-Dspring.profiles.active=test", "/app/app.jar" ]