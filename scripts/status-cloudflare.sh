#!/bin/bash

SCRIPT_DIR="/web2/Api-Analysis/scripts"
LOG_DIR="$SCRIPT_DIR/log"

DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"
PID_FILE="/tmp/lead_lab_cloudflare.pid"

mkdir -p "$LOG_DIR"

echo "===========================================" | tee -a "$LOG_FILE"
echo "STATUS CLOUDFLARE TUNNEL - $(date)" | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"

if [ ! -f "$PID_FILE" ]; then
  echo "Cloudflare status: NOT RUNNING (no PID file)" | tee -a "$LOG_FILE"
  exit 0
fi

PID=$(cat "$PID_FILE")

if ps -p "$PID" > /dev/null 2>&1; then
  echo "Cloudflare status: RUNNING (PID $PID)" | tee -a "$LOG_FILE"
  CF_URL=$(grep -o 'https://[^ ]*trycloudflare.com' "$LOG_FILE" | tail -n 1)
  if [ -n "$CF_URL" ]; then
    echo "Public URL: $CF_URL" | tee -a "$LOG_FILE"
  else
    echo "Public URL: Not yet detected (check logs)" | tee -a "$LOG_FILE"
  fi
else
  echo "Cloudflare status: NOT RUNNING (stale PID file)" | tee -a "$LOG_FILE"
fi
