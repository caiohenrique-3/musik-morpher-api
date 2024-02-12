# Use maven:3.8.5-openjdk-17 as a build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Use openjdk:17.0.1-jdk-slim for the application
FROM openjdk:17.0.1-jdk-slim

# Install ffmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    rm -rf /var/lib/apt/lists/*

# Copy the jar file from the build stage
COPY --from=build /target/ch7-0.0.1-SNAPSHOT.jar demo.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","demo.jar"]
