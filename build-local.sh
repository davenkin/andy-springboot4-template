#!/bin/bash

# First stop docker to have a clean docker environment
#./stop-docker-compose.sh

# Then start a fresh docker environment
./start-docker-compose.sh

# Run the build
./mvnw clean package
./mvnw clean package -Dspring.profiles.active=local-it



