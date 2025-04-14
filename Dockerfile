# ---- Builder stage ----
FROM maven:3.8.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Now copy the rest of the project
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# ---- Final (Runtime) image ----
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
