# ------------ Build Stage ------------
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy toàn bộ mã nguồn vào container
COPY . .

# Build project và tạo file JAR
RUN mvn clean package -DskipTests

# ------------ Runtime Stage ------------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy file JAR từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (hoặc port bạn sử dụng)
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-jar", "app.jar"]
