# Start with a base image with Java
FROM openjdk:17-jdk-oracle

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file created by your build process into the container
COPY target/fasdoor.jar /app/fasdoor.jar

# Expose the port your app will run on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "fasdoor.jar"]
