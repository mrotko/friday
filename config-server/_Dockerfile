# Stage 1: Cache Maven dependencies
FROM maven:3.8-amazoncorretto-21 AS dependencies
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Stage 2: Build the application with Maven
FROM maven:3.8-amazoncorretto-21 AS build
WORKDIR /app

# Copy the cached .m2 repository
COPY --from=dependencies /root/.m2 /root/.m2
# Copy the source code and build the application
COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests


# Stage 3: Create the final image to run the application
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


