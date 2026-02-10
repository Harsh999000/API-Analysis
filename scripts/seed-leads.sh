#!/usr/bin/env bash

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$BASE_DIR/log"

API_URL="http://192.168.0.184:8110/api/leads"
DATE_TAG=$(date +"%Y%m%d")
LOG_FILE="$LOG_DIR/lead-lab-$(date +"%Y-%m-%d").log"

mkdir -p "$LOG_DIR"

log () {
  echo "[$(date +"%Y-%m-%d %H:%M:%S")] $1" | tee -a "$LOG_FILE"
}

submit_lead () {
  local USER_EMAIL=$1
  local USER_PASSWORD=$2
  local X=$3
  local REQUEST_ID
  REQUEST_ID=$(uuidgen 2>/dev/null || echo "$RANDOM-$X")

  local PAYLOAD
  PAYLOAD=$(cat <<EOF
{
  "name": "Lead Demo $DATE_TAG $X",
  "email": "lead_demo_${DATE_TAG}_${X}@demo.com",
  "source": "WEBSITE_FORM",
  "finalPage": "/pricing"
}
EOF
)

  log "REQ_ID=$REQUEST_ID USER=$USER_EMAIL LEAD_X=$X PAYLOAD=$PAYLOAD"

  RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
    -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -H "X-User-Email: $USER_EMAIL" \
    -H "X-User-Password: $USER_PASSWORD" \
    -d "$PAYLOAD")

  log "REQ_ID=$REQUEST_ID RESPONSE=$RESPONSE"
}

run_user_flow () {
  local USER_EMAIL=$1
  local USER_PASSWORD=$2

  log "START user flow: $USER_EMAIL"

  # 1️⃣ Unique submissions
  for x in $(seq 1 20); do
    submit_lead "$USER_EMAIL" "$USER_PASSWORD" "$x"
    sleep 8
  done

  # 2️⃣ Duplicate submissions
  DUP_COUNT=$((RANDOM % 5 + 1))
  log "$USER_EMAIL submitting $DUP_COUNT duplicate leads"

  for x in $(seq 1 "$DUP_COUNT"); do
    submit_lead "$USER_EMAIL" "$USER_PASSWORD" "$x"
    sleep 8
  done

  log "END user flow: $USER_EMAIL"
}

rate_limit_blast () {
  local USER_EMAIL=$1
  local USER_PASSWORD=$2

  FAIL_COUNT=$((RANDOM % 5 + 11))
  log "RATE LIMIT BLAST for $USER_EMAIL ($FAIL_COUNT calls)"

  for i in $(seq 1 "$FAIL_COUNT"); do
    submit_lead "$USER_EMAIL" "$USER_PASSWORD" "RL$i"
    sleep 1
  done

  log "END RATE LIMIT BLAST for $USER_EMAIL"
}

log "================= LEAD LAB RUN START ================="

# Async execution
run_user_flow "user1@demo.com" "user1" &
run_user_flow "user2@demo.com" "user2" &

wait

# Intentional failure storm
rate_limit_blast "user1@demo.com" "user1"

log "================= LEAD LAB RUN END ==================="
