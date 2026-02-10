#!/bin/bash

SCRIPT_DIR="/web2/Api-Analysis/scripts"
LOG_DIR="$SCRIPT_DIR/log"

TUNNEL_NAME="apianalysis"
DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"
PID_FILE="/tmp/lead_lab_cloudflare.pid"

mkdir -p "$LOG_DIR"

echo "===========================================" | tee -a "$LOG_FILE"
echo "START CLOUDFLARE NAMED TUNNEL - $(date)" | tee -a "$LOG_FILE"
echo "Tunnel: $TUNNEL_NAME" | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"

# Prevent duplicate tunnel
if [ -f "$PID_FILE" ] && ps -p "$(cat $PID_FILE)" > /dev/null 2>&1; then
  echo "Cloudflare tunnel already running (PID $(cat $PID_FILE))" | tee -a "$LOG_FILE"
  exit 0
fi

nohup cloudflared tunnel run "$TUNNEL_NAME" \
  >> "$LOG_FILE" 2>&1 &

CF_PID=$!
echo "$CF_PID" > "$PID_FILE"

echo "Cloudflare tunnel started in background (PID: $CF_PID)" | tee -a "$LOG_FILE"
echo "Public Domain: https://apianalysis.harshjha.co.in" | tee -a "$LOG_FILE"
