# Use OpenJDK as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file
COPY backend/build/libs/backend.jar backend.jar

# Ensure the JAR is executable
RUN chmod +x backend.jar

# Expose backend port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
