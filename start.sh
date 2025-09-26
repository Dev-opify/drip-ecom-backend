#!/bin/sh
# Make sure the jar is in the same folder as start.sh
java -jar dripyard-backend-0.0.1-SNAPSHOT.jar --server.port=$PORT
