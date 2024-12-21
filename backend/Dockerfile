FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY ./target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
