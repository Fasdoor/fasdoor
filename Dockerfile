# Start with a base image with Java
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file created by your build process into the container
COPY target/onestopper.jar /app/onestopper.jar

# Expose the port your app will run on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "onestopper.jar"]
