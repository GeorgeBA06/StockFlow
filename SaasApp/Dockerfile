# Этап сборки: Maven с JDK 23
FROM maven:3.9-eclipse-temurin-23 AS builder

WORKDIR /build

# Копируем исходники
COPY client/ ./client/
COPY common/ ./common/
COPY server/ ./server/
COPY pom.xml ./

# Собираем серверный модуль (пропускаем тесты для скорости)
RUN mvn clean package -pl server -am -DskipTests

# Финальный образ: JRE 23
FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

# Копируем JAR и конфигурацию
COPY --from=builder /build/server/target/server-*.jar ./app.jar
COPY server/src/main/resources/server.properties ./server.properties

# Открываем порт
EXPOSE 12345

# Запуск
CMD ["java", "-jar", "app.jar"]