#!/bin/bash

TUNNEL_NAME="apianalysis"
PID_FILE="/tmp/lead_lab_cloudflare.pid"

if [ -f "$PID_FILE" ] && ps -p "$(cat $PID_FILE)" > /dev/null 2>&1; then
  echo "Cloudflare tunnel already running (PID $(cat $PID_FILE))"
  exit 0
fi

nohup cloudflared tunnel run "$TUNNEL_NAME" > /dev/null 2>&1 &

echo $! > "$PID_FILE"

echo "Cloudflare tunnel started (PID $!)"