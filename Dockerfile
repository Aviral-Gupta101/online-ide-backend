FROM amazoncorretto:17-alpine3.18-jdk
WORKDIR /app
COPY . .
RUN ./gradlew build
EXPOSE 8080
ENTRYPOINT ["/app/gradlew", "bootRun"]