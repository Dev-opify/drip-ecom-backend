#!/bin/bash

# Go to the folder where your jar is
cd dripyard-backend/target

# Run the Spring Boot jar with dynamic port
java -jar dripyard-backend-0.0.1-SNAPSHOT.jar --server.port=$PORT
