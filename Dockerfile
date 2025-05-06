# -------- Build Stage --------
    FROM eclipse-temurin:21-jdk AS build

    WORKDIR /app
    
    # Copy project files into the container
    COPY . .
    
    # Build the project with Maven
    RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests
    
    # -------- Run Stage --------
    FROM eclipse-temurin:21-jdk
    
    WORKDIR /app
    
    # Copy the built jar file from the build stage
    COPY --from=build /app/target/Lazy-Hotel-0.0.1-SNAPSHOT.jar app.jar
    
    # Expose the port on which the app will run (default Spring Boot port)
    EXPOSE 8080
    
    # Set the entrypoint to run the Spring Boot application
    ENTRYPOINT ["java", "-jar", "app.jar"]
    