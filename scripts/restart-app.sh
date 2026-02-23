#!/bin/bash

PROJECT_DIR="/web2/Api-Analysis"

cd "$PROJECT_DIR" || exit 1

echo "Pulling latest code..."
git pull origin main

echo "Building project..."
mvn clean package -DskipTests

echo "Restarting server..."
/web2/Api-Analysis/scripts/stop-server.sh
sleep 3
/web2/Api-Analysis/scripts/start-server.sh