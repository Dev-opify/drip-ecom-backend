#!/bin/bash
cd dripyard-backend

# Build the project (skip tests to save time)
./mvnw clean package -DskipTests

# List target folder to check jar
ls -l target/

# Run Spring Boot app
java -jar target/dripyard-backend-0.0.1-SNAPSHOT.jar
