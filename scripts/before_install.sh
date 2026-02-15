#!/bin/bash

DEPLOY_DIR=/home/ubuntu/deploy

if [ -d "$DEPLOY_DIR" ]; then
  rm -f $DEPLOY_DIR/*.jar
  rm -rf $DEPLOY_DIR/scripts
fi

mkdir -p $DEPLOY_DIR