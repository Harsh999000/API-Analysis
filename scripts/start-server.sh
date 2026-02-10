#!/bin/bash

BASE_DIR="/web2/Lead-Submission-Observability-Lab"
SCRIPT_DIR="$BASE_DIR/scripts"
LOG_DIR="$SCRIPT_DIR/log"

PROFILE="local"
DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"
PID_FILE="/tmp/lead_lab_app.pid"

mkdir -p "$LOG_DIR"

{
  echo "======================================"
  echo "START SERVER - $(date)"
  echo "Profile: $PROFILE"
  echo "======================================"
} >> "$LOG_FILE"

cd "$BASE_DIR" || {
  echo "ERROR: App directory not found" >> "$LOG_FILE"
  exit 1
}

nohup mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE \
  >> "$LOG_FILE" 2>&1 &

APP_PID=$!
echo $APP_PID > "$PID_FILE"

echo "Server started with PID $APP_PID" >> "$LOG_FILE"
echo "==============================================="
echo "==============================================="
