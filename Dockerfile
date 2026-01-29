FROM node:20-alpine AS frontend-builder

WORKDIR /frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# Используем официальный образ с JDK для сборки
FROM eclipse-temurin:21-jdk-alpine AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем gradle wrapper и конфигурационные файлы
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY versions.properties .
COPY gradle/libs.versions.toml gradle/

# Даем права на выполнение gradlew
RUN chmod +x gradlew

# Загружаем зависимости (кэшируется если файлы не изменились)
RUN ./gradlew dependencies --no-daemon

# Копируем исходный код
COPY src src

# Добавляем собранную фронтенд статику в resources перед сборкой jar
RUN rm -rf src/main/resources/static && mkdir -p src/main/resources/static
COPY --from=frontend-builder /frontend/dist/ src/main/resources/static/

# Запускаем тесты
RUN ./gradlew test --no-daemon

# Собираем приложение
RUN ./gradlew build -x test --no-daemon

# Финальный образ для запуска
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем собранный jar из builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Кладем фронтенд-артефакты в image (их будет вытаскивать Ansible на хост для nginx)
COPY --from=frontend-builder /frontend/dist/ /app/frontend-dist/

# Открываем порты
EXPOSE 8080 9090

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
