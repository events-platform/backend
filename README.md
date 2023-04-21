# Events platform backend

## Документация

Swagger-ui доступен по пути /swagger-ui/index.html#/

## Сборка и запуск

* Для запуска docker-compose: 
`cd src/main/docker && docker-compose pull && docker-compose --env-file <путь до env файла> up`

* Собрать fat-jar: ./gradlew clean build
* Собрать docker контейнер: ./gradlew buildDocker