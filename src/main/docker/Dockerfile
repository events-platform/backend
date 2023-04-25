FROM bellsoft/liberica-openjre-alpine:17 as jre-build

ARG DB_USERNAME
ENV DB_USERNAME=$DB_USERNAME

ARG DB_PASSWORD
ENV DB_PASSWORD=$DB_PASSWORD

ARG DB_HOST
ENV DB_HOST=$DB_HOST

ARG DB_PORT
ENV DB_PORT=$DB_PORT

ARG DB_NAME
ENV DB_NAME=$DB_NAME

COPY build/libs/*.jar /app/app.jar
WORKDIR /app

COPY src/main/resources/templates /app/templates

EXPOSE 8000
ENTRYPOINT exec java -jar -Dspring.profiles.active=prod /app/app.jar \
--sprind.datasource.username=$DB_USERNAME --spring.datasource.password=$DB_PASSWORD