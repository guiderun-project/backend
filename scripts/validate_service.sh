#!/bin/bash

echo ">>> Validating service..."

# 최대 90초 대기하면서 health check
for i in $(seq 1 90); do
  # 포트가 열려있는지만 확인 (401도 서버가 정상 기동된 것)
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080 2>/dev/null)
  if [ -n "$STATUS" ] && [ "$STATUS" != "000" ]; then
    echo ">>> Application is running! (HTTP $STATUS)"
    exit 0
  fi
  echo ">>> Waiting for application to start... ($i/90)"
  sleep 1
done

echo ">>> ERROR: Application failed to start within 90 seconds"
exit 1