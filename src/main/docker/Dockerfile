FROM eclipse-temurin:17-jdk-alpine as jre-build

RUN apk add --no-cache binutils
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /customjre

FROM alpine:latest
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=jre-build /customjre $JAVA_HOME
COPY build/libs/*.jar /app/app.jar
WORKDIR /app

EXPOSE 8000
ENTRYPOINT [ "/jre/bin/java", "-jar", "-Dspring.profiles.active=test", "/app/app.jar" ]