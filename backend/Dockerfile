FROM amazoncorretto:21-alpine

RUN apk update && apk add curl

WORKDIR /app

# Copy the JAR file from the build stage
COPY ./target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 80

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
