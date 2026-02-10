#!/bin/bash

echo "============================================"
echo "============================================"

SCRIPT_DIR="/web2/Lead-Submission-Observability-Lab/scripts"
LOG_DIR="$SCRIPT_DIR/log"
DATE=$(date +"%Y-%m-%d")
LOG_FILE="$LOG_DIR/lead-lab-$DATE.log"

mkdir -p "$LOG_DIR"

echo "===========================================" | tee -a "$LOG_FILE"
echo "STATUS ALL SERVICES - $(date)" | tee -a "$LOG_FILE"
echo "===========================================" | tee -a "$LOG_FILE"

echo "--- Server Status ---" | tee -a "$LOG_FILE"
"$SCRIPT_DIR/status-server.sh"

echo "--- Cloudflare Tunnel Status ---" | tee -a "$LOG_FILE"
"$SCRIPT_DIR/status-cloudflare.sh"

echo "=============================================="
echo "=============================================="
