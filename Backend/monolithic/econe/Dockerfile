# Step 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cache layer)
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline

# Copy project source
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# Step 2: Run the application using a small JDK image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built JAR from previous stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]