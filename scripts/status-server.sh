#!/bin/bash

PID_FILE="/tmp/lead_lab_app.pid"

if [ ! -f "$PID_FILE" ]; then
  echo "Server status: NOT RUNNING"
  exit 0
fi

PID=$(cat "$PID_FILE")

if ps -p "$PID" -o args= | grep -q "Api-Analysis.jar"; then
  echo "Server status: RUNNING (PID $PID)"
else
  echo "Server status: NOT RUNNING (stale PID)"
fi