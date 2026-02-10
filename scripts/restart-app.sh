#!/bin/bash

APP_NAME="Lead-Submission-Observability-Lab"
PROFILE="local"

# Absolute path to project root
PROJECT_DIR="/web2/Api-Analysis"

echo "=============================="
echo "Restarting $APP_NAME"
echo "Time: $(date)"
echo "=============================="

# Move to project root
cd "$PROJECT_DIR" || {
  echo "ERROR: Project directory not found!"
  exit 1
}

# Stop existing Spring Boot (Maven) process
echo "Stopping existing application..."
pkill -f "mvn spring-boot:run" || true

sleep 3

# Pull latest code
echo "Pulling latest code from GitHub..."
git pull origin main

# Start application again
echo "Starting application..."
nohup mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE >/dev/null 2>&1 &

sleep 2

echo "Application restart command executed successfully"
echo "Cloudflare tunnel is NOT affected"
