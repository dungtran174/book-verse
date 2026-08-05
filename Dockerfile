# ============================================
# BookVerse - Multi-stage Dockerfile
# ============================================

# --- Stage 1: Build ---
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies trước để tận dụng Docker cache
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Tạo user non-root cho bảo mật
RUN addgroup -S bookverse && adduser -S bookverse -G bookverse

# Copy JAR từ builder stage
COPY --from=builder /app/target/*.jar app.jar

# Tạo thư mục upload
RUN mkdir -p /app/uploads && chown -R bookverse:bookverse /app

USER bookverse

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget -q --spider http://localhost:8080/api-docs || exit 1

# Entrypoint
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
