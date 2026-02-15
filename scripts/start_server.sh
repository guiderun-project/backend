#!/bin/bash

DEPLOY_DIR=/home/ubuntu/deploy
JAR_FILE=$(ls -t $DEPLOY_DIR/*.jar 2>/dev/null | head -1)

if [ -z "$JAR_FILE" ]; then
  echo ">>> ERROR: No JAR file found in $DEPLOY_DIR"
  exit 1
fi

echo ">>> Starting application: $JAR_FILE"

# 환경변수 로드 (MAIN_RDS, REDIS_DOMAIN 등)
source /home/ubuntu/.env

# 각 EC2에 미리 만들어둔 .deploy_profile에서 프로필 읽기
# main-1: prod / main-2: batch
PROFILE_FILE=/home/ubuntu/.deploy_profile
if [ -f "$PROFILE_FILE" ]; then
  PROFILE=$(cat $PROFILE_FILE | tr -d '[:space:]')
else
  PROFILE="prod"
fi

echo ">>> Using profile: $PROFILE"

nohup java \
  -Dspring.profiles.active=$PROFILE \
  -Duser.timezone=Asia/Seoul \
  -jar $JAR_FILE > $DEPLOY_DIR/nohup.out 2>&1 &

echo ">>> Application started with PID: $!"