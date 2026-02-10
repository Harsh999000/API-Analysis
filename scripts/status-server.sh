#!/bin/bash

SCRIPT_DIR="/web2/AI-Analysis/scripts"
LOG_DIR="$SCRIPT_DIR/log"

DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"
PID_FILE="/tmp/lead_lab_app.pid"

mkdir -p "$LOG_DIR"

{
  echo "======================================"
  echo "STATUS CHECK - $(date)"
  echo "======================================"
} >> "$LOG_FILE"

if [ ! -f "$PID_FILE" ]; then
  echo "STATUS: NOT RUNNING" >> "$LOG_FILE"
  echo "Server status: NOT RUNNING"
  exit 0
fi

PID=$(cat "$PID_FILE")

if ps -p "$PID" > /dev/null 2>&1; then
  echo "STATUS: RUNNING (PID $PID)" >> "$LOG_FILE"
  echo "Server status: RUNNING (PID $PID)"
else
  echo "STATUS: NOT RUNNING (stale PID $PID)" >> "$LOG_FILE"
  echo "Server status: NOT RUNNING (stale PID)"
  echo "===================================================="
  echo "===================================================="
fi
