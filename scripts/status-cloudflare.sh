#!/bin/bash

SCRIPT_DIR="/web2/Api-Analysis/scripts"
LOG_DIR="$SCRIPT_DIR/log"

TUNNEL_NAME="apianalysis"
PID_FILE="/tmp/lead_lab_cloudflare.pid"

DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"

mkdir -p "$LOG_DIR"

echo "===========================================" | tee -a "$LOG_FILE"
echo "STATUS CLOUDFLARE TUNNEL - $(date)" | tee -a "$LOG_FILE"
echo "Tunnel: $TUNNEL_NAME" | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"

# No PID file
if [ ! -f "$PID_FILE" ]; then
  echo "Cloudflare status: NOT RUNNING (no PID file)" | tee -a "$LOG_FILE"
  exit 0
fi

PID=$(cat "$PID_FILE")

# Verify PID exists and belongs to THIS tunnel
if ps -p "$PID" -o args= 2>/dev/null | grep -q "cloudflared tunnel run $TUNNEL_NAME"; then
  echo "Cloudflare status: RUNNING (PID $PID)" | tee -a "$LOG_FILE"
  echo "Public Domain: https://apianalysis.harshjha.co.in" | tee -a "$LOG_FILE"
else
  echo "Cloudflare status: NOT RUNNING (stale or unrelated PID)" | tee -a "$LOG_FILE"
fi