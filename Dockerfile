# Use OpenJDK 17 as the base image
FROM eclipse-temurin:17-jdk

# Set working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/doctor-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that Spring Boot runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
