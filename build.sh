#!/bin/bash

# First stop docker to have a clean docker environment
#./stop-docker-compose.sh

# Then start a fresh docker environment
./start-docker-compose.sh

# Run the build
./mvnw clean package



