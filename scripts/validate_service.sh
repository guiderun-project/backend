#!/bin/bash

DEPLOY_DIR=/home/ubuntu/deploy
LOG_DIR=$DEPLOY_DIR/logs
JAR_PATTERN='java.*run-0\.0\.1-SNAPSHOT\.jar'
MAX_WAIT=90

echo ">>> Validating service..."

dump_diagnostics() {
  echo ""
  echo ">>> ================= 진단 정보 ================="

  local pids
  pids=$(pgrep -f "$JAR_PATTERN" | tr '\n' ' ')
  if [ -n "$pids" ]; then
    echo ">>> JVM 프로세스: 살아있음 (PID $pids)"
  else
    echo ">>> JVM 프로세스: 종료됨"
  fi

  echo ""
  echo ">>> --- 로그 파일 목록 (갱신 시각으로 이전 배포 로그와 구분) ---"
  ls -l "$DEPLOY_DIR/nohup.out" "$LOG_DIR" 2>&1

  echo ""
  echo ">>> --- tail -80 $LOG_DIR/guiderun-error.log ---"
  tail -80 "$LOG_DIR/guiderun-error.log" 2>&1

  echo ""
  echo ">>> --- tail -40 $DEPLOY_DIR/nohup.out ---"
  tail -40 "$DEPLOY_DIR/nohup.out" 2>&1

  echo ""
  echo ">>> --- tail -40 $LOG_DIR/guiderun-app.log ---"
  tail -40 "$LOG_DIR/guiderun-app.log" 2>&1

  echo ">>> =============================================="
}

for i in $(seq 1 $MAX_WAIT); do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080 2>/dev/null)
  if [ -n "$STATUS" ] && [ "$STATUS" != "000" ]; then
    echo ">>> Application is running! (HTTP $STATUS)"
    exit 0
  fi

  if [ "$i" -ge 3 ] && ! pgrep -f "$JAR_PATTERN" > /dev/null 2>&1; then
    echo ">>> ERROR: JVM process exited after ${i}s (application failed to boot)"
    dump_diagnostics
    exit 1
  fi

  echo ">>> Waiting for application to start... ($i/$MAX_WAIT)"
  sleep 1
done

echo ">>> ERROR: Application failed to start within ${MAX_WAIT} seconds"
dump_diagnostics
exit 1
