# --- Stage 1: The "Build" Stage ---
# This stage uses a full JDK to build our app
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /workspace
COPY journalApp .
RUN chmod +x mvnw
RUN ./mvnw package -DskipTests

# --- Stage 2: The "Run" Stage ---
# This stage uses a much smaller JRE-only image,
# making our final container more secure and lightweight.
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy *only* the built .jar file from the "builder" stage
COPY --from=builder /workspace/target/*.jar app.jar

# Expose the port the app runs on (it gets this from the config server)
EXPOSE 8082

# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]