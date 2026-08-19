# --- Stage 1: Build Stage ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml và tải dependencies trước để tận dụng cache của Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ mã nguồn vào container
COPY src ./src

# Thực hiện build dự án và bỏ qua các test cases để tiết kiệm thời gian deploy
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Copy file jar được sinh ra từ Build Stage
COPY --from=build /app/target/*.jar app.jar

# Khai báo cổng mà ứng dụng lắng nghe (mặc định Spring Boot là 8080)
EXPOSE 8080

# Cấu hình môi trường mặc định là production khi deploy
ENV SPRING_PROFILES_ACTIVE=prod

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
