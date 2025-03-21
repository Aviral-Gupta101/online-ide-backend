
########## OLD DOCKERFILE ##########
#FROM amazoncorretto:17-alpine3.18-jdk
#WORKDIR /app
#COPY . .
#RUN ./gradlew build
#EXPOSE 8080
#ENTRYPOINT ["/app/gradlew", "bootRun"]


########## LATEST DOCKERFILE ##########
FROM amazoncorretto:17-alpine3.18-jdk
WORKDIR /app

# Copy only Gradle files to cache dependencies
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Download dependencies first
RUN ./gradlew dependencies || ./gradlew dependencies --refresh-dependencies

# Copy the rest of the application source code
COPY . .

# Build the Spring Boot application
RUN ./gradlew bootJar

# Expose port
EXPOSE 8080

# Run the application using the built JAR
CMD ["java", "-jar", "/app/build/libs/online_compiler-0.0.1-SNAPSHOT.jar"]
