#!/bin/bash

PID_FILE="/tmp/lead_lab_app.pid"

if [ ! -f "$PID_FILE" ]; then
  echo "Server not running (no PID file)"
  exit 0
fi

PID=$(cat "$PID_FILE")

# Verify this PID belongs to THIS jar
if ps -p "$PID" -o args= | grep -q "Api-Analysis.jar"; then
  echo "Stopping server (PID $PID)"
  kill "$PID"
  sleep 5

  if ps -p "$PID" > /dev/null 2>&1; then
    echo "Force killing (PID $PID)"
    kill -9 "$PID"
  fi

  echo "Server stopped"
else
  echo "PID does not belong to this project. Not touching."
fi

rm -f "$PID_FILE"