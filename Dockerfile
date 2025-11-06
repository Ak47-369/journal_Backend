# --- Stage 1: The "Build" Stage ---
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /workspace

COPY . .

# Make mvnw executable and build
RUN chmod +x mvnw
RUN ./mvnw package -DskipTests

# --- Stage 2: The "Run" Stage ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy only the built jar from the builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# Expose your app port
EXPOSE 8082

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]