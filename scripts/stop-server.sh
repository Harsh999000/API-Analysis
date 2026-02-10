#!/bin/bash

echo = "===================================================="
echo = "===================================================="

SCRIPT_DIR="/web2/Lead-Submission-Observability-Lab/scripts"
LOG_DIR="$SCRIPT_DIR/log"

DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"
PID_FILE="/tmp/lead_lab_app.pid"

mkdir -p "$LOG_DIR"

{
  echo "======================================"
  echo "STOP SERVER - $(date)"
  echo "======================================"
} >> "$LOG_FILE"

if [ ! -f "$PID_FILE" ]; then
  echo "Server not running (PID file missing)" >> "$LOG_FILE"
  echo "Server not running (PID file missing)"
  exit 0
fi

PID=$(cat "$PID_FILE")

if ps -p "$PID" > /dev/null 2>&1; then
  echo "Stopping server PID $PID" >> "$LOG_FILE"
  echo "Stopping server PID $PID"
  kill "$PID"
  sleep 5

  if ps -p "$PID" > /dev/null 2>&1; then
    echo "Force killing PID $PID" >> "$LOG_FILE"
    echo "Force killing PID $PID"
    kill -9 "$PID"
  fi

  echo "Server stopped" >> "$LOG_FILE"
  echo "Server stopped"
  echo "========================================"
  echo "========================================"
else
  echo "Stale PID file found ($PID)" >> "$LOG_FILE"
  echo "Stale PID file found ($PID)"
  echo "=========================================="
  echo "=========================================="
fi

rm -f "$PID_FILE"
