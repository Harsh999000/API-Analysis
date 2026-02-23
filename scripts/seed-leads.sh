#!/usr/bin/env bash

# ---------------------------------------------------
# ENV (cron-safe)
# ---------------------------------------------------
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$BASE_DIR/log"

API_URL="http://192.168.0.183:8110/api/leads"
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
  "email": "lead_demo_${DATE_TAG}_${USER_EMAIL//[@.]/}_${X}@demo.com",
  "source": "WEBSITE_FORM",
  "finalPage": "/pricing"
}
EOF
)

  log "REQ_ID=$REQUEST_ID USER=$USER_EMAIL LEAD_X=$X"

  RESPONSE=$(curl --max-time 30 -s -w "\nHTTP_STATUS:%{http_code}\n" \
    -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -H "X-User-Email: $USER_EMAIL" \
    -H "X-User-Password: $USER_PASSWORD" \
    -d "$PAYLOAD")

  if [ $? -ne 0 ]; then
    log "REQ_ID=$REQUEST_ID ERROR: curl failed"
    return
  fi

  log "REQ_ID=$REQUEST_ID RESPONSE=$RESPONSE"
}

run_user_flow () {
  local USER_EMAIL=$1
  local USER_PASSWORD=$2

  log "START user flow: $USER_EMAIL"

  # Unique submissions: 40–80
  SUCCESS_COUNT=$((RANDOM % 40 + 40))
  log "$USER_EMAIL will submit $SUCCESS_COUNT unique leads"

  for x in $(seq 1 "$SUCCESS_COUNT"); do
    submit_lead "$USER_EMAIL" "$USER_PASSWORD" "$x"
    sleep $((RANDOM % 4 + 6))
  done

  # Duplicate submissions: 10–30
  DUP_COUNT=$((RANDOM % 30 + 10))
  log "$USER_EMAIL submitting $DUP_COUNT duplicate leads"

  for x in $(seq 1 "$DUP_COUNT"); do
    RAND_DUP=$((RANDOM % SUCCESS_COUNT + 1))
    submit_lead "$USER_EMAIL" "$USER_PASSWORD" "$RAND_DUP"
    sleep $((RANDOM % 4 + 6))
  done

  log "END user flow: $USER_EMAIL"
}

rate_limit_blast () {
  local USER_EMAIL=$1
  local USER_PASSWORD=$2

  FAIL_COUNT=$((RANDOM % 20 + 5))
  log "RATE LIMIT BLAST for $USER_EMAIL ($FAIL_COUNT calls)"

  for i in $(seq 1 "$FAIL_COUNT"); do
    submit_lead "$USER_EMAIL" "$USER_PASSWORD" "RL$i"
    sleep $((RANDOM % 3 + 1))
  done

  log "END RATE LIMIT BLAST for $USER_EMAIL"
}

log "================= LEAD LAB RUN START ================="

# Parallel user execution
run_user_flow "user1@demo.com" "user1" &
run_user_flow "user2@demo.com" "user2" &

wait

# Intentional burst after normal flow
rate_limit_blast "user1@demo.com" "user1"

log "================= LEAD LAB RUN END ==================="
