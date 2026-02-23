#!/bin/bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"
BASE="/web2/Api-Analysis"
LOG_DIR="$BASE/logs"
LOG_DATE="$(date +%F)"
CRONLOG_FILE="$LOG_DIR/cron-$LOG_DATE.log"

mkdir -p "$LOG_DIR"

log() {
  {
    echo "--------------------------------------------------"
    echo "[ $(date '+%Y-%m-%d %H:%M:%S') ] [$SCRIPT_NAME]"
    echo "$@"
  } >> "$CRONLOG_FILE"
}

log "Starting log cleanup (retention: 7 days)"

find "$LOG_DIR" -type f -mtime +7 -print -delete >> "$CRONLOG_FILE" 2>&1

log "Log cleanup completed successfully"
