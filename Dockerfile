FROM eclipse-temurin:17-jdk-alpine

# Use the PORT environment variable provided by Railway, defaulting to 8080
ENV PORT=${PORT:-8080}

WORKDIR /app

COPY dripyard-backend/.mvn/ .mvn
COPY dripyard-backend/mvnw dripyard-backend/mvnw.cmd ./
COPY dripyard-backend/pom.xml ./
COPY dripyard-backend/src ./src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE $PORT

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q --spider http://localhost:${PORT}/actuator/health || exit 1

CMD ["java", "-jar", "target/dripyard-backend-0.0.1-SNAPSHOT.jar"]