#!/bin/bash

# Step 3a: Build the project
./mvnw clean package -DskipTests

# Step 3b: Run the Spring Boot jar
java -jar target/dripyard-backend-0.0.1-SNAPSHOT.jar