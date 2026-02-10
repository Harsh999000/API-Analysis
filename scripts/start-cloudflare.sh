#!/bin/bash

SCRIPT_DIR="/web2/Api-Analysis/scripts"
LOG_DIR="$SCRIPT_DIR/log"

PORT=8110
DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"
PID_FILE="/tmp/lead_lab_cloudflare.pid"

mkdir -p "$LOG_DIR"

echo "===========================================" | tee -a "$LOG_FILE"
echo "START CLOUDFLARE QUICK TUNNEL - $(date)" | tee -a "$LOG_FILE"
echo "Port: $PORT" | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"

nohup cloudflared tunnel --url http://localhost:$PORT \
  >> "$LOG_FILE" 2>&1 &

CF_PID=$!
echo "$CF_PID" > "$PID_FILE"

echo "Cloudflare tunnel started in background (PID: $CF_PID)" | tee -a "$LOG_FILE"

# Wait briefly for URL to appear
sleep 5

CF_URL=$(grep -o 'https://[^ ]*trycloudflare.com' "$LOG_FILE" | tail -n 1)

if [ -n "$CF_URL" ]; then
  echo "Cloudflare public URL:" | tee -a "$LOG_FILE"
  echo "$CF_URL" | tee -a "$LOG_FILE"
else
  echo "WARNING: Could not detect Cloudflare URL yet (check log)" | tee -a "$LOG_FILE"
fi
