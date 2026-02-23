#!/bin/bash

BASE_DIR="/web2/Api-Analysis"
SCRIPT_DIR="$BASE_DIR/scripts"
LOG_DIR="$SCRIPT_DIR/log"
TARGET_DIR="$BASE_DIR/target"
PID_FILE="/tmp/lead_lab_app.pid"

DATE=$(date +"%Y-%m-%d")
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"

mkdir -p "$LOG_DIR"

echo "===========================================" | tee -a "$LOG_FILE"
echo "CREATE JAR (Safe Mode) - $(date)" | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"

cd "$BASE_DIR" || {
  echo "ERROR: Project directory not found." | tee -a "$LOG_FILE"
  exit 1
}

# ---------------------------------------------------
# STEP 1: Check if server running and stop it safely
# ---------------------------------------------------

if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")

  if ps -p "$PID" -o args= 2>/dev/null | grep -q "Api-Analysis"; then
    echo "Server running (PID $PID). Stopping before build..." | tee -a "$LOG_FILE"
    kill "$PID"
    sleep 5

    if ps -p "$PID" > /dev/null 2>&1; then
      echo "Force killing PID $PID" | tee -a "$LOG_FILE"
      kill -9 "$PID"
    fi

    rm -f "$PID_FILE"
    echo "Server stopped." | tee -a "$LOG_FILE"
  else
    echo "Stale PID file found. Cleaning..." | tee -a "$LOG_FILE"
    rm -f "$PID_FILE"
  fi
else
  echo "Server not running. Proceeding with build." | tee -a "$LOG_FILE"
fi

# ---------------------------------------------------
# STEP 2: Build project
# ---------------------------------------------------

echo "Building project..." | tee -a "$LOG_FILE"

mvn package -DskipTests >> "$LOG_FILE" 2>&1

if [ $? -ne 0 ]; then
  echo "Build FAILED. Aborting." | tee -a "$LOG_FILE"
  exit 1
fi

# ---------------------------------------------------
# STEP 3: Version the new JAR
# ---------------------------------------------------

NEW_JAR=$(ls "$TARGET_DIR"/*.jar 2>/dev/null | grep -v "original" | head -n 1)

if [ -z "$NEW_JAR" ]; then
  echo "ERROR: JAR not found after build." | tee -a "$LOG_FILE"
  exit 1
fi

VERSIONED_JAR="$TARGET_DIR/Api-Analysis-$TIMESTAMP.jar"
mv "$NEW_JAR" "$VERSIONED_JAR"

echo "New JAR created: $VERSIONED_JAR" | tee -a "$LOG_FILE"

# ---------------------------------------------------
# STEP 4: Retain only last 2 versions
# ---------------------------------------------------

echo "Cleaning old JAR versions (keeping last 2)..." | tee -a "$LOG_FILE"

ls -t "$TARGET_DIR"/Api-Analysis-*.jar | tail -n +3 | while read oldjar; do
  echo "Deleting old JAR: $oldjar" | tee -a "$LOG_FILE"
  rm -f "$oldjar"
done

echo "Build complete. Only latest 2 versions retained." | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"
