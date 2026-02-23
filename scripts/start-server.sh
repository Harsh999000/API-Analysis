#!/bin/bash

BASE_DIR="/web2/Api-Analysis"
SCRIPT_DIR="$BASE_DIR/scripts"
LOG_DIR="$SCRIPT_DIR/log"
JAR_FILE="$BASE_DIR/target/Api-Analysis.jar"
PROFILE="local"

PID_FILE="/tmp/lead_lab_app.pid"
DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"

mkdir -p "$LOG_DIR"

echo "======================================" | tee -a "$LOG_FILE"
echo "START SERVER - $(date)" | tee -a "$LOG_FILE"
echo "======================================" | tee -a "$LOG_FILE"

if [ -f "$PID_FILE" ] && ps -p "$(cat $PID_FILE)" > /dev/null 2>&1; then
  echo "Server already running (PID $(cat $PID_FILE))" | tee -a "$LOG_FILE"
  exit 0
fi

if [ ! -f "$JAR_FILE" ]; then
  echo "ERROR: JAR file not found. Build first." | tee -a "$LOG_FILE"
  exit 1
fi

nohup java -Xms128m -Xmx384m -XX:+UseG1GC \
  -jar "$JAR_FILE" \
  --spring.profiles.active="$PROFILE" \
  >> "$LOG_FILE" 2>&1 &

APP_PID=$!
echo "$APP_PID" > "$PID_FILE"

echo "Server started (PID $APP_PID)" | tee -a "$LOG_FILE"