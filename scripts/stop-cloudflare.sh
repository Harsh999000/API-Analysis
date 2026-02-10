#!/bin/bash

SCRIPT_DIR="/web2/AI-Analysis/scripts"
LOG_DIR="$SCRIPT_DIR/log"

DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"
PID_FILE="/tmp/lead_lab_cloudflare.pid"

mkdir -p "$LOG_DIR"

echo "===========================================" | tee -a "$LOG_FILE"
echo "STOP CLOUDFLARE TUNNEL - $(date)" | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"

if [ ! -f "$PID_FILE" ]; then
  echo "Cloudflare tunnel not running (no PID file)" | tee -a "$LOG_FILE"
  exit 0
fi

PID=$(cat "$PID_FILE")

if ps -p "$PID" > /dev/null 2>&1; then
  echo "Stopping Cloudflare tunnel (PID $PID)" | tee -a "$LOG_FILE"
  kill "$PID"
  sleep 3

  if ps -p "$PID" > /dev/null 2>&1; then
    echo "Force killing Cloudflare tunnel (PID $PID)" | tee -a "$LOG_FILE"
    kill -9 "$PID"
  fi

  echo "Cloudflare tunnel stopped" | tee -a "$LOG_FILE"
else
  echo "Stale Cloudflare PID file found" | tee -a "$LOG_FILE"
fi

rm -f "$PID_FILE"
