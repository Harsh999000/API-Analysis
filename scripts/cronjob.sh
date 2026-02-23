#!/bin/bash
set -euo pipefail

# ==================================================
# Lead Lab Nightly Orchestrator
# Executes maintenance steps sequentially.
# Stops immediately if any step fails.
# ==================================================

SCRIPT_NAME="$(basename "$0")"
BASE="/web2/Api-Analysis"
SCRIPT_DIR="$BASE/scripts"
LOG_DIR="$BASE/logs"
LOG_DATE="$(date +%F)"
CRONLOG_FILE="$LOG_DIR/cron-$LOG_DATE.log"

mkdir -p "$LOG_DIR"

log() {
  {
    echo "=================================================="
    echo "[ $(date '+%Y-%m-%d %H:%M:%S') ] [$SCRIPT_NAME]"
    echo "$@"
  } | tee -a "$CRONLOG_FILE"
}

log "Starting Lead Lab full lifecycle"

# --------------------------------------------------
# 1. Seed Leads (Traffic Simulation)
# --------------------------------------------------
"$SCRIPT_DIR/seed-leads.sh" &&
log "Lead seeding completed"

# --------------------------------------------------
# 2. Delete Old Logs (Retention: 7 days)
# --------------------------------------------------
"$SCRIPT_DIR/delete-logs.sh" &&
log "Log cleanup completed"

log "Lead Lab lifecycle completed successfully"
