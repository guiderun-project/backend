#!/bin/bash

echo ">>> Validating service..."

# 최대 60초 대기하면서 health check
for i in $(seq 1 60); do
  if curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo ">>> Application is running!"
    exit 0
  fi
  echo ">>> Waiting for application to start... ($i/60)"
  sleep 1
done

echo ">>> ERROR: Application failed to start within 60 seconds"
exit 1