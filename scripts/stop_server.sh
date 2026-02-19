#!/bin/bash

echo ">>> Stopping existing application..."

CURRENT_PID=$(pgrep -f "java.*run-0\.0\.1-SNAPSHOT\.jar")

if [ -z "$CURRENT_PID" ]; then
  echo ">>> No running application found."
else
  echo ">>> Killing PID: $CURRENT_PID"
  kill -15 $CURRENT_PID

  # Graceful shutdown 대기 (최대 30초)
  for i in $(seq 1 30); do
    if ! ps -p $CURRENT_PID > /dev/null 2>&1; then
      echo ">>> Application stopped gracefully."
      break
    fi
    sleep 1
  done

  # 아직 살아있으면 강제 종료
  if ps -p $CURRENT_PID > /dev/null 2>&1; then
    echo ">>> Force killing PID: $CURRENT_PID"
    kill -9 $CURRENT_PID
  fi
fi