#!/bin/bash

echo "========================================================"
echo "========================================================"

SCRIPT_DIR="/web2/Lead-Submission-Observability-Lab/scripts"
LOG_DIR="$SCRIPT_DIR/log"
DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"

mkdir -p "$LOG_DIR"

echo "===========================================" | tee -a "$LOG_FILE"
echo "STOP ALL SERVICES - $(date)" | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"

echo "Stopping Cloudflare tunnel..." | tee -a "$LOG_FILE"
"$SCRIPT_DIR/stop-cloudflare.sh"

sleep 5

echo "Stopping server..." | tee -a "$LOG_FILE"
"$SCRIPT_DIR/stop-server.sh"

echo "All services stopped." | tee -a "$LOG_FILE"
echo "==========================================================="
echo "==========================================================="
