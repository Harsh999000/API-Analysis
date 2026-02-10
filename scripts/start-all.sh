#!/bin/bash

echo "======================================================="
echo "======================================================="

SCRIPT_DIR="/web2/AI-Analysis/scripts"
LOG_DIR="$SCRIPT_DIR/log"
DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"

mkdir -p "$LOG_DIR"

echo "===========================================" | tee -a "$LOG_FILE"
echo "START ALL SERVICES - $(date)" | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"

echo "Starting server..." | tee -a "$LOG_FILE"
"$SCRIPT_DIR/start-server.sh"

# Give server time to bind port
sleep 20

echo "Starting Cloudflare tunnel..." | tee -a "$LOG_FILE"
"$SCRIPT_DIR/start-cloudflare.sh"

echo "All services started." | tee -a "$LOG_FILE"
echo "==============================================="
echo "==============================================="
