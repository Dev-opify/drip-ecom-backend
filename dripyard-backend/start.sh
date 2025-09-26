#!/bin/sh
set -e

echo "Building application..."
./mvnw clean package -DskipTests

echo "Starting application..."
java -jar target/dripyard-backend-0.0.1-SNAPSHOT.jar
