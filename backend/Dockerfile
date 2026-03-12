# ---------- Stage 1: Build ----------
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom first (for caching dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests


# ---------- Stage 2: Run ----------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy generated jar (use wildcard so version changes don't break)
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]