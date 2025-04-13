# Use Maven + JDK image
FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app

# Copy all project files
COPY . .

# Build the JAR
RUN mvn clean package -DskipTests

# ---- Production image ----
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Copy the built JAR from the builder image
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
