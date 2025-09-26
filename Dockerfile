FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY dripyard-backend/.mvn/ .mvn
COPY dripyard-backend/mvnw dripyard-backend/mvnw.cmd ./
COPY dripyard-backend/pom.xml ./
COPY dripyard-backend/src ./src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 5454

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q --spider http://localhost:5454/actuator/health || exit 1

CMD ["java", "-jar", "target/dripyard-backend-0.0.1-SNAPSHOT.jar"]
