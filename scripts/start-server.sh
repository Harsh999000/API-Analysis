#!/bin/bash

BASE_DIR="/web2/Api-Analysis"
SCRIPT_DIR="$BASE_DIR/scripts"
LOG_DIR="$SCRIPT_DIR/log"
PROFILE="local"

PID_FILE="/tmp/lead_lab_app.pid"
DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"

mkdir -p "$LOG_DIR"

echo "======================================" | tee -a "$LOG_FILE"
echo "START SERVER - $(date)" | tee -a "$LOG_FILE"
echo "======================================" | tee -a "$LOG_FILE"

# ---------------------------------------------------
# STEP 1: Check if already running
# ---------------------------------------------------

if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")

  if ps -p "$PID" -o args= 2>/dev/null | grep -q "Api-Analysis-"; then
    echo "Server already running (PID $PID)" | tee -a "$LOG_FILE"
    exit 0
  else
    echo "Stale PID file found. Cleaning..." | tee -a "$LOG_FILE"
    rm -f "$PID_FILE"
  fi
fi

# ---------------------------------------------------
# STEP 2: Detect latest versioned JAR
# ---------------------------------------------------

LATEST_JAR=$(ls -t "$BASE_DIR"/target/Api-Analysis-*.jar 2>/dev/null | head -n 1)

if [ -z "$LATEST_JAR" ]; then
  echo "ERROR: No versioned JAR found. Build first." | tee -a "$LOG_FILE"
  exit 1
fi

echo "Starting JAR: $LATEST_JAR" | tee -a "$LOG_FILE"

# ---------------------------------------------------
# STEP 3: Start application (Heap Optimized)
# ---------------------------------------------------

nohup java -Xms128m -Xmx256m -XX:+UseG1GC \
  -jar "$LATEST_JAR" \
  --spring.profiles.active="$PROFILE" \
  >> "$LOG_FILE" 2>&1 &

APP_PID=$!
echo "$APP_PID" > "$PID_FILE"

sleep 2

if ps -p "$APP_PID" > /dev/null 2>&1; then
  echo "Server started successfully (PID $APP_PID)" | tee -a "$LOG_FILE"
  exit 0
else
  echo "ERROR: Server failed to start." | tee -a "$LOG_FILE"
  rm -f "$PID_FILE"
  exit 1
fi
