#!/bin/bash

PID_FILE="/tmp/lead_lab_cloudflare.pid"

if [ ! -f "$PID_FILE" ]; then
  echo "Cloudflare tunnel not running"
  exit 0
fi

PID=$(cat "$PID_FILE")

if ps -p "$PID" -o args= | grep -q "cloudflared tunnel run apianalysis"; then
  echo "Stopping Cloudflare tunnel (PID $PID)"
  kill "$PID"
else
  echo "PID does not belong to this tunnel"
fi

rm -f "$PID_FILE"