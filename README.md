# Events platform backend

## Документация

Swagger-ui доступен по пути /swagger-ui/index.html#/

## Сборка и запуск

* Готовый контейнер можно спуллить тут:
> docker push nobodyknowsdotcom/events-platform-backend:latest

* Собрать fat-jar: ./gradlew clean build
* Собрать docker контейнер: ./gradlew buildDocker